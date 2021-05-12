package com.yfshop.admin.api.draw.result;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApiModel
@Data
public class DrawActivityDetailsResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String actTitle;

    private Date startTime;

    private Date endTime;

    private String bannerUrl;

    private String actDesc;

    private String jumpUrl;

    private List<DrawPrizeResult> prizeList;


    @Data
    public static class DrawPrizeResult implements Serializable {

        private static final long serialVersionUID = 1L;

        private Integer id;

        private String prizeTitle;

        private Integer prizeLevel;

        private Integer couponId;

        private Integer prizeCount;

        private String prizeIcon;

        private Integer winRate;

        /** 二等奖小盒中奖概率 */
        private Integer smallBoxRate;

        private Integer sort;
    }


}
