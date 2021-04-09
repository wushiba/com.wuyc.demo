package com.yfshop.shop.dubbo;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * dubbo log filter
 *
 * @author Xulg
 * Created in 2019-05-29 18:53
 */
@Activate(group = CommonConstants.PROVIDER)
public class CustomLogFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomLogFilter.class);

    public CustomLogFilter() {
        System.out.println("-------------------------------------------");
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (!ArrayUtil.containsAny(SpringUtil.getActiveProfiles(), "dev", "uat")) {
            return invoker.invoke(invocation);
        }

        // 接口名称
        String interfaceName = invocation.getAttachment("interface", "unknown");
        // 接口方法名称
        String methodName = invocation.getMethodName();

        // 方法参数类型
        List<String> parameterTypes = Arrays.stream(Optional.ofNullable(invocation.getParameterTypes())
                .orElse(new Class[0])).map(Class::getName).collect(Collectors.toList());
        // 参数列表
        Object[] args = Optional.ofNullable(invocation.getArguments()).map(Object[]::clone).orElse(null);

        long start = System.currentTimeMillis();
        // invoke the rpc service
        Result result = invoker.invoke(invocation);

        VisitorInfo visitorInfo = new VisitorInfo();
        visitorInfo.setCostMills(System.currentTimeMillis() - start);
        visitorInfo.setInterfaceName(interfaceName);
        visitorInfo.setMethodName(methodName);
        visitorInfo.setParameterTypes(parameterTypes);
        visitorInfo.setArgs(args);
        visitorInfo.setResult(result.getValue());
        LOGGER.info("Dubbo服务调用=====>>>" + JSON.toJSONString(visitorInfo, true));
        return result;
    }

    @Data
    private static class VisitorInfo {
        private String interfaceName;
        private String methodName;
        private List<String> parameterTypes;
        private Object args;
        private Object result;
        private long costMills;
    }
}