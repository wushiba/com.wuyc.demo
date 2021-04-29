package com.yfshop.admin.tool.poster.kernal.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.PutObjectResult;
import com.yfshop.admin.tool.poster.contracts.Uploader;
import com.yfshop.admin.tool.poster.kernal.UploadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Component
public class OssUploader implements Uploader {

    @Autowired
    OssConfig config;

    OSS ossClient;


    public OSS getOssClient() {
        return ossClient;
    }

    @Autowired
    public void setOssClient() {
        this.ossClient = new OSSClientBuilder().build(config.getEndpoint(), config.getAccess(), config.getSecret());
    }

    @Override
    public UploadResult upload(File file) throws IOException {
//        if (ArrayUtil.containsAny(SpringUtil.getActiveProfiles(), "dev")) {
//            return new UploadResult("");
//        }
        String filepath = DigestUtils.md5DigestAsHex(new FileInputStream(file));
        PutObjectResult result = ossClient.putObject(config.getBucket(), filepath, file);
        return new UploadResult("http://" + config.getDomain() + "/" + filepath);
    }


    public UploadResult upload(File file, String name) throws IOException {
//        if (ArrayUtil.containsAny(SpringUtil.getActiveProfiles(), "dev")) {
//            return new UploadResult("");
//        }
        String filepath = name;
        PutObjectResult result = ossClient.putObject(config.getBucket(), filepath, file);
        return new UploadResult("http://" + config.getDomain() + "/" + filepath);
    }

    public  static void main(String args[]){
        OSS ossClient = new OSSClientBuilder().build("oss-cn-shenzhen.aliyuncs.com", "LTAI5tSnQKpNY8rGRDYBH8es", "h9jlsJITV88Yaey82tF9qHSQo9RiiE");
        try {
            ObjectListing objectListing = ossClient.listObjects(new ListObjectsRequest("yf-oss-prev"));
//                    .withMaxKeys(num).withPrefix(filePath));
            List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
            for (OSSObjectSummary s : sums) {
                System.out.println("\t" + s.getKey());
            }
        } catch (Exception e) {
           e.printStackTrace();
        }

//        PutObjectResult result = ossClient.putObject("yf-oss-prev", "e60936d641c0ae14b22a9588555b11c0.txt", new File("F:\\temp\\e60936d641c0ae14b22a9588555b11c0.txt"));

    }



}
