package com.yfshop.admin.api.healthy;

import com.yfshop.admin.api.healthy.request.HealthySubOrderImportReq;
import com.yfshop.admin.api.healthy.request.QueryHealthySubOrderReq;
import com.yfshop.admin.api.healthy.result.HealthySubOrderExportResult;

import java.util.List;

public interface AdminHealthyExportService {

    List<HealthySubOrderExportResult> exportSubOrderList(QueryHealthySubOrderReq req);

    Void importSubOrderList(List<HealthySubOrderImportReq> healthySubOrderImport);
}
