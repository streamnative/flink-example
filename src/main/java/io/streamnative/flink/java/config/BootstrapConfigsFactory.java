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

package io.streamnative.flink.java.config;

import lombok.NonNull;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.flink.runtime.entrypoint.parser.ParserResultFactory;

import java.util.Properties;

/**
 * Parser factory which generates a custom {@link BootstrapConfigs} of command line arguments.
 */
public class BootstrapConfigsFactory implements ParserResultFactory<BootstrapConfigs> {

    private static final Option CUSTOM_CONFIGS = Option.builder("C")
        .required(false)
        .hasArg(true)
        .numberOfArgs(2)
        .desc("Override the skywalker settings through command line.")
        .valueSeparator('=')
        .build();

    @Override
    public Options getOptions() {
        return new Options().addOption(CUSTOM_CONFIGS);
    }

    @Override
    public BootstrapConfigs createResult(@NonNull CommandLine commandLine) {
        Properties properties = commandLine.getOptionProperties(CUSTOM_CONFIGS.getOpt());
        return new BootstrapConfigs().setProperties(properties);
    }
}
