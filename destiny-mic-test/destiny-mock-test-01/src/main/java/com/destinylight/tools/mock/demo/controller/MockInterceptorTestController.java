package com.destinylight.tools.mock.demo.controller;

import com.destinylight.tools.mock.demo.service.CorpService;
import com.destinylight.tools.mock.demo.service.PersonService;
import com.destinylight.tools.mock.demo.service.SomeMethodProxy;
import com.destinylight.tools.mock.demo.service.SomethingForProxy;
import com.dahuyou.change.method.param.ServerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;

/**
 * <p>
 * 用于测试Mock拦截器
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/3
 */
@RestController
@RequestMapping("/mic")
public class MockInterceptorTestController {
    private static final Logger log = LoggerFactory.getLogger(MockInterceptorTestController.class);

    @Autowired
    private PersonService personService;
    @Autowired
    private CorpService corpService;

    @GetMapping(value = "/showconfigFromProfile")
    public void showconfigFromProfile() {
        log.info("打印活动的profile文件中定义的mock拦截器组件主参数");
    }

    @GetMapping(value = "/personInfoTest")
    public void personInfoTest() {
        log.info("测试【个人信息服务】");
        personService.personInfoTest();
        log.info("测试【个人信息服务】完成");
        ServerConnector serverConnector = new ServerConnector();
        serverConnector.newSelectorManager(null, null, 9);
        String a = new PersonService().abc(1, 2, 1388, "3S");
        log.info("from abc(): {}", a);
    }

    @GetMapping(value = "/corpServiceTest")
    public void corpServiceTest() {
        log.info("测试【Mock服务器的mock接口】");
        corpService.execute();
        log.info("测试【Mock服务器的mock接口】完成");
    }

    /**
     * 大在启动时，某个方法并没有被动态代理
     */
    @GetMapping(value = "/proxystep01")
    public void proxystep01() {
        log.info("测试【对方法的动态代理-Step 1.还没有被代理】开始");
        new SomethingForProxy().shouldProxy();
        new SomethingForProxy().withoutProxy();
        log.info("测试【对方法的动态代理-Step 1.还没有被代理】完成");
    }

    /**
     * 将该方法注册到动态代理中
     */
    @GetMapping(value = "/proxystep02")
    public void proxystep02()
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        log.info("测试【对方法的动态代理-step 2.将该方法注册到动态代理中】开始");
        // java.lang.ClassCastException:class jdk.proxy2.SProxy79 cannot be cast to
        // class com.destinylight.service.somethingForProxy (jdk.proxy2.SProxy79 is in module jdk.proxy2 of loader 'app';
        // com.destinylight.service.SomethingForProxy is in unnamed module of loader 'app')
        SomethingForProxy obj = (SomethingForProxy) SomeMethodProxy.proxy(new SomethingForProxy());
        obj.shouldProxy();
        obj.withoutProxy();
        log.info("测试【对方法的动态代理-Step 2.将该方法注册到动态代理中】完成");
    }

    /**
     * 再次调用已经被动态代理的方法
     */
    @GetMapping(value = "/proxystep03")
    public void proxystep03() {
        log.info("测试【对方法的动态代理-Step 3.已经被代理】开始");
        new SomethingForProxy().shouldProxy();
        new SomethingForProxy().withoutProxy();
        log.info("测试【对方法的动态代理-Step 3.已经被代理】完成");
    }

    /**
     * 测试数据类型。
     * <pre>
     * 不能直接判断某个变量的类型是否是"int"类型，只能将其转换成"Object"，然后看其是否是"Integer"的实例。
     * 即，我们即使知道某个变量是"Integer"，但也不能再进一步确定到底是"int"还是"Integer"。
     * 要判断某个变量，只能通过反射的方式来做。
     * 也就是说，只能判断类的成员变量或者方法参数的某个变量是否是"int"的基础类型，
     * 不能判断方法体内某个变量是否是"int"的基础类型。
     * </pre>
     *
     * @See <a href="https://blog.51cto.com/u_16213438/9500035">如何判断java属性是否为基础类型</a>
     */
    @GetMapping(value = "/typeTest")
    public void typeTest() {
        int v1 = 0;
        testTest(v1);
        Integer v2 = 1;
        log.info("类型名称[{}][{}]", v2.getClass().getName(), v2.getClass().isPrimitive());
        testTest(v2);
    }

    private void testTest(Object v) {
        log.info("类型名称[{}][{}]", v.getClass().getName(), v.getClass().isPrimitive());
    }
}
