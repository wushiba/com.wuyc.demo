package com.yfshop.admin.api.mall.request;

import com.yfshop.common.enums.ReceiveWayEnum;
import com.yfshop.common.validate.annotation.CandidateValue;
import com.yfshop.common.validate.annotation.CheckEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 创建商品所需要的参数封装类
 *
 * @author Xulg
 * Created in 2019-06-25 13:01
 */
@Data
public class ItemCreateReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "商品名称不能为空")
    private String itemTitle;

    @NotBlank(message = "商品配送方式不能为空")
    @CheckEnum(value = ReceiveWayEnum.class, message = "不支持的配送方式")
    private String receiveWay;

    @NotNull(message = "分类id不能为空")
    private Integer categoryId;

    private String itemSubTitle;

    @NotBlank(message = "是否上架不能为空")
    @CandidateValue(candidateValue = {"Y", "N"}, message = "是否上架值只能是Y|N")
    private String isEnable;

    @NotEmpty(message = "商品图片列表不能为空")
    private List<String> itemImages;

    @NotBlank(message = "商品详情不能为空")
    private String itemContent;


}
