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