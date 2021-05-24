package com.yfshop.admin.api.draw.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

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
public class DrawRecordSatsByLevelResult implements Serializable {


    private String level;

    private Integer drawCount = 0;

    private Integer useCount = 0;


}
