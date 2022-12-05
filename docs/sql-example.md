
# Flink SQL Connector for Pulsar Examples

In this guide, we are gonna cover 4 major uses cases.


1. Create Pulsar Catalogs.
2. Run window query against an `explicit` table.
3. Write to and query an `explicit` table.
4. Write to and query a `native` table.



### Prepare the Pulsar + Flink environment

Run the following command to spawn up a running Pulsar cluster and a Flink container.

```bash
docker-compose run flink
```
We then are logged onto the Flink container.

> Run `docker/create-topics.sh` outside the Flink container to create topics that we will be using in this guide if you haven't run it in previous steps.


Now, let's first take a look at our installation by examining the `lib/` folder.
```shell
ls lib/
```
The `lib/` folder is contains the Pulsar SQL connector jar and dependencies for Pulsar SQL connector. 


### Start Flink Cluster and SQL Client
Now, the Flink cluster is not started yet. Run the following command to start the Flink session cluster.
```shell
./bin/start-cluster.sh
```

And start the sql client which is connected to the Flink session cluster.

```shell
./bin/sql-client.sh
```



### Create a PulsarCatalog

Now, let's create a PulsarCatalog. It connects to the Pulsar docker instance we spawn up using the `docker-compose` command.
```shell
 CREATE CATALOG pulsar
  WITH (
    'type' = 'pulsar-catalog',
    'catalog-admin-url' = 'http://pulsar:8080',
    'catalog-service-url' = 'pulsar://pulsar:6650'
  );
```

Let's examine the avaialbe databases;

```shell
SHOW CURRENT DATABASE;
SHOW DATABASES;
```

The first command gives `default`. However this database is not created yet.

The second command should contain a list of databases in `tenant/namespace` format. These database are indeed
mapped from a Pulsar namespace. These databases contains the `native` tables.


Create an `explict` database in the catalog; Please execute one SQL query at a time.
```shell
USE CATALOG pulsar;
CREATE DATABASE sql_examples;

USE sql_examples;
```


### Window query against an `explicit` table

Create a `user` table. Users don't have to provide the service url or admin url. PulsarCatalog uses the same address when creating the catalog if no address is provided.

```shell
CREATE TABLE sql_user (
    name STRING,
    age INT,
    income DOUBLE,
    single BOOLEAN,
    createTime BIGINT,
    row_time AS cast(TO_TIMESTAMP(FROM_UNIXTIME(createTime / 1000), 'yyyy-MM-dd HH:mm:ss') as timestamp(3)),
    WATERMARK FOR row_time AS row_time - INTERVAL '5' SECOND
) WITH (
  'connector' = 'pulsar',
  'topics' = 'persistent://sample/flink/user',
  'format' = 'json'
);
```

Run the producer program `ExampleUserProducer`. Users can use Intellij to run `SqlJsonExampleProducer`. It will send
an `ExampleUser` record to the topic every 100 ms. 

Next we can run a window query for a 10 seconds tumbling window:

```shell
SELECT single,
 TUMBLE_START(row_time, INTERVAL '10' SECOND) AS sStart,
 SUM(age) as age_sum from `sql_user`
 GROUP BY TUMBLE(row_time, INTERVAL '10' SECOND), single;
```

### Write to and query an `explicit` table.
First let's stop the `ExampleUserProducer` java program and stop the SQL query, as we will insert into the same topic using Flink.

Then, execute the following SQL quries:
```sql
INSERT INTO `sql_user` VALUES ('user 1', 11, 25000.0, true, 1656831003);
```


Then we can see if the result is inserted by query that record.

```sql
SELECT * FROM `sql_user` WHERE name='user 1';
```


### Write to and query a `native` table
What if users want to query from a Pulsar topic directly without the CREATE statements?
Let's see how we can do this.

First we need to change the database to use.
```sql
USE `sample/flink`;
```

PulsarCatalog maps a `tenant/namespace` to a Flink database. Let's see what tables we have.

```shell
SHOW TABLES;
```

Pulsar connector allow users to query directly from a partition (or multiple partitions). Here we will query directly from the `user` table.

```sql
SELECT * FROM `user` LIMIT 10; 
```

We can insert to a native table as well.

```sql
INSERT INTO `user` VALUES (26, 1656831003,  25000.0, 'Bob', false);
```

> Please note the order of fields is different from the previous one. This is because in native mode, the column order is decided by the Pulsar Schema of the topic.

Lastly, we select the user `Bob`.

```sql
SELECT * FROM `user` WHERE name='Bob'; 
```


# Conclusion
Now you have got a basic feeling how the sql connector works. For more detailed uses documentation,
please go to [SQL Connector for Pulsar documentation](https://hub.streamnative.io/data-processing/pulsar-flink/1.15.0.1) for more details.

We plan to add more SQL cookbooks around FLink SQL Connector for Pulsar. 
