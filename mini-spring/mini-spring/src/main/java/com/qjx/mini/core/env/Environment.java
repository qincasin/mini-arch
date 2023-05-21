package com.qjx.mini.core.env;

/**
 * 环境env
 *
 * @author qinjiaxing on 2023/5/21
 * @author <others>
 */
public interface Environment extends PropertyResolver {

    String[] getActiveProfiles();

    String[] getDefaultProfiles();

    boolean acceptsProfiles(String... profiles);

}
