package com.yfshop.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.util.QiNiuYunHelper;
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
@RequestMapping("upload/token")
public class UploadController {

//    qiniu:
//    domain: qqjyvi051.hn-bkt.clouddn.com
//    bucket: yufanshop
//    access: DSM_E9p8v4nikrb0W6ovHkzxH_2uRtz8c5-nx8y6
//    secret: QFrGQnxFdOzz-iNKQmilWQGdnrzy-3ScVGYGVQG7
//    prefix: /yufanshop

    @RequestMapping(value = "/createUploadToken", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<String> createUploadToken() {
        QiNiuYunHelper helper = QiNiuYunHelper.builder().accessKey("DSM_E9p8v4nikrb0W6ovHkzxH_2uRtz8c5-nx8y6")
                .secretKey("QFrGQnxFdOzz-iNKQmilWQGdnrzy-3ScVGYGVQG7").bucketName("yufanopen").build();
        return CommonResult.success(helper.createUploadToken());
    }

}
