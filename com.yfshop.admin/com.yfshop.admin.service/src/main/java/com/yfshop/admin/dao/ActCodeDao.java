package com.yfshop.admin.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.activity.request.ActCodeQueryReq;
import com.yfshop.admin.api.activity.result.ActCodeResult;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ActCodeDao {

    List<ActCodeResult> queryActCodeList(IPage iPage, @Param("req") ActCodeQueryReq req);

    int sumActCodeByBeforeId(@Param("id")Integer id,@Param("startDate") Date startDate);
}
