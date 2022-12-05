#!/usr/bin/env bash
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

command -v pulsarctl >/dev/null 2>&1 || { echo >&2 "Require pulsarctl but it's not installed. Aborting."; exit 1; }

pulsarctl clusters list standalone
pulsarctl tenants create sample --allowed-clusters="standalone"
pulsarctl namespaces create sample/flink
pulsarctl topics create sample/flink/simple-string 8
pulsarctl topics create sample/flink/load-event 8
pulsarctl topics create sample/flink/user 4
pulsarctl topics list sample/flink
