# '2ez' Project requirements

## Table of contents

* [Goals](#goals)
* [Project objectives](#project-objectives)
* [Business requirements](#business-requirements)
* [Functional requirements](#functional-requirements)
* [Non-Functional requirements](#non-functional-requirements)
* [Architecture and technical details](#architecture-and-technical-details)

## Goals

 1. Development of software product for obtaining and processing financial data for [Brent oil](https://en.wikipedia.org/wiki/Brent_Crude)
 2. Stable software capable of prediction prices for Brent oil

## Project objectives

 1. Real-time data processing
 2. Accurate price prediction
 3. Support of obtaining historical Brent oil price data
 4. Graphic display of historical, real-time and predicted data

## Business requirements

 1. Project represents web-site which contains several pages
 2. Pages must contain:
    * graphical representation of historical price data in user-defined date scope
    * predicted prices in user-defined date scope
    * real-time price ticker with date and time for last price
    * greetings page explining project goals and navigation bar through all content
 3. Project consists of several parts with different functionalities. At least:
    * [REST API](https://en.wikipedia.org/wiki/Representational_state_transfer) for collecting whole project functionalities in one control panel
    * Database for storing all predicted, real-time and historical data
    * Data collector for real-time and historical data
    * Predictor

## Functional requirements

 1) UI as control panel for end users
 2) Prediction for historical and real-time data
 3) Graphical representation for results
 4) Permanent storage
 5) Ability to change prediction algorithm on-user-demand

## Non-Functional requirements

 1. Project must be able to train at least for 20 seconds and accurate(at least 75% for prediction)
 2. UI must be convenient for end-user
 3. Data can be obtained from different sources
    * [X-markets](https://www.xmarkets.db.com/DE/ENG/Underlying-Detail/XC0009677409)
    * [Intrinio](https://intrinio.com/)
    * etc...
 4. Real-time data with once a second frequency
 5. Project supports only desktop browsers
 6. Project must be available 90% of the time
 7. Project must be available for at least 3 end-users, while it ran on a single machine

## Architecture and technical details

 1. Project must maintain [WebSocket](https://www.websocket.org/) technology for permanent connection and displaying real-time prices
 2. Project must store historical and real-time data in database(current is [MySQL](https://www.mysql.com/))
 3. Architecture of database suggests unique timestamps for each entry and three different tables for historical, real-time and predicted data
 4. Ability to predict Brent oil prices with machine learning algorithms(Self-written algorithms and libraries for example [Spark MLLib](https://spark.apache.org/docs/latest/index.html))
 5. REST API
=======
# Deutsche Bank cource project by 2ez team

## Description

It's a repository of our project. It consists of several modules:

  1. **common**: module for code shared between all modules
  2. **site**: main module representing main functionality
  3. **historyuploader**: module used to fill database with historical data
  4. **predictor**: prediction engine for historical and real-time processing

## Requirements

  Now project requires [Apache Kafka](https://kafka.apache.org/), which can be downloaded from [here](https://www.apache.org/dyn/closer.cgi?path=/kafka/2.1.0/kafka_2.12-2.1.0.tgz).
  Kafka need to be correctly configured and we will provide brief course how to do it.
  1. After you downloaded go to `config` directory where you can find 2 files: `server.properties` and `zookeeper.properties`.
  2. Make sure that in `zookeeper.properties` configuration `clientPort=2181` and `maxClientCnxns=1000` are set
  3. In `server.properties` variable `zookeeper.connect=localhost:2181` must be set.
  4. After that you can run Zookeeper as `./zookeeper-server-start.sh -daemon ../config/zookeeper.properties` from terminal.
  5. Now you need to create 2 topics in Kafka. For that use: `./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic [topic_name] --config retention.ms=2000` where topic names are **current_price** and **predicted_prices**. Configuration variable `retention.ms` sets frequency of topic purging.
  6. Now you are ready to run Kafka as message broker for whole system. Use: `./kafka-server-start.sh -daemon ../config/server.properties`
  7. Common issue with starting Kafka is that file `/etc/hosts/` does not contain mapping for localhost.

## Build

  Just use `mvn package` to build all modules at once

## Run

  Now module running order is sufficient so first run **historyuploader** then **predictor** and only then **site**. But breaking order will not cause any problems if only **historyuploader** initialized database.
