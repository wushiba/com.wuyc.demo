package com.yfshop.admin.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.yfshop.admin.api.excel.ExcelService;
import com.yfshop.admin.api.excel.result.WebsiteCodeExcel;
import com.yfshop.admin.dao.ExcelDao;
import com.yfshop.admin.task.EmailTask;
import com.yfshop.common.util.ExcelUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.io.File;
import java.util.Date;
import java.util.List;

@DubboService
public class ExcelServiceImpl implements ExcelService {
    @Resource
    private ExcelDao excelDao;
    @Value("${websiteCode.dir}")
    private String dirPath = "/home/www/web-deploy/yufan-admin-report/websiteCode/";
    @Autowired
    EmailTask emailTask;
    @Resource
    private QueryJxsDataHelper queryJxsDataHelper;

    @Override
    @Async
    public Void sendWebsiteData() {
        File dir = new File(dirPath, "excel");
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }

        File file = new File(dir, DateUtil.format(new Date(), "yyMMddHHmmss") + ".xls");
        List<WebsiteCodeExcel> data = queryJxsDataHelper.getWebsiteCode();
        ExcelWriter excelWriter = ExcelUtil.getWriter(file);
        excelWriter.write(data);
        excelWriter.flush();
        try {
            emailTask.sendAttachmentsMail("shenjianxin@51jujibao.com", "网点码统计", "网点码统计", file.getPath());
            file.deleteOnExit();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
