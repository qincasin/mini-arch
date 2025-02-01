package com.yxt.ucache.core.config;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CacheReporterConfig {

    /**
     * 导出开关
     */
    private boolean enabled;
    /**
     * 导出地址
     */
    private String remoteAddr;
    /**
     * 导出位置
     */
    private String reportPosition;
    /**
     * 窗口大小
     */
    private Duration timeWindow;
    /**
     * 窗口阈值
     */
    private int windowThreshold;

}