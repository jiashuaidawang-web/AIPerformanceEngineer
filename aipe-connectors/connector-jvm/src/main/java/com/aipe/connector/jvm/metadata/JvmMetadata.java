package com.aipe.connector.jvm.metadata;

import com.aipe.connector.sdk.model.ConnectorMetadata;

import java.util.Arrays;

/**
 * JVM Connector 元数据
 */
public final class JvmMetadata {

    private JvmMetadata() {}

    public static ConnectorMetadata get() {
        return ConnectorMetadata.builder()
                .name("JVM Connector")
                .version("1.0.0")
                .author("AI Performance Engineer")
                .description("Collects JVM performance metrics via JMX (Memory, GC, Thread, CPU, ClassLoader, Runtime)")
                .type("JVM")
                .supportedResources(Arrays.asList("jvm-local", "jvm-remote"))
                .build();
    }
}
