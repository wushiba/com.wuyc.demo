package com.wuyc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author sp0313
 * @date 2023年09月22日 17:19:00
 */
@Service
public class StrategyFactory {

    private Map<Integer, AbstractTemplateClass> strategyMap;

    @Autowired
    public StrategyFactory(List<AbstractTemplateClass> abstractStrategyList) {
        this.strategyMap = abstractStrategyList.stream()
                .collect(Collectors.toMap(AbstractTemplateClass::getCode, Function.identity()));
    }

    public void test(Integer code) {
        AbstractTemplateClass abstractTemplateClass = strategyMap.get(code);
        abstractTemplateClass.initData();
    }


}
