/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.streamnative.flink.java.config;

import ch.kk7.confij.ConfijBuilder;
import ch.kk7.confij.source.env.ExplicitPropertiesSource;
import org.apache.flink.runtime.entrypoint.FlinkParseException;
import org.apache.flink.runtime.entrypoint.parser.CommandLineParser;
import org.apache.flink.streaming.api.CheckpointingMode;

import java.time.Duration;
import java.util.Map;

/**
 * An application configuration for this flink example project.
 */
public interface ApplicationConfigs {

    String serviceUrl();

    String adminUrl();

    FlinkConfigs flink();

    Map<String, String> sourceConfigs();

    Map<String, String> sinkConfigs();

    interface FlinkConfigs {

        int parallelism();

        CheckpointingMode checkpointMode();

        Duration checkpointInterval();

        Duration autoWatermarkInterval();

        int restartAttempts();

        Duration delayBetweenAttempts();

        Map<String, String> options();
    }

    /**
     * Load the final application configs from the {@code configs.yml} file and main args.
     * You can override the configuration by passing something like {@code -C serviceUrl=pulsar://192.168.50.8:6650}.
     */
    static ApplicationConfigs loadConfig(String[] args) throws FlinkParseException {
        CommandLineParser<BootstrapConfigs> commandLineParser = new CommandLineParser<>(new BootstrapConfigsFactory());
        BootstrapConfigs bootstrapConfigs = commandLineParser.parse(args);

        return ConfijBuilder.of(ApplicationConfigs.class)
            .loadFrom("classpath:configs.yml")
            .loadFrom(new ExplicitPropertiesSource(bootstrapConfigs.getProperties()))
            .build();
    }
}
