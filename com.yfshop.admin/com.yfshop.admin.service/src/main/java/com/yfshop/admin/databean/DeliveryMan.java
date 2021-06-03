package com.yfshop.admin.databean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Xulg
 * Description: TODO 请加入类描述信息
 * Created in 2021-06-03 11:41
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryMan {
    private Integer merchantId;
    private String name;
    private String mobile;
}
