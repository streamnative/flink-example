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

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.pulsar.source.PulsarSource;
import org.apache.flink.connector.pulsar.source.enumerator.cursor.StartCursor;
import org.apache.flink.connector.pulsar.source.enumerator.cursor.StopCursor;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import io.streamnative.flink.example.config.ApplicationConfigs;

import static io.streamnative.flink.example.common.EnvironmentUtils.createEnvironment;
import static io.streamnative.flink.example.config.ApplicationConfigs.loadConfig;
import static java.time.Duration.ofMinutes;
import static org.apache.flink.api.common.eventtime.WatermarkStrategy.forBoundedOutOfOrderness;
import static org.apache.flink.configuration.Configuration.fromMap;
import static org.apache.flink.connector.pulsar.source.reader.deserializer.PulsarDeserializationSchema.flinkSchema;
import static org.apache.pulsar.client.api.SubscriptionType.Shared;

/**
 * This example is used for consuming message from Pulsar.
 */
public final class SimpleSource {

    public static void main(String[] args) throws Exception {
        // Load application configs.
        ApplicationConfigs configs = loadConfig();

        // Create execution environment
        StreamExecutionEnvironment env = createEnvironment(configs);

        // Create a Pulsar source, it would consume messages from Pulsar on "sample/flink/simple-string" topic.
        PulsarSource<String> pulsarSource = PulsarSource.builder()
            .setServiceUrl(configs.serviceUrl())
            .setAdminUrl(configs.adminUrl())
            .setStartCursor(StartCursor.earliest())
            .setUnboundedStopCursor(StopCursor.never())
            .setTopics("persistent://sample/flink/simple-string")
            .setDeserializationSchema(flinkSchema(new SimpleStringSchema()))
            .setSubscriptionName("flink-source")
            .setConsumerName("flink-source-%s")
            .setSubscriptionType(Shared)
            .setConfig(fromMap(configs.sourceConfigs()))
            .build();

        // Pulsar Source don't require extra TypeInformation be provided.
        env.fromSource(pulsarSource, forBoundedOutOfOrderness(ofMinutes(5)), "pulsar-source")
            .print();

        env.execute("Simple Pulsar Source");
    }
}
