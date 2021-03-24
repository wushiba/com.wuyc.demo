package com.yfshop.admin.config;


import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DateConverter implements Converter<String, Date> {

    @Override
    public Date convert(String source) {

        if (StringUtils.isBlank(source)) {
            return null;
        } else if (NumberUtils.isParsable(source) && source.length() == 13) {
            // 时间戳
            return new Date(Long.parseLong(source));
        } else {
            // yyyy-MM-dd HH:mm:ss
            // yyyy-MM-dd
            // ......
            return DateUtil.parse(source).toJdkDate();
        }

    }
}
