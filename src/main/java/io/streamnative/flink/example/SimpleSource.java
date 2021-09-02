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

package io.streamnative.flink.example;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.pulsar.source.PulsarSource;
import org.apache.flink.connector.pulsar.source.enumerator.cursor.StartCursor;
import org.apache.flink.connector.pulsar.source.enumerator.cursor.StopCursor;
import org.apache.flink.connector.pulsar.source.reader.deserializer.PulsarDeserializationSchema;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.apache.pulsar.client.api.SubscriptionType;

import java.time.Duration;

/**
 * This example is used for consuming message from pulsar
 */
public final class SimpleSource {

    public static void main(String[] args) throws Exception {
        // Create a StreamExecutionEnvironment with a local Flink dashboard on port 8081.
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());

        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setCheckpointInterval(Duration.ofMinutes(5).toMillis());
        env.getConfig().setAutoWatermarkInterval(Duration.ofSeconds(5).toMillis());
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, Duration.ofSeconds(10).toMillis()));

        // Set the default parallelism to 4.
        env.setParallelism(4);

        // Create a Pulsar source, it would
        PulsarSource<String> pulsarSource = PulsarSource.builder()
            .setServiceUrl("pulsar://127.0.0.1:6650")
            .setAdminUrl("http://127.0.0.1:8080")
            .setStartCursor(StartCursor.earliest())
            .setUnboundedStopCursor(StopCursor.never())
            .setTopics("persistent://sample/flink/simple-string")
            .setDeserializationSchema(PulsarDeserializationSchema.flinkSchema(new SimpleStringSchema()))
            .setSubscriptionName("flink-source")
            .setSubscriptionType(SubscriptionType.Shared)
            .build();

        // Pulsar Source don't require extra TypeInformation be provided.
        env.fromSource(pulsarSource, WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofMinutes(5)), "pulsar-source")
            .print();

        env.execute("Pulsar source with String message");
    }
}
