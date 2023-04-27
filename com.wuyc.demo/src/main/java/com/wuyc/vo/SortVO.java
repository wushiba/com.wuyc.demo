package com.wuyc.vo;

import com.wuyc.util.ListSort;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author sp0313
 * @date 2023年04月27日 15:24:00
 */
@Data
public class SortVO {

    private Long valueId;

    private Integer sortNum;

    public SortVO(Long valueId, Integer sortNum) {
        this.valueId = valueId;
        this.sortNum = sortNum;
    }

    public static List<SortVO> initSortList() {
        return Stream.of(
                new SortVO(1111111L, 1),
                new SortVO(2222222L, 2),
                new SortVO(3333333L, 3),
                new SortVO(4444444L, 4),
                new SortVO(5555555L, 5)
        ).collect(Collectors.toList());
    }

}
