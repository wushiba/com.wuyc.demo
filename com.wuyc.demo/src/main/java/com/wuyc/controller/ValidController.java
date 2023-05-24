package com.wuyc.controller;

import com.alibaba.fastjson.JSON;
import com.wuyc.vo.Result;
import com.wuyc.vo.StudentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @author sp0313
 * @date 2022年11月28日 09:38:00
 */
@RestController
public class ValidController {

    @Value("${config.url}")
    public String url;

    @RequestMapping("/test")
    public String test() throws IOException {
        System.out.println(JSON.toJSONString("66666666666666666666"));
        System.out.println(JSON.toJSONString("666666666666666666667777"));
        System.out.println(JSON.toJSONString("aaaaaaaaaaaaa"));
        System.out.println(JSON.toJSONString("aaaaaaaaaaaaa22222222222222222222222"));
        System.out.println(JSON.toJSONString(url));
//        Resource resource = new ClassPathResource("test.yml");
//        File file = resource.getFile();
//        byte[] buffer =new byte[(int) file.length()];
//        FileInputStream is =new FileInputStream(file);
//        is.read(buffer, 0, buffer.length);
//        is.close();
//        String str = new String(buffer);
//        System.out.println(str);
        return "SUCCESS";
    }

    @RequestMapping("/testCheck")
    public Result<Boolean> checkEnum(@Validated StudentVO studentVO) {
        return Result.success(Boolean.TRUE);
    }

}
