package com.yfshop.admin.tool.poster.kernal.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Date;

@Component
public class OssDownloader {

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


    public String privateDownloadUrl(String baseUrl, long expires) {
        // 设置URL过期时间为1小时
        String kye = baseUrl.substring(baseUrl.lastIndexOf("/") + 1);
        Date expiration = new Date(System.currentTimeMillis() + expires * 1000);
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(config.getBucket(), kye);
        generatePresignedUrlRequest.setExpiration(expiration);
        URL url = ossClient.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString() + "&attname=";
    }

}
