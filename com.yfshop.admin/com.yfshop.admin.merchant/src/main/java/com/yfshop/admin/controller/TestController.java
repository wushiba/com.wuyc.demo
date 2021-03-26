package com.yfshop.admin.controller;

import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Xulg
 * Created in 2021-03-26 11:32
 */
@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping("convert")
    public Object object(LocalDateTime localDateTime, Date date, Data data) {
        Map<String, Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put("localDateTime", localDateTime);
        stringObjectMap.put("date", date);
        return stringObjectMap;
    }

    @PostMapping("convert2")
    public Object object(@RequestBody Data data) {
        return data;
    }

    @lombok.Data
    public static class Data {
        private LocalDateTime localDateTime;
        private Date date;
    }
}
