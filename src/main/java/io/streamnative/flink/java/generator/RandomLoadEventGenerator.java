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

package io.streamnative.flink.java.generator;

import io.streamnative.flink.java.common.InfiniteSourceFunction;
import io.streamnative.flink.java.models.LoadCreatedEvent;
import io.streamnative.flink.java.models.LoadDeletedEvent;
import io.streamnative.flink.java.models.LoadEvent;
import io.streamnative.flink.java.models.LoadUpdatedEvent;
import net.datafaker.Faker;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;

import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * A generator which will generate random subclass of {@link LoadEvent}.
 */
public class RandomLoadEventGenerator implements InfiniteSourceFunction.InfiniteGenerator<LoadEvent>, Serializable {
    private static final long serialVersionUID = -8054614105845216295L;

    private transient Faker faker;

    @Override
    public LoadEvent generate() {
        switch (faker.random().nextInt(0, 2)) {
            case 0:
                return randomLoadCreatedEvent();
            case 1:
                return randomLoadDeletedEvent();
            case 2:
                return randomLoadUpdatedEvent();
            default:
                throw new IllegalArgumentException("This exception shouldn't occur.");
        }
    }

    private LoadEvent randomLoadCreatedEvent() {
        List<String> messages = Stream.generate(() -> faker.artist().name())
            .limit(faker.number().numberBetween(5, 10))
            .collect(toList());

        return new LoadCreatedEvent()
            .setCreatedAction(faker.bigBangTheory().character())
            .setMessages(messages)
            .setUuid(UUID.randomUUID().toString())
            .setId(faker.number().positive())
            .setName(faker.name().name())
            .setContent(faker.commerce().productName());
    }

    private LoadEvent randomLoadDeletedEvent() {
        List<String> ids = Stream.generate(() -> UUID.randomUUID().toString())
            .limit(faker.number().numberBetween(10, 15))
            .collect(toList());

        return new LoadDeletedEvent()
            .setDeletedIds(ids)
            .setErrorMsg(faker.appliance().brand())
            .setUuid(UUID.randomUUID().toString())
            .setId(faker.number().positive())
            .setName(faker.name().name())
            .setContent(faker.commerce().productName());
    }

    private LoadEvent randomLoadUpdatedEvent() {
        return new LoadUpdatedEvent()
            .setNewAction(faker.pokemon().name())
            .setErrorMsg(faker.appliance().brand())
            .setUuid(UUID.randomUUID().toString())
            .setId(faker.number().positive())
            .setName(faker.name().name())
            .setContent(faker.commerce().productName());
    }

    @Override
    public void open(RuntimeContext runtimeContext) {
        this.faker = new Faker(new Random());
    }

    @Override
    public TypeInformation<LoadEvent> getType() {
        return Types.POJO(LoadEvent.class);
    }
}
