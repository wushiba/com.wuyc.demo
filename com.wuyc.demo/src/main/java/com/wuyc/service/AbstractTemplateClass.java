package com.wuyc.service;

import org.springframework.stereotype.Service;

/**
 * @author sp0313
 * @date 2023年09月22日 16:56:00
 */
@Service
public abstract class AbstractTemplateClass implements StrategyService {

    protected abstract Integer getCode();

    @Override
    public void initData() {
        System.out.println("进来了抽象模板类");
    }

    public String getUserName() {
        return "张三";
    }

}
