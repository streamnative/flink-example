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

package io.streamnative.flink.example.common;

import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.runtime.state.storage.JobManagerCheckpointStorage;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import io.streamnative.flink.example.config.ApplicationConfigs;
import io.streamnative.flink.example.config.ApplicationConfigs.FlinkConfigs;

import static org.apache.flink.api.common.restartstrategy.RestartStrategies.fixedDelayRestart;

/**
 * Util for creating {@link StreamExecutionEnvironment}
 */
public final class EnvironmentUtils {

    private EnvironmentUtils() {
        // No public
    }

    public static StreamExecutionEnvironment createEnvironment(ApplicationConfigs applicationConfigs) {
        FlinkConfigs flinkConfigs = applicationConfigs.flink();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getCheckpointConfig().setCheckpointingMode(flinkConfigs.checkpointMode());
        env.getCheckpointConfig().setCheckpointInterval(flinkConfigs.checkpointInterval().toMillis());
        env.getConfig().setAutoWatermarkInterval(flinkConfigs.autoWatermarkInterval().toMillis());
        env.setRestartStrategy(fixedDelayRestart(flinkConfigs.restartAttempts(), flinkConfigs.delayBetweenAttempts().toMillis()));
        env.setParallelism(flinkConfigs.parallelism());

        // These configs are only used for test purpose.
        env.getCheckpointConfig().setCheckpointStorage(new JobManagerCheckpointStorage());
        env.setStateBackend(new HashMapStateBackend());

        return env;
    }
}
