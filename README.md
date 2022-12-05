# Flink Pulsar Integration Related Examples

We provided a set of examples on using the latest `flink-connector-pulsar` in the Flink repository.
Showing the user how to use this connector.

## The example list

1. `SimpleSource`: Consuming the message from Pulsar by using Flink's `StringSchema`.
2. `SimpleSink`: Write the message into Pulsar by using Flink's `StringSchema`.
3. `LoadEventSink`: Write load event messages with multiple sub-classes into Pulsar by `JSONSchema`.
4. `LoadEventSource`: Consuming load event messages from Pulsar by `JSONSchema` and filter the `LoadCreateEvent` class.

## How to use

### Prepare the Pulsar instance

#### Docker Compose (Recommend)

Docker compose is quite easy to use. Simply execute `docker compose run pulsar` in project root directory.

#### Docker command

We use docker to run an operable Pulsar environment. All the thing you need to do is just one command.

```shell
cd "${this flink-example project directory}"

sudo docker run -it \
  -p 6650:6650 \
  -p 8080:8080 \
  --mount type=bind,source=${PWD}/docker/data,target=/pulsar/data \
  --mount type=bind,source=${PWD}/docker/bootstrap.sh,target=/pulsar/bin/bootstrap.sh \
  apachepulsar/pulsar:2.10.2 \
  /pulsar/bin/bootstrap.sh
```

### Install `pulsarctl`

After install and setup the Pulsar standalone, we need some management tools for operating on the Pulsar cluster.
We prefer to use [pulsarctl](https://github.com/streamnative/pulsarctl) because it supports shell auto-completion.
You can skip this section if you want to use the scripts bundled in Pulsar distribution.

#### Mac operating system

Use [homebrew](https://brew.sh/) to install `pulsarctl` on the Mac operating system.

```bash
brew tap streamnative/streamnative
brew install pulsarctl
```

We would auto install zsh-completion and bash-completion when you use Homebrew.

#### Linux operating system

Use this command to install `pulsarctl` on the Linux operating system.

```bash
sh -c "$(curl -fsSL https://raw.githubusercontent.com/streamnative/pulsarctl/master/install.sh)"
```

#### Windows operating system

To install `pulsarctl` on the Windows operating system, follow these steps:

1. Download the package from [here](https://github.com/streamnative/pulsarctl/releases).
2. Add the `pulsarctl` directory to your system PATH.
3. Execute `pulsarctl -h`  to verify that `pulsarctl` is work.

### Prepare the test dataset

All the code snippet shown below was using `pulsarctl`. You can convert it to Pulsar scripts
by reading [the documentation](https://pulsar.apache.org/docs/en/admin-api-overview/) for the Pulsar admin interface.

1. Create and use pulsarctl's context for connecting to a Pulsar standalone instance.

```shell
## Change the 192.168.50.8 to your Pulsar standalone address. 
pulsarctl context set development --admin-service-url="http://192.168.50.8:8080"

## Use the created context
pulsarctl context use development
```

2. Execute the initialization scripts. `docker/create-topics.sh`

### Execute the program in IntelliJ IDEA for DataStream Connectors

All the required configurations are defined in `configs.yml` file. Change the `serviceUrl` and `adminUrl` to your
pulsar standalone address.

We share the IDEA run configuration in `.run` directory. You can choose the example case in IDEA's `Run Configuration` and execute it.


### Try the SQL Connector in docker container

Please go to [SQL examples](docs/sql-example.md)

## Reference

* [Apache Pulsar Connector](https://nightlies.apache.org/flink/flink-docs-master/docs/connectors/datastream/pulsar/)
