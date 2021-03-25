package com.yfshop.admin.api.sourcefactory.req;

import com.yfshop.admin.api.sourcefactory.excel.SourceFactoryExcel;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author Xulg
 * Created in 2021-03-25 19:20
 */
@Data
public class ImportSourceFactoryReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "导入数据不能为空")
    @Valid
    private List<SourceFactoryExcel> excels;
}
