package com.aipe.connector.jvm.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.util.List;

/**
 * JMX 支持工具类
 *
 * <p>提供统一的 JMX Bean 访问入口。
 */
public final class JmxSupport {

    private static final Logger log = LoggerFactory.getLogger(JmxSupport.class);

    private JmxSupport() {}

    public static MemoryMXBean getMemoryMXBean() {
        return ManagementFactory.getMemoryMXBean();
    }

    public static ThreadMXBean getThreadMXBean() {
        return ManagementFactory.getThreadMXBean();
    }

    public static OperatingSystemMXBean getOperatingSystemMXBean() {
        return ManagementFactory.getOperatingSystemMXBean();
    }

    public static ClassLoadingMXBean getClassLoadingMXBean() {
        return ManagementFactory.getClassLoadingMXBean();
    }

    public static RuntimeMXBean getRuntimeMXBean() {
        return ManagementFactory.getRuntimeMXBean();
    }

    public static List<GarbageCollectorMXBean> getGarbageCollectorMXBeans() {
        return ManagementFactory.getGarbageCollectorMXBeans();
    }

    /**
     * 获取 JVM 基本信息摘要
     */
    public static String getJvmSummary() {
        RuntimeMXBean rt = getRuntimeMXBean();
        OperatingSystemMXBean os = getOperatingSystemMXBean();
        return String.format("JVM[%s %s %s] OS[%s %s] Cores[%d]",
                rt.getVmName(), rt.getVmVendor(), rt.getVmVersion(),
                os.getName(), os.getVersion(), os.getAvailableProcessors());
    }
}
