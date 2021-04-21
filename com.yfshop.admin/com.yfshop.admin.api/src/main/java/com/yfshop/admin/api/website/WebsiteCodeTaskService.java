package com.yfshop.admin.api.website;

import io.swagger.models.auth.In;

public interface WebsiteCodeTaskService {
   void buildWebSiteCode(Integer id);

   void doWorkWebsiteCodeFile(String outTradeNo);
}
