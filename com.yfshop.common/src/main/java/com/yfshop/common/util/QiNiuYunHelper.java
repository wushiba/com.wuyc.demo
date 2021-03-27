package com.yfshop.common.util;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.BatchStatus;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FetchRet;
import com.qiniu.util.Auth;
import com.yfshop.common.exception.ApiException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.time.DateUtils;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 七牛云操作助手
 *
 * @author Xulg
 * Created in 2021-03-27 11:16
 */
@Data
@NotThreadSafe
public class QiNiuYunHelper {

    /**
     * 上传凭证默认有效时间(秒)
     */
    private static final int DEFAULT_EXPIRES = 3600;

    /**
     * 上传凭证提前几分钟过期
     */
    private static final int AHEAD_MINUTES = 5;

    private final Configuration defaultCfg = new Configuration(Region.huadong());
    private final String bucketName;
    private final String accessKey;
    private final String secretKey;
    private final Auth auth;

    private String domainName;
    private UploadToken uploadToken;
    private UploadManager uploadManager;
    private BucketManager bucketManager;

    private QiNiuYunHelper(String accessKey, String secretKey, String bucketName) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.bucketName = bucketName;
        this.auth = Auth.create(accessKey, secretKey);
    }

    public static QiNiuYunHelperBuilder builder() {
        return new QiNiuYunHelperBuilder();
    }

    /* *****上传凭证***** */

    /**
     * 获取3600秒有效期的上传凭证
     *
     * @return the upload token
     */
    public String createUploadToken() {
        return createUploadToken(null);
    }

    /**
     * 获取3600秒有效期的上传凭证
     * 如果fileKey已经存在则覆写文件
     *
     * @param overrideFileKey 覆写的fileKey
     * @return the upload token
     */
    public String createUploadToken(String overrideFileKey) {
        return createUploadToken(overrideFileKey, DEFAULT_EXPIRES, TimeUnit.SECONDS);
    }

    /**
     * 获取指定过期时间的上传凭证
     *
     * @param expires  过期时间
     * @param timeUnit 时间单位
     * @return the upload token
     */
    public String createUploadToken(long expires, TimeUnit timeUnit) {
        return createUploadToken(null, expires, timeUnit);
    }

    /**
     * 获取指定过期时间的上传凭证
     *
     * @param overrideFileKey 覆写的fileKey
     * @param expires         过期时间
     * @param timeUnit        时间单位
     * @return the upload token
     */
    public String createUploadToken(String overrideFileKey, long expires, TimeUnit timeUnit) {
        // 有效时间(秒)
        long expiresInSeconds = TimeUnit.SECONDS.convert(expires, timeUnit);
        String token = auth.uploadToken(bucketName, overrideFileKey, expiresInSeconds, null);
        // 缓存这个token
        Date expireTime = this.calcExpireTime(expires, timeUnit);
        this.uploadToken = new UploadToken(overrideFileKey, token, expireTime);
        // 返回token
        return token;
    }

    /* *****上传文件***** */

    /**
     * 上传文件
     *
     * @param url      资源连接url
     * @param filepath 文件路径
     * @return 文件访问url
     */
    public UploadResult upload(String url, String filepath, boolean isOverride) {
        InputStream in;
        try {
            in = URLUtil.url(url).openStream();
        } catch (IOException e) {
            throw new ApiException(500, "打开流" + url + "失败" + e.getMessage());
        }
        return upload(in, filepath, isOverride);
    }

    /**
     * 上传文件
     *
     * @param file     文件
     * @param filepath 文件路径
     * @return 文件访问url
     */
    public UploadResult upload(File file, String filepath, boolean isOverride) {
        return upload(FileUtil.readBytes(file), filepath, isOverride);
    }

    /**
     * 上传文件
     *
     * @param in       资源流
     * @param filepath 文件路径
     * @return 文件访问url
     */
    public UploadResult upload(InputStream in, String filepath, boolean isOverride) {
        return upload(IoUtil.readBytes(in), filepath, isOverride);
    }

    /**
     * 上传文件
     *
     * @param fileBytes  文件字节内容
     * @param filepath   文件路径
     * @param isOverride 如果重名是否覆写
     * @return 文件访问url
     */
    public UploadResult upload(byte[] fileBytes, String filepath, boolean isOverride) {
        try {
            filepath = resolvePath(filepath);
            String token = this.getUploadToken(isOverride ? filepath : null);
            Response response = getUploadManager().put(fileBytes, filepath, token);
            if (!response.isOK()) {
                String msg = "upload failed status_code=" + response.statusCode + "&error=" + response.error;
                throw new ApiException(500, msg);
            }
            DefaultPutRet result = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            // 文件hash值
            String hash = result.hash;
            // 文件名
            String key = result.key;
            // 文件访问路径
            String url = "http://" + getDomainName() + "/" + key;
            return UploadResult.builder().success(true).msg("SUCCESS").key(key).hash(hash).url(url).build();
        } catch (QiniuException e) {
            String msg = "upload failed status_code=" + e.response.statusCode
                    + "&error=" + e.response.error;
            throw new ApiException(500, msg);
        }
    }

    /**
     * 抓取资源存入七牛云空间
     *
     * @param resourceUrl the resource url
     * @param fileKey     the file key
     */
    public UploadResult fetchUrl(String resourceUrl, String fileKey) {
        try {
            FetchRet fetchRet = getBucketManager().fetch(resourceUrl, bucketName, fileKey);
            // 文件访问路径
            String url = "http://" + getDomainName() + "/" + fetchRet.key;
            UploadResult uploadResult = new UploadResult();
            uploadResult.setSuccess(true);
            uploadResult.setMsg("SUCCESS");
            uploadResult.setKey(fetchRet.key);
            uploadResult.setHash(fetchRet.hash);
            uploadResult.setUrl(url);
            return uploadResult;
        } catch (QiniuException e) {
            // 抓取失败
            String msg = "status_code=" + e.response.statusCode
                    + "&error=" + e.response.error;
            throw new ApiException(500, msg);
        }
    }

    /* *****下载文件***** */

    /**
     * 获取文件的下载链接
     *
     * @param publicUrl 七牛云的访问链接
     * @param expires   下载链接的有效期
     * @param timeUnit  有效期时间单位
     */
    public String downloadUrl(String publicUrl, long expires, TimeUnit timeUnit) {
        return downloadUrl0(publicUrl, expires, timeUnit, this.getAuth());
    }

    /* *删除文件* */

    /**
     * 删除文件
     *
     * @param fileKey the file key
     * @return true if delete success
     */
    public boolean delete(String fileKey) {
        return delete0(getBucketManager(), bucketName, fileKey);
    }

    /**
     * 批量删除文件
     *
     * @param fileKeys the file keys
     * @return the count of delete success
     */
    public int batchDelete(List<String> fileKeys) {
        BucketManager.BatchOperations operations = new BucketManager.BatchOperations()
                .addDeleteOp(bucketName, fileKeys.toArray(new String[0]));
        try {
            Response response = getBucketManager().batch(operations);
            BatchStatus[] batchStatuses = response.jsonToObject(BatchStatus[].class);
            int count = 0;
            for (BatchStatus status : batchStatuses) {
                if (status.code == 0) {
                    count++;
                }
            }
            return count;
        } catch (QiniuException e) {
            throw new ApiException(500, e.getMessage());
        }
    }

    /* *管理* */

    /**
     * 查询账户下所有的空间名称
     *
     * @return the bucket names
     */
    public List<String> queryBucketNames() {
        return queryBucketNames0(this.getBucketManager());
    }

    /**
     * 查询存储空间下的域名
     *
     * @return 域名列表
     */
    public List<String> queryDomainNames() {
        try {
            String[] domains = getBucketManager().domainList(bucketName);
            return new ArrayList<>(Arrays.asList(domains));
        } catch (QiniuException e) {
            throw new ApiException(500, "获取账号下所有空间名称列表失败" + e.getMessage());
        }
    }

    /**
     * 查询存储空间中的第一个域名
     *
     * @return 域名
     */
    @Nullable
    public String queryFirstDomainName() {
        return IterUtil.getFirst(queryDomainNames());
    }

    /**
     * 文件列表搜索，一次最多1000个文件
     *
     * @param fileKeyPrefix 文件名前缀
     */
    public List<FileInfo> searchFile(String fileKeyPrefix) {
        return searchFile(fileKeyPrefix, null, 100);
    }

    /**
     * 文件列表搜索，一次最多1000个文件
     *
     * @param fileKeyPrefix      文件名前缀
     * @param directoryDelimiter 目录分隔符
     */
    public List<FileInfo> searchFile(String fileKeyPrefix, String directoryDelimiter, int iterSizeLimit) {
        iterSizeLimit = Math.min(iterSizeLimit, 1000);
        // 创建文件迭代器并读取
        BucketManager.FileListIterator fileListIterator = getBucketManager()
                .createFileListIterator(bucketName, fileKeyPrefix, iterSizeLimit, directoryDelimiter);
        List<FileInfo> list = new ArrayList<>();
        while (fileListIterator.hasNext()) {
            com.qiniu.storage.model.FileInfo[] items = fileListIterator.next();
            for (com.qiniu.storage.model.FileInfo item : items) {
                FileInfo fileInfo = FileInfo.convert(getDomainName(), item);
                list.add(fileInfo);
            }
        }
        return list;
    }

    /**
     * 处理文件
     *
     * @param fileKeyPrefix 文件名前缀
     * @param consumer      处理逻辑
     */
    public void processFile(String fileKeyPrefix, Consumer<FileInfo> consumer) {
        List<FileInfo> list = searchFile(fileKeyPrefix);
        for (FileInfo fileInfo : list) {
            consumer.accept(fileInfo);
        }
    }

    /* ************************************************************************************************************** */

    public String getBucketName() {
        return bucketName;
    }

    public Auth getAuth() {
        return auth;
    }

    public UploadManager getUploadManager() {
        if (uploadManager == null) {
            uploadManager = new UploadManager(defaultCfg);
        }
        return uploadManager;
    }

    public BucketManager getBucketManager() {
        if (bucketManager == null) {
            bucketManager = new BucketManager(auth, defaultCfg);
        }
        return bucketManager;
    }

    public String getDomainName() {
        if (domainName == null) {
            domainName = queryFirstDomainName();
        }
        return domainName;
    }

    /* ************************************************************************************************************** */

    /**
     * 获取上传token，过期则刷新
     *
     * @return the upload token
     */
    private String getUploadToken(String overrideFileKey) {
        if (this.uploadToken == null || this.uploadToken.isExpired()
                || !Objects.equals(this.uploadToken.overrideFileKey, overrideFileKey)) {
            this.createUploadToken(overrideFileKey, DEFAULT_EXPIRES, TimeUnit.SECONDS);
        }
        return this.uploadToken.token;
    }

    /**
     * 计算上传凭证的过期时间
     */
    private Date calcExpireTime(long expires, TimeUnit timeUnit) {
        long expiresInMinutes = TimeUnit.MINUTES.convert(expires, timeUnit);
        return DateUtils.addMinutes(new Date(), (int) (expiresInMinutes - AHEAD_MINUTES));
    }

    /**
     * 去除路径首尾的"/"
     *
     * @param filepath the file path
     * @return the file path
     */
    private static String resolvePath(String filepath) {
        if (filepath.startsWith("/")) {
            filepath = filepath.substring(filepath.indexOf("/") + 1);
        }
        if (filepath.endsWith("/")) {
            filepath = filepath.substring(0, filepath.lastIndexOf("/"));
        }
        return filepath;
    }

    private static List<String> queryBucketNames0(BucketManager bucketManager) {
        try {
            String[] buckets = bucketManager.buckets();
            return new ArrayList<>(Arrays.asList(buckets));
        } catch (QiniuException e) {
            throw new ApiException(500, "获取账号下所有空间名称列表失败" + e.getMessage());
        }
    }

    private static String downloadUrl0(String publicUrl, long expires, TimeUnit timeUnit, Auth auth) {
        // 有效期(秒)
        long expiresInSeconds = TimeUnit.SECONDS.convert(expires, timeUnit);
        return auth.privateDownloadUrl(publicUrl, expiresInSeconds);
    }

    private static boolean delete0(BucketManager bucketManager, String bucketName, String fileKey) {
        try {
            Response response = bucketManager.delete(bucketName, fileKey);
            return response.isOK()/* || (response.statusCode == 612)*/;
        } catch (QiniuException e) {
            throw new ApiException(500, e.getMessage());
        }
    }

    /* ************************************************************************************************************** */

    @AllArgsConstructor
    private static class UploadToken {
        private final String overrideFileKey;
        private final String token;
        private final Date expireTime;

        boolean isExpired() {
            return expireTime.before(new Date());
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UploadResult implements Serializable {
        private static final long serialVersionUID = 1L;
        private boolean success;
        private String msg;
        private String key;
        private String hash;
        private String url;

        public UploadResult(boolean success, String msg) {
            this.success = success;
            this.msg = msg;
        }
    }

    @Data
    @Accessors(chain = true)
    public static class FileInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        /**
         * 文件名
         */
        private String key;
        /**
         * 文件hash值
         */
        private String hash;
        /**
         * 文件大小，单位：字节
         */
        private long fileSize;
        /**
         * 文件上传时间
         */
        private Date putTime;
        /**
         * 文件的mimeType
         */
        private String mimeType;
        /**
         * 文件上传时设置的endUser
         */
        private String endUser;
        /**
         * 文件的存储类型，0为普通存储，1为低频存储
         */
        private int type;
        /**
         * 文件的状态，0表示启用，1表示禁用
         */
        private int status;
        /**
         * 文件的md5值
         */
        private String md5;
        /**
         * 域名
         */
        private String domainName;

        public String url() {
            return "http://" + domainName + "/" + key;
        }

        public static FileInfo convert(String domainName, com.qiniu.storage.model.FileInfo fileInfo) {
            long timestamp = TimeUnit.MILLISECONDS.convert(fileInfo.putTime, TimeUnit.NANOSECONDS);
            return new FileInfo().setKey(fileInfo.key).setHash(fileInfo.hash)
                    .setFileSize(fileInfo.fsize).setPutTime(new Date(timestamp))
                    .setMimeType(fileInfo.mimeType).setEndUser(fileInfo.endUser)
                    .setType(fileInfo.type).setStatus(fileInfo.status).setMd5(fileInfo.md5)
                    .setDomainName(domainName);
        }
    }

    public static class QiNiuYunHelperBuilder {
        private String bucketName;
        private String accessKey;
        private String secretKey;

        private QiNiuYunHelperBuilder() {
        }

        public QiNiuYunHelperBuilder bucketName(String bucketName) {
            this.bucketName = bucketName;
            return this;
        }

        public QiNiuYunHelperBuilder accessKey(String accessKey) {
            this.accessKey = accessKey;
            return this;
        }

        public QiNiuYunHelperBuilder secretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        public QiNiuYunHelper build() {
            return new QiNiuYunHelper(accessKey, secretKey, bucketName);
        }
    }

    public static void main(String[] args) {
        QiNiuYunHelper helper = QiNiuYunHelper.builder().accessKey("").secretKey("").bucketName("").build();
        helper.upload("", "", true);
    }
}
