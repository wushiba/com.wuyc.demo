package com.wuyc.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 置顶排序信息
 *
 * @author sp0313
 * @date 2023年04月28日 09:51:00
 */
@Data
public class TopSortInfo implements Serializable {
    private static final long serialVersionUID = -901786934563578411L;

    /**
     * 排序，根据不通业务场景对应不同值、如内容id、活动id等
     */
    private Long valueId;

    /**
     * 排序号
     */
    private Integer sortNum;

    public TopSortInfo() {

    }

    public TopSortInfo(Long valueId, Integer sortNum) {
        this.valueId = valueId;
        this.sortNum = sortNum;
    }

    public static List<TopSortInfo> initSortList() {
//        return Stream.of(
//                new SortVO(1111111L, 1),
//                new SortVO(2222222L, 2),
//                new SortVO(3333333L, 3),
//                new SortVO(4444444L, 4),
//                new SortVO(5555555L, 5)
//        ).collect(Collectors.toList());
        return Stream.of(
                new TopSortInfo(33333336666L, 2)
        ).collect(Collectors.toList());
    }

}
