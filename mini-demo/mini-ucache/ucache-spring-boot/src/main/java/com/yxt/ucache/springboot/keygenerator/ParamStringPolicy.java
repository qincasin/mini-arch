package com.yxt.ucache.springboot.keygenerator;

import com.yxt.ucache.core.utils.InnerMD5Utils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ParamStringPolicy
 *
 * @author qinjiaxing on 2025/1/31
 * @author <others>
 */
@Component
public class ParamStringPolicy extends AbsKeyPolicy {

    protected static final List<String> IGNORE_NAMES = Arrays
            .asList("javax.servlet.ServletRequest", "javax.servlet.ServletResponse");
    private static final String MD5 = "MD5";
    private static final int MAX_LEN = 64;
    private final List<Class<?>> ignoreClasses = new ArrayList<>();

    public ParamStringPolicy() {
        for (String eventClassName : IGNORE_NAMES) {
            Class<?> classSafe = getClassSafe(eventClassName);
            if (classSafe != null) {
                ignoreClasses.add(classSafe);
            }
        }

    }

    @Override
    public Object generateKey(Object target, Method method, Object... args) {
        if (args == null || args.length == 0) {
            String className = org.apache.commons.lang3.ClassUtils.getShortClassName(target.getClass());
            String methodName = method.getName();
            return className + "_" + methodName;
        }
        StringBuilder sb = new StringBuilder();
        for (Object param : args) {
            if (ignore(param)) {
                continue;
            }
            sb.append(param);
        }
        if (sb.length() > MAX_LEN) {
            return MD5 + InnerMD5Utils.md5Hex(sb.toString(), "UTF-8");
        }
        return sb.toString();
    }

    private boolean ignore(Object param) {
        return this.ignoreClasses.stream()
                .anyMatch(clazz -> org.springframework.util.ClassUtils.isAssignableValue(clazz, param));
    }


    private Class<?> getClassSafe(String className) {
        try {
            return org.springframework.util.ClassUtils.forName(className, null);
        } catch (ClassNotFoundException ignore) {
            return null;
        }
    }
}
