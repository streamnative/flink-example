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

package io.streamnative.flink.java.producer;

import io.streamnative.flink.java.config.ApplicationConfigs;
import io.streamnative.flink.java.models.ExampleUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.impl.schema.JSONSchema;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static io.streamnative.flink.java.config.ApplicationConfigs.loadConfig;
import static io.streamnative.flink.java.models.ExampleUser.createRandomUserWithCreateTime;

/**
 * Producer application that writes data to a Pulsar topic.
 * SQL examples will then query the data from the same topic.
 */
@Slf4j
public final class ExampleUserProducer {

    private static final String USER_COMPLETE_TOPIC_PATH = "sample/flink/user";

    public static void main(String[] args) throws Exception {
        ApplicationConfigs configs = loadConfig(args);

        PulsarClient client = PulsarClient.builder()
                .serviceUrl(configs.serviceUrl())
                .build();

        // designate an JSON schema
        Schema<ExampleUser> schema = JSONSchema.of(ExampleUser.class);
        Producer<ExampleUser> producer = client.newProducer(schema)
                .topic(USER_COMPLETE_TOPIC_PATH)
                .create();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Closing Pulsar clients...");
            try {
                producer.close();
                client.close();
            } catch (PulsarClientException e) {
                e.printStackTrace();
            }
        }));

        while (true) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            long epochMillis = currentDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
            ExampleUser user = createRandomUserWithCreateTime(epochMillis);
            producer.newMessage()
                    .eventTime(epochMillis)
                    .value(user)
                    .send();

            Thread.sleep(100);
        }
    }
}
