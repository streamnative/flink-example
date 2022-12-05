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

package io.streamnative.flink.java.models;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.pulsar.shade.com.fasterxml.jackson.annotation.JsonSubTypes;
import org.apache.pulsar.shade.com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 * Common class for events. We should add the Jackson annotation here.
 * You should use the Pulsar-shaded Jackson annotations.
 */
@Data
@Accessors(chain = true)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@class"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = LoadCreatedEvent.class, name = "LoadCreatedEvent"),
    @JsonSubTypes.Type(value = LoadDeletedEvent.class, name = "LoadDeletedEvent"),
    @JsonSubTypes.Type(value = LoadUpdatedEvent.class, name = "LoadUpdatedEvent"),
})
public class LoadEvent implements Serializable {
    private static final long serialVersionUID = 2895533085240328403L;

    private String uuid;

    private Integer id;

    private String name;

    private String content;
}
