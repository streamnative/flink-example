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

package io.streamnative.flink.java.polymorphic;

import io.streamnative.flink.java.common.InfiniteSourceFunction;
import io.streamnative.flink.java.config.ApplicationConfigs;
import io.streamnative.flink.java.generator.RandomLoadEventGenerator;
import io.streamnative.flink.java.models.LoadEvent;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.pulsar.sink.PulsarSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.pulsar.client.api.Schema;

import static io.streamnative.flink.java.common.EnvironmentUtils.createEnvironment;
import static io.streamnative.flink.java.config.ApplicationConfigs.loadConfig;
import static org.apache.flink.configuration.Configuration.fromMap;
import static org.apache.flink.connector.pulsar.sink.writer.serializer.PulsarSerializationSchema.pulsarSchema;

/**
 * Generate load event and save it into Pulsar with JSON schema.
 */
public class LoadEventSink {

    public static void main(String[] args) throws Exception {
        // Load application configs.
        ApplicationConfigs configs = loadConfig(args);

        // Create execution environment.
        StreamExecutionEnvironment env = createEnvironment(configs);

        // Create a fake source.
        InfiniteSourceFunction<LoadEvent> sourceFunction = new InfiniteSourceFunction<>(new RandomLoadEventGenerator(), 20000);
        DataStreamSource<LoadEvent> source = env.addSource(sourceFunction);

        // Create Pulsar sink.
        PulsarSink<LoadEvent> sink = PulsarSink.builder()
            .setServiceUrl(configs.serviceUrl())
            .setAdminUrl(configs.adminUrl())
            .setTopics("persistent://sample/flink/load-event")
            .setProducerName("flink-sink-%s")
            .setSerializationSchema(pulsarSchema(Schema.JSON(LoadEvent.class), LoadEvent.class))
            .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
            .setConfig(fromMap(configs.sinkConfigs()))
            .build();

        source.sinkTo(sink);

        env.execute("Load Event Pulsar Sink");
    }
}
