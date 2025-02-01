package com.yxt.ucache.springboot.keygenerator;

import com.yxt.ucache.anno.CacheRemove;
import com.yxt.ucache.anno.CacheRemoveAll;
import com.yxt.ucache.anno.CacheUpdate;
import com.yxt.ucache.anno.Cached;
import com.yxt.ucache.springboot.aop.AnnoAttributeUtil;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;

/**
 * SpelKeyGeneratorPolicy
 * TODO 待完善
 *
 * @author qinjiaxing on 2025/1/31
 * @author <others>
 */
@Component
public class SpelKeyGeneratorPolicy extends AbsKeyPolicy {

    private final ExpressionEvaluator evaluator = new ExpressionEvaluator();

    @Override
    public Object generateKey(Object target, Method method, Object... args) {
        Cached cached = method.getAnnotation(Cached.class);
        CacheRemove cacheRemove = method.getAnnotation(CacheRemove.class);
        CacheRemoveAll cacheRemoveAll = method.getAnnotation(CacheRemoveAll.class);
        CacheUpdate cacheUpdate = method.getAnnotation(CacheUpdate.class);
        String key = AnnoAttributeUtil.getKey(cached, cacheRemove, cacheUpdate);
        if (StringUtils.isEmpty(key) && cacheRemoveAll == null) {
            return "";
        }
        EvaluationContext evaluationContext = evaluator
                .createEvaluationContext(target, target.getClass(), method, args);
        AnnotatedElementKey methodKey = new AnnotatedElementKey(method, target.getClass());

        if (cacheRemoveAll != null) {
            return Arrays.stream(cacheRemoveAll.value()).map(e -> {
                if (StringUtils.isEmpty(e.key())) {
                    return "";
                }
                try {
                    return evaluator.evalValue(e.key(), methodKey, evaluationContext, String.class);
                } catch (Exception ex) {
                    logger.warn("UCache SpelKeyGeneratorPolicy cacheRemoveAll evalValue error, key={}, e", e.key(), e);
                    return e.key();
                }
            }).collect(Collectors.toSet());
        }
        try {
            return evaluator.evalValue(key, methodKey, evaluationContext, String.class);
        } catch (Exception e) {
            logger.warn("UCache SpelKeyGeneratorPolicy evalValue error, key={}, e", key, e);
            return key;
        }
    }
}
