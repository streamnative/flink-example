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
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.pulsar.sink.PulsarSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import io.streamnative.flink.example.common.EnvironmentUtils;
import io.streamnative.flink.example.common.FakerSourceFunction;
import io.streamnative.flink.example.config.ApplicationConfigs;

import static io.streamnative.flink.example.config.ApplicationConfigs.loadConfig;
import static org.apache.flink.configuration.Configuration.fromMap;
import static org.apache.flink.connector.pulsar.sink.writer.serializer.PulsarSerializationSchema.flinkSchema;

/**
 * This example is used for writing message into Pulsar.
 * We use exactly-once semantic.
 */
public class SimpleSink {

    public static void main(String[] args) throws Exception {
        // Load application configs.
        ApplicationConfigs configs = loadConfig();

        // Create execution environment
        StreamExecutionEnvironment env = EnvironmentUtils.createEnvironment(configs);

        // Create a fake source.
        DataStreamSource<String> source = env.addSource(new FakerSourceFunction());

        // Create Pulsar sink.
        PulsarSink<String> sink = PulsarSink.builder()
            .setServiceUrl(configs.serviceUrl())
            .setAdminUrl(configs.adminUrl())
            .setTopics("persistent://sample/flink/simple-string")
            .setProducerName("flink-sink-%s")
            .setSerializationSchema(flinkSchema(new SimpleStringSchema()))
            .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
            .setConfig(fromMap(configs.sinkConfigs()))
            .build();

        source.sinkTo(sink);

        env.execute("Simple Pulsar Sink");
    }
}
