package com.qjx.mini.test;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/7
 * @author <others>
 */
public class AServiceImpl implements AService {

    private String property1;
    private String property2;

    private String name;
    private int level;

    private BaseService ref1;

    public AServiceImpl() {
    }

    public AServiceImpl(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public BaseService getRef1() {
        return ref1;
    }

    public void setRef1(BaseService ref1) {
        this.ref1 = ref1;
    }

    public String getProperty1() {
        return property1;
    }

    public void setProperty1(String property1) {
        this.property1 = property1;
    }

    public String getProperty2() {
        return property2;
    }

    public void setProperty2(String property2) {
        this.property2 = property2;
    }

    @Override
    public void sayHello() {
        System.out.println("11111");
    }
}
