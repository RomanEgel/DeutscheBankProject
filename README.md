# Deutsche Bank cource project by 2ez team

## Description

It's a repository of our project. It consists of several modules:

  1. **common**: module for code shared between all modules
  2. **site**: main module representing main functionality
  3. **historyuploader**: module used to fill database with historical data
  4. **predictor**: prediction engine for historical and real-time processing

## Requirements

* Now project requires [Apache Kafka](https://kafka.apache.org/), which can be downloaded from [here](https://www.apache.org/dyn/closer.cgi?path=/kafka/2.1.0/kafka_2.12-2.1.0.tgz).
  Kafka need to be correctly configured and we will provide brief course how to do it.
  1. After you downloaded go to `config` directory where you can find 2 files: `server.properties` and `zookeeper.properties`.
  2. Make sure that in `zookeeper.properties` configuration `clientPort=2181` and `maxClientCnxns=1000` are set
  3. In `server.properties` variables `zookeeper.connect=localhost:2181` and `host.name=localhost` must be set. Last to allow connection to Kafka when network connection is not available.
  4. After that you can run Zookeeper as `./zookeeper-server-start.sh -daemon ../config/zookeeper.properties` from terminal.
  5. Now you are ready to run Kafka as message broker for whole system. Use: `./kafka-server-start.sh ../config/server.properties`
  6. Now you need to create 2 topics in Kafka. For that use: `./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic [topic_name] --config retention.ms=2000` where topic names are **current_price** and **predicted_prices**. Configuration variable `retention.ms` sets frequency of topic purging.
  7. Common issue with starting Kafka is that file `/etc/hosts/` does not contain mapping for localhost.

* [MySQL](https://www.mysql.com/) also required as data storage

## Build

  Just use `mvn package` to build all modules at once

## Run

  Now module running order is sufficient so first run **historyuploader** then anything else you want. Modules will wait for updates, so prediction will show up on frontpage only if **predictor** was ran.