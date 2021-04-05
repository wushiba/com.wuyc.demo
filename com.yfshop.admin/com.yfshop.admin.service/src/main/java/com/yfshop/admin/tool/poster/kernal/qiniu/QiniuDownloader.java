package com.yfshop.admin.tool.poster.kernal.qiniu;

import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QiniuDownloader {

    @Autowired
    QiniuConfig config;


    Auth auth;

    @Autowired
    public void setAuth() {
        this.auth = Auth.create(config.getAccess(), config.getSecret());
    }

    public Auth getAuth() {
        return auth;
    }


    public String privateDownloadUrl(String baseUrl, long expires) {

        return auth.privateDownloadUrl(baseUrl,expires)+"&attname=";
    }
}
