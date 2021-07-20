package com.yfshop.code.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author yoush
 * @since 2021-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_wx_push_task_extend")
public class WxPushTaskExtend extends Model<WxPushTaskExtend> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer pushId;

    private LocalDateTime subscribeStartTime;

    private LocalDateTime subscribeEndTime;

    private Integer firstCount;

    private Integer secondCount;

    private Integer otherCount;

    private LocalDateTime couponStartTime;

    private LocalDateTime couponEndTime;

    private Integer useCount;

    private LocalDateTime useStartTime;

    private LocalDateTime useEndTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
