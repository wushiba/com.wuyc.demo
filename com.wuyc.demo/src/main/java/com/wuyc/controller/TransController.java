package com.wuyc.controller;

import com.alibaba.fastjson.JSON;
import com.wuyc.vo.TestDriveReq;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author sp0313
 * @date 2023年07月19日 11:20:00
 */
@RestController
public class TransController {

    @RequestMapping("/trans")
    public String test(@RequestBody TestDriveReq testDriveReq) throws IOException {
        System.out.println(JSON.toJSONString("66666666666666666666"));
        return "SUCCESS";
    }

}
