package com.qjx.mini.beans;

import lombok.Getter;
import lombok.Setter;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/10
 * @author <others>
 */
@Getter
public class PropertyValue {

    private final String type;

    private final String name;
    private final Object value;

    public PropertyValue(String type, String name, Object value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

}