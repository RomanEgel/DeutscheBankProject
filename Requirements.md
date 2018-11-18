# '2ez' Project requirements

## Table of contents

* [Goals](#goals)
* [Project objectives](#project-objectives)
* [Business requirements](#business-requirements)
* [Functional requirements](#functional-requirements)
* [Non-Functional requirements](#non-functional-requirements)
* [Current state](#current-state)
* [Plans for future](#plans-for-future)
* [Restrictions](#restrictions)

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

 1. Project must maintain [WebSocket](https://www.websocket.org/) technology for permanent connection and displaying real-time prices
 2. Store historical and real-time data in database(current is [MySQL](https://www.mysql.com/))
 3. Ability to predict Brent oil prices with machine learning algorithms(Self-wrietten algorithms and libraries for example [Spark MLLib](https://spark.apache.org/docs/latest/index.html))
 4. REST-API built on [Spring](https://spring.io/) engine

## Non-Functional requirements

 1. Project must be able to process data fast and accurate
 2. UI must be convenient for end-user
 3. Data can be obtained from different sources
    * [X-markets](https://www.xmarkets.db.com/DE/ENG/Underlying-Detail/XC0009677409)
    * [Intrinio](https://intrinio.com/)
    * etc...

## Current state

 1. System is able to obtain historical and real-time data with less frequency then required
 2. Prediction is not fully implemented
 3. System is able to store historical data

## Plans for future

 1. Implementation real-time data obtaining once a second
 2. Implementation of prediction engine
 3. Implementation of new database architecture with unique timestamps and three tables for predicted, historical and real-time data
 4. Redesign of UI for more convenient usage

## Restrictions

  1. Real-time data with once a second frequency
  2. Prediction accuracy at least 75%