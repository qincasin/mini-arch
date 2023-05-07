package com.qjx.mini.test;

import com.qjx.mini.context.ClassPathXmlApplicationContext;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/7
 * @author <others>
 */
public class Test1 {

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        AService aService = (AService) context.getBean("aservice");
        aService.sayHello();
    }

}
