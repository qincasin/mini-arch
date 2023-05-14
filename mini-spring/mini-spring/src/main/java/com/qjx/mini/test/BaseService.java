package com.qjx.mini.test;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/14
 * @author <others>
 */
public class BaseService {

    private BaseBaseService bbs;

    public BaseService() {
    }

    public BaseBaseService getBbs() {
        return bbs;
    }

    public void setBbs(BaseBaseService bbs) {
        this.bbs = bbs;
    }

    public void sayHello() {
        System.out.println("这里是 base service says hello ");
        this.bbs.sayHello();
    }
}
