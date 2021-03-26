package com.yfshop.admin.api.draw.request;

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
public class CreateDrawActivityReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @NotBlank(message = "活动标题不能为空")
    @Length(min = 1, max = 10, message = "活动标题不能超过{max}个字")
    private String actTitle;

    @NotBlank(message = "开始时间不能为空")
    private Date startTime;

    @NotBlank(message = "结束时间不能为空")
    private Date endTime;

    @NotBlank(message = "banner图不能为空")
    private String bannerUrl;

    @NotBlank(message = "活动描述不能为空")
    private String actDesc;

    private String jumpUrl;

    private List<DrawPrizeReq> prizeList;

    private List<ProvinceRateReq> provinceRateList;

    @Data
    public static class DrawPrizeReq implements Serializable {

        private static final long serialVersionUID = 1L;

        private Integer id;

        @NotBlank(message = "奖品标题不能为空")
        private String prizeTitle;

        @NotBlank(message = "奖品等级不能为空")
        @Min(value = 1, message = "奖品等级最小为1")
        @Max(value = 3, message = "奖品等级最大为3")
        private Integer prizeLevel;

        @NotBlank(message = "优惠券id不能为空")
        @Min(value = 1, message = "优惠券id最小为1")
        private Integer couponId;

        @NotBlank(message = "奖品数量不能为空")
        private Integer prizeCount;

        @NotBlank(message = "奖品图标地址不能为空")
        private String prizeIcon;

        @NotBlank(message = "中奖概率不能为空")
        private Integer winRate;

        /** 二等奖小盒中奖概率 */
        private Integer smallBoxRate;

        private Integer sort;
    }

    @Data
    public static class ProvinceRateReq implements Serializable {

        private static final long serialVersionUID = 1L;

        private Integer id;

        private Integer provinceId;

        private String provinceName;

        private Integer firstWinRate;

        private Integer secondWinRate;

        /** 二等奖小盒中奖概率 */
        private Integer secondSmallBoxWinRate;
    }

    public static void main(String[] args) {

        List<CreateDrawActivityReq.DrawPrizeReq> prizeList = new ArrayList<>();

        CreateDrawActivityReq.DrawPrizeReq prizeReq1 = new CreateDrawActivityReq.DrawPrizeReq();
        prizeReq1.setPrizeLevel(1);

        CreateDrawActivityReq.DrawPrizeReq prizeReq2 = new CreateDrawActivityReq.DrawPrizeReq();
        prizeReq2.setPrizeLevel(2);

        CreateDrawActivityReq.DrawPrizeReq prizeReq3 = new CreateDrawActivityReq.DrawPrizeReq();
        prizeReq3.setPrizeLevel(3);

        prizeList.add(prizeReq1);
        prizeList.add(prizeReq2);
        prizeList.add(prizeReq3);

        Map<Integer, List<DrawPrizeReq>> map = prizeList.stream().collect(Collectors.groupingBy(CreateDrawActivityReq.DrawPrizeReq::getPrizeLevel));

        System.out.println(map.size());
    }

}
