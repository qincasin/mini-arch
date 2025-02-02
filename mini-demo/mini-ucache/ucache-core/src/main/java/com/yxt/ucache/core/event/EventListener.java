package com.yxt.ucache.core.event;

/**
 * 缓存时间监听器, 用于监听缓存操作的各种事件
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
public interface EventListener {

    void on(OpEvent event);
}
