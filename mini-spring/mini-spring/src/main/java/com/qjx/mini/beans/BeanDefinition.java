package com.qjx.mini.beans;

import lombok.Getter;
import lombok.Setter;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/9
 * @author <others>
 */
@Setter
@Getter
public class BeanDefinition {

    private String id;
    private String className;

    public BeanDefinition(String id, String className) {
        this.id = id;
        this.className = className;
    }
}
