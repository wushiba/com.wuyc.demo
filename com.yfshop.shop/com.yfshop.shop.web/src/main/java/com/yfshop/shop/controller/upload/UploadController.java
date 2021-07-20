package com.yfshop.shop.controller.upload;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.date.DateUtil;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.api.ResultCode;
import com.yfshop.common.log.IgnoreLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author wuyc
 * Created in 2021-04-14 17:00
 */
@RequestMapping("/front/upload")
@Controller
public class UploadController {

    private static Logger logger = LoggerFactory.getLogger(UploadController.class);

    @Value("${upload.server.domain}")
    private String host;

    @Value("${upload.server.imagePath}")
    private String imagePath;

    @IgnoreLog
    @RequestMapping("/image")
    @ResponseBody
    @SaCheckLogin
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
