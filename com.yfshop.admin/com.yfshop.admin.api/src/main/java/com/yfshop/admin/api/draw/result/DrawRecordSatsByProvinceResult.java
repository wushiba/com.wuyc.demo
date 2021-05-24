package com.yfshop.admin.api.draw.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 抽奖活动奖品表
 * </p>
 *
 * @author yoush
 * @since 2021-05-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DrawRecordSatsByProvinceResult implements Serializable {


    private String province;

    private Integer drawCount = 0;

    private Integer useCount = 0;


}
