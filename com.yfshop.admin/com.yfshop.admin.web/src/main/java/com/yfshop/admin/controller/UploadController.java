package com.yfshop.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Xulg
 * Created in 2021-04-01 15:27
 */
@ApiIgnore
@Controller
@RequestMapping("admin/upload/token")
public class UploadController {
    @Autowired
    OssConfig ossConfig;

    @RequestMapping(value = "/createUploadToken", method = {RequestMethod.GET, RequestMethod.POST})
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

}
