package com.wuyc.controller;

import com.wuyc.vo.Result;
import com.wuyc.vo.StudentVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sp0313
 * @date 2022年11月28日 09:38:00
 */
@RestController
public class ValidController {

    @RequestMapping("/test")
    public String test() {
        return "SUCCESS";
    }

    @RequestMapping("/testCheck")
    public Result<Boolean> checkEnum(@Validated StudentVO studentVO) {
        return Result.success(Boolean.TRUE);
    }

}
