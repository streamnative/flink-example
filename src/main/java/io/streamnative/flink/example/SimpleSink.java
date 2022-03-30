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

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.pulsar.sink.PulsarSink;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import io.streamnative.flink.example.common.FakerSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;
import static org.apache.flink.connector.pulsar.sink.writer.serializer.PulsarSerializationSchema.flinkSchema;

/**
 * This example is used for writing message into Pulsar.
 * We use at-least-once semantic.
 */
public class SimpleSink {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleSink.class);

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setCheckpointInterval(ofMinutes(5).toMillis());
        env.getConfig().setAutoWatermarkInterval(ofSeconds(5).toMillis());
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, ofSeconds(10).toMillis()));

        // Set the default parallelism to 4.
        env.setParallelism(4);

        // Create a fake source.
        DataStreamSource<String> source = env.addSource(new FakerSourceFunction());

        // Create Pulsar sink.
        PulsarSink<String> sink = PulsarSink.builder()
            .setServiceUrl("pulsar://127.0.0.1:6650")
            .setAdminUrl("http://127.0.0.1:8080")
            .setTopics("persistent://sample/flink/simple-string")
            .setSerializationSchema(flinkSchema(new SimpleStringSchema()))
            .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
            .build();

        source.map(new MapFunction<String, String>() {
            private static final long serialVersionUID = -4782508933535702921L;

            @Override
            public String map(String s) {
                LOG.info("Write \"{}\" into Pulsar.", s);
                return s;
            }
        }).sinkTo(sink);

        env.execute("Simple Pulsar Sink");
    }
}
