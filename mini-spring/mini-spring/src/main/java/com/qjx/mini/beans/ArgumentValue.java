package com.qjx.mini.beans;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/10
 * @author <others>
 */
public class ArgumentValue {

    private Object value;
    private String type;
    private String name;

    public ArgumentValue(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public ArgumentValue(String type, String name, String value) {
        this.type = type;
        this.value = value;
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
