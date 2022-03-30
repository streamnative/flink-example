package io.streamnative.flink.example.common;

import ch.kk7.confij.ConfijBuilder;

import java.util.Map;
import java.util.Properties;

/**
 * An application configuration for this flink example.
 */
public interface ApplicationConfigs {

    String serviceUrl();

    String adminUrl();

    Integer parallelism();

    Map<String, String> sourceConfigs();

    Map<String, String> sinkConfigs();

    static ApplicationConfigs loadConfig() {
        return ConfijBuilder.of(ApplicationConfigs.class).loadFrom("classpath:configs.yml").build();
    }

    static Properties toProperties(Map<String, String> map) {
        Properties properties = new Properties();
        properties.putAll(map);

        return properties;
    }
}
