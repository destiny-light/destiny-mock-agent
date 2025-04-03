package com.destinylight.tools.mock.demo.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * 动态代理某个指定的方法
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/11
 */
public class SomeMethodProxy implements InvocationHandler {
    private Object target;

    private SomeMethodProxy(Object target) {
        this.target = target;
    }

    public static Object proxy(Object target) {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                getAllInterfaces(target.getClass()), new SomeMethodProxy(target));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Before method: " + method.getName());
        Object result = method.invoke(target, args); // 调用目标方法
        System.out.println("After method: " + method.getName());
        return result;
    }

    private static Class<?>[] getAllInterfaces(Class<?> type) {
        Set<Class<?>> interfaces = new HashSet<>();
        while (type != null) {
            interfaces.addAll(Arrays.asList(type.getInterfaces()));
            type = type.getSuperclass();
        }
        return interfaces.toArray(new Class<?>[0]);
    }
}
