package com.dahuyou.change.method.param;

/**
 * <p>
 * 网上找到的示例:
 * <a href="https://blog.csdn.net/wang0907/article/details/140488919">字节码编程bytebuddy之通过Advice动态修改方法参数值</a>
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/11
 */
public class ServerConnector {
    public static void main(String[] args) {
        ServerConnector serverConnector = new ServerConnector();
        serverConnector.newSelectorManager(null, null, 9);
    }

    public void newSelectorManager(Object executor, Object scheduler, int selectors) {
        showSelector(selectors);
    }

    private void showSelector(int selectors) {
        System.out.println("selector is: " + selectors);
    }
}
