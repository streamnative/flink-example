# Flink Pulsar Integration Related Examples

We provided a set of examples on using the latest `flink-connector-pulsar` in the Flink repository. Showing the user how
to use this connector.

## The example list

1. `SimpleSource`: Consuming the message from Pulsar by using Flink's `StringSchema`.

## How to use

### Prepare the environment.

#### Manually

Download and set up a standalone Pulsar locally. Read this [tutorial](https://pulsar.apache.org/docs/en/standalone/) and enable the transaction in Pulsar.

#### Homebrew (for macOS and Linux)

If you use Homebrew, you can download our pre-written [Formula](https://github.com/streamnative/homebrew-streamnative). Just execute these commands below.

```shell
brew tap streamnative/streamnative

## Without install OpenJDK
brew install pulsar

## With an OpenJDK provided by Homebrew
brew install pulsar --with-openjdk
```

Then you need modify the broker configuration for enabling the Transaction manager.

```shell
cd `brew --prefix pulsar`

cd libexec/conf

vi standalone.conf
```

Change the `transactionCoordinatorEnabled=false` to `transactionCoordinatorEnabled=true`.
Start Pulsar standalone by executing `pulsar standalone`.

### Install `pulsarctl`

After install and setup the Pulsar standalone, we need some management tools for operating on the Pulsar cluster. We prefer to use [pulsarctl](https://github.com/streamnative/pulsarctl) because it supports shell auto-completion. You can skip this section if you want to use the scripts bundled in Pulsar distribution.

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

All the code snippet shown below was using `pulsarctl`. You can convert it to Pulsar scripts by reading [the documentation](https://pulsar.apache.org/docs/en/admin-api-overview/) for the Pulsar admin interface.

1. Create the `sample` tenant on Pulsar.

```shell
## Mark sure you have the standalone cluster.
pulsarctl clusters list standalone

## Create tenant
pulsarctl tenants create sample --allowed-clusters="standalone"
```

2. Create the `flink` namespace below `sample` tenant.

```shell
pulsarctl namespaces create sample/flink
```

3. Create topic `simple-string` with 8 partition under `flink` namespace.

```shell
pulsarctl topics create sample/flink/simple-string 8
```

Execute `pulsarctl topics list sample/flink` make sure we would list a set of topics like below.

```text
+-----------------------------------------------------+---------------+
|                     TOPIC NAME                      | PARTITIONED ? |
+-----------------------------------------------------+---------------+
| persistent://sample/flink/simple-string             | Y             |
| persistent://sample/flink/simple-string-partition-0 | N             |
| persistent://sample/flink/simple-string-partition-1 | N             |
| persistent://sample/flink/simple-string-partition-2 | N             |
| persistent://sample/flink/simple-string-partition-3 | N             |
| persistent://sample/flink/simple-string-partition-4 | N             |
| persistent://sample/flink/simple-string-partition-5 | N             |
| persistent://sample/flink/simple-string-partition-6 | N             |
| persistent://sample/flink/simple-string-partition-7 | N             |
+-----------------------------------------------------+---------------+
```

### Execute the program in IntelliJ IDEA

We share the IDEA run configuration in `.run` directory. You can choose the example case in IDEA's `Run Configuration` and execute it.
