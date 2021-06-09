package com.yfshop.admin.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.activity.request.ActCodeQueryReq;
import com.yfshop.admin.api.activity.result.ActCodeResult;
import com.yfshop.admin.api.excel.result.WebsiteCodeExcel;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ExcelDao {
    List<WebsiteCodeExcel> getWebsiteCode();
}
