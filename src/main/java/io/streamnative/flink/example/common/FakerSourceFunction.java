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

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import org.apache.flink.shaded.guava30.com.google.common.util.concurrent.Uninterruptibles;

import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * A source function which would generate an infinite name stream.
 */
@Slf4j
public class FakerSourceFunction extends RichParallelSourceFunction<String> {
    private static final long serialVersionUID = 6879785309829729896L;

    private transient Faker faker;
    private transient int taskId;
    private volatile boolean cancelled;

    @Override
    public void run(SourceContext<String> sourceContext) {
        while (!cancelled) {
            String message = taskId + " - " + faker.name().fullName();
            log.info("Generate message: {}", message);

            sourceContext.collect(message);
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        }
    }

    @Override
    public void cancel() {
        this.cancelled = true;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        this.faker = new Faker(new Random());
        this.taskId = getRuntimeContext().getIndexOfThisSubtask();
    }
}
