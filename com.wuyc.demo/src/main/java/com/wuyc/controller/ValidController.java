package com.wuyc.controller;

import com.alibaba.fastjson.JSON;
import com.wuyc.vo.Result;
import com.wuyc.vo.StudentVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author sp0313
 * @date 2022年11月28日 09:38:00
 */
@RestController
public class ValidController {


    @RequestMapping("/test")
    public String test() throws IOException {
        System.out.println(JSON.toJSONString("66666666666666666666"));
        return "SUCCESS";
    }

    @RequestMapping("/testCheck")
    public Result<Boolean> checkEnum(@Validated StudentVO studentVO) {
        return Result.success(Boolean.TRUE);
    }

}
