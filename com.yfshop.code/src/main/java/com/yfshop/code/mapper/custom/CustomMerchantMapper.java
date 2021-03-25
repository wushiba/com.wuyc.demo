package com.yfshop.code.mapper.custom;

import com.yfshop.code.model.Merchant;

import java.util.List;

/**
 * 商户复杂查询sql
 *
 * @author Xulg
 * Created in 2021-03-25 14:12
 */
public interface CustomMerchantMapper {
    List<Merchant> queryAll();
}
