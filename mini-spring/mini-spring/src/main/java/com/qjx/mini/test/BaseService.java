package com.qjx.mini.test;

import com.qjx.mini.beans.factory.annotation.Autowired;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/14
 * @author <others>
 */
public class BaseService {

    @Autowired
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
        bbs.sayHello();
    }

    public void init() {
        System.out.print("Base Service init method.");
    }

}
