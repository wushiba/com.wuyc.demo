package com.yfshop.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.date.DateUtil;
import com.aliyun.oss.ClientException;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.yfshop.admin.oss.OssConfig;
import com.yfshop.admin.oss.StsSecurityTokenEntity;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.api.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Xulg
 * Created in 2021-04-01 15:27
 */
@ApiIgnore
@Controller
@RequestMapping("admin/upload/")
public class UploadController {
    private static Logger logger = LoggerFactory.getLogger(UploadController.class);
    @Value("${upload.server.domain}")
    private String host;
    @Value("${upload.server.imagePath}")
    private String imagePath;
    @Autowired
    OssConfig ossConfig;

    @RequestMapping(value = "/token/createUploadToken", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<StsSecurityTokenEntity> createUploadToken() {
        StsSecurityTokenEntity stsSecurityTokenEntity = new StsSecurityTokenEntity();
        String roleArn = "acs:ram::1058504988352672:role/ramosstmp";//todo
        String roleSessionName = "stsUploadRoleSession";//自定义
        try {
            String policy = "{\n" +
                    "    \"Statement\": [\n" +
                    "        {\n" +
                    "            \"Action\": [\n" +
                    "                \"oss:PutObject\"\n" +
                    "            ],\n" +
                    "            \"Effect\": \"Allow\",\n" +
                    "            \"Resource\": [\n" +
                    "                \"acs:oss:*:*:yf-oss-prev-open\",\n" +
                    "                \"acs:oss:*:*:yf-oss-prev-open/*\"\n" +
                    "            ]\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"Version\": \"1\"\n" +
                    "}";
            // 添加endpoint（直接使用STS endpoint，前两个参数留空，无需添加region ID）
            DefaultProfile.addEndpoint("", "", "Sts", "sts.cn-shenzhen.aliyuncs.com");
            // 构造default profile（参数留空，无需添加region ID）
            IClientProfile profile = DefaultProfile.getProfile("", ossConfig.getAccess(), ossConfig.getSecret());
            // 用profile构造client
            DefaultAcsClient client = new DefaultAcsClient(profile);
            final AssumeRoleRequest request = new AssumeRoleRequest();
            request.setMethod(MethodType.POST);
            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
            //request.setPolicy(policy); // 若policy为空，则用户将获得该角色下所有权限
            request.setDurationSeconds(60 * 15L); // 设置凭证有效时间，我设置了30分钟，单位是秒
            final AssumeRoleResponse response = client.getAcsResponse(request);
            stsSecurityTokenEntity.setExpiration(response.getCredentials().getExpiration());
            stsSecurityTokenEntity.setAccessKeyId(response.getCredentials().getAccessKeyId());
            stsSecurityTokenEntity.setAccessKeySecret(response.getCredentials().getAccessKeySecret());
            stsSecurityTokenEntity.setSecurityToken(response.getCredentials().getSecurityToken());
            stsSecurityTokenEntity.setRequestId(response.getRequestId());
            stsSecurityTokenEntity.setBucketName(ossConfig.getBucket());
            stsSecurityTokenEntity.setRegion(ossConfig.getEndpoint());
            return CommonResult.success(stsSecurityTokenEntity);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.failed("获取令牌失败");
        }

    }

    @RequestMapping("/image")
    @ResponseBody
//    @SaCheckLogin
    @CrossOrigin
    public CommonResult uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        logger.info("======================================进入上传文件uploadImage");
        if (file.isEmpty()) {
            return CommonResult.failed(ResultCode.FAILED, "请选择文件!");
        }
        String dest = UUID.randomUUID().toString().replace("-", "") + ".jpg";
        String date = DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
        String dirStr = imagePath + File.separator + date;
        File dir = new File(dirStr);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdir();
        }
        String name = dir + File.separator + dest;
        File newFile = new File(name);
        file.transferTo(newFile);
        String url = host + "/image/yf-shop/" + date + "/" + dest;
        return CommonResult.success(url);
    }

}
