package com.yxt.ucache.springboot.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AnnoAttributeUtil
 *
 * @author qinjiaxing on 2025/1/31
 * @author <others>
 */
public final class AnnoAttributeUtil {

    private static Logger logger = LoggerFactory.getLogger(AnnoAttributeUtil.class);

    private AnnoAttributeUtil() {
    }


    public static <A extends Annotation> String getKey(A... annos) {
        return getAttributeByAnno(null, a -> {
            try {
                return (String) MethodUtils.invokeMethod(a, "key");
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                // ignore
            }
            return "";
        }, annos);
    }


    private static <R, A extends Annotation> R getAttributeByAnno(R dftValWhenNull, Function<A, R> getVal, A... annos) {
        if (annos == null || annos.length == 0 || !ObjectUtils.anyNotNull(annos)) {
            return dftValWhenNull;
        }
        for (A anno : annos) {
            if (anno == null) {
                continue;
            }
            return getVal.apply(anno);
        }
        return dftValWhenNull;
    }
}
