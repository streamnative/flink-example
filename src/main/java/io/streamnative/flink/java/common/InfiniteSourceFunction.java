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

package io.streamnative.flink.java.common;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import org.apache.flink.shaded.guava30.com.google.common.util.concurrent.Uninterruptibles;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * A source function which would generate an infinite name stream.
 */
@Slf4j
public class InfiniteSourceFunction<T> extends RichParallelSourceFunction<T> implements ResultTypeQueryable<T> {
    private static final long serialVersionUID = 6879785309829729896L;

    private final InfiniteGenerator<T> generator;
    private final Duration interval;

    private volatile boolean cancelled;

    public InfiniteSourceFunction(InfiniteGenerator<T> generator, Duration interval) {
        this.generator = generator;
        this.interval = interval;
    }

    @Override
    public void run(SourceContext<T> sourceContext) {
        while (!cancelled) {
            T message = generator.generate();

            if (log.isInfoEnabled()) {
                log.info("Write message into Pulsar: {}", message);
            }

            sourceContext.collect(message);
            Uninterruptibles.sleepUninterruptibly(interval.toMillis(), TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void cancel() {
        this.cancelled = true;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        generator.open(getRuntimeContext());
    }

    @Override
    public TypeInformation<T> getProducedType() {
        return generator.getType();
    }

    /**
     * The generator for {@link InfiniteSourceFunction}
     */
    public interface InfiniteGenerator<T> extends Serializable {

        /**
         * Generate a record.
         */
        T generate();

        /**
         * Init a generator.
         */
        default void open(RuntimeContext runtimeContext) {}

        TypeInformation<T> getType();
    }
}
