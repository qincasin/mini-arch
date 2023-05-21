package com.qjx.mini.test;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/14
 * @author <others>
 */
public class BaseBaseService {

    private AServiceImpl as;

    public BaseBaseService() {
    }

    public AServiceImpl getAs() {
        return as;
    }

    public void setAs(AServiceImpl as) {
        this.as = as;
    }

    public void sayHello() {
        System.out.println("Base Base Service says hello");

    }


    public void init() {
        System.out.println("Base Base Service init method.");
    }
}
