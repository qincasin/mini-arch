package com.yxt.ucache.springboot.keygenerator;

import java.lang.reflect.Method;
import org.springframework.stereotype.Component;

/**
 * NoneKeyGeneratorPolicy
 *
 * @author qinjiaxing on 2025/1/31
 * @author <others>
 */
@Component
public class NoneKeyGeneratorPolicy extends AbsKeyPolicy {

    @Override
    public Object generateKey(Object target, Method method, Object... args) {
        return "";
    }
}
