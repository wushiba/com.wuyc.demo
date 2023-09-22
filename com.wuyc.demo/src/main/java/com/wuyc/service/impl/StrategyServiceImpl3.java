package com.wuyc.service.impl;

import com.wuyc.service.AbstractTemplateClass;
import org.springframework.stereotype.Service;

/**
 * @author sp0313
 * @date 2023年09月22日 16:55:00
 */
@Service
public class StrategyServiceImpl3 extends AbstractTemplateClass {

    @Override
    protected Integer getCode() {
        return 3;
    }

    @Override
    public void initData() {
        System.out.println(getUserName());
        System.out.println("进来了StrategyServiceImpl3");
    }

}
