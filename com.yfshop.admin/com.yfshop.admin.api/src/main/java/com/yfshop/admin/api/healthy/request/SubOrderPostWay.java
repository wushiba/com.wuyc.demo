package com.yfshop.admin.api.healthy.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 商品图片表
 * </p>
 *
 * @author yoush
 * @since 2021-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SubOrderPostWay implements Serializable {


    private List<Integer> ids;

    /**
     * 指派商户
     */
    private Integer merchantId;

    /**
     * 配送方式：物流|配送
     */
    private String postWay;


    /**
     * 快递公司名称
     */
    private String expressCompany;

    /**
     * 快递单号
     */
    private String expressNo;

}
