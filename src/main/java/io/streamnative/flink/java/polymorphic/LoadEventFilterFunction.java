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

import io.streamnative.flink.java.models.LoadCreatedEvent;
import io.streamnative.flink.java.models.LoadEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

/**
 * Filter the load event by the class types.
 */
@Slf4j
public class LoadEventFilterFunction implements FlatMapFunction<LoadEvent, LoadCreatedEvent> {
    private static final long serialVersionUID = 6291670669138608297L;

    @Override
    public void flatMap(LoadEvent loadEvent, Collector<LoadCreatedEvent> collector) {
        if (loadEvent instanceof LoadCreatedEvent) {
            collector.collect((LoadCreatedEvent) loadEvent);
        } else {
            log.warn("This isn't a load event message, class type: {}, content: {}", loadEvent.getClass(), loadEvent);
        }
    }
}
