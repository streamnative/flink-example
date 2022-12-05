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

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;

import io.streamnative.flink.java.common.InfiniteSourceFunction.InfiniteGenerator;
import net.datafaker.Faker;

import java.io.Serializable;
import java.util.Random;

/**
 * Generate an infinite string message.
 */
public class RandomStringGenerator implements InfiniteGenerator<String>, Serializable {
    private static final long serialVersionUID = -3598419528328743200L;

    private transient Faker faker;
    private int taskId;

    @Override
    public String generate() {
        return taskId + " - " + faker.name().fullName();
    }

    @Override
    public void open(RuntimeContext runtimeContext) {
        this.faker = new Faker(new Random());
        this.taskId = runtimeContext.getIndexOfThisSubtask();
    }

    @Override
    public TypeInformation<String> getType() {
        return Types.STRING;
    }
}
