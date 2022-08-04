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

package io.streamnative.flink.java;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.pulsar.sink.PulsarSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import io.streamnative.flink.java.common.FakerGenerator;
import io.streamnative.flink.java.common.InfiniteSourceFunction;
import io.streamnative.flink.java.config.ApplicationConfigs;

import static io.streamnative.flink.java.common.EnvironmentUtils.createEnvironment;
import static io.streamnative.flink.java.config.ApplicationConfigs.loadConfig;
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

        // Create execution environment.
        StreamExecutionEnvironment env = createEnvironment(configs);

        // Create a fake source.
        InfiniteSourceFunction<String> sourceFunction = new InfiniteSourceFunction<>(new FakerGenerator(), 20000);
        DataStreamSource<String> source = env.addSource(sourceFunction);

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
