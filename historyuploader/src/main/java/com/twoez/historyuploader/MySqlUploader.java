package com.twoez.historyuploader;

import com.twoez.common.SQLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.Map;

public class MySqlUploader implements DBUploader{

    private Logger logger = LoggerFactory.getLogger(MySqlUploader.class);

    private Connection connection;

    private String unformatedURL = "jdbc:mysql://localhost/%s?useLegacyDatetimeCode=false&serverTimezone=America/New_York&user=%s%s";

    private SQLConfiguration configuration;

    private String TABLE_NAME = "brent_oil";

    private String createTableCommand = "create table if not exists " + TABLE_NAME +" (price_timestamp datetime, price double, source varchar(50), PRIMARY KEY(price_timestamp, source))";

    private final String sourceName;
    public MySqlUploader(String source){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (Exception ex){
            throw new RuntimeException("Driver for MySQL database was not found");
        }
        sourceName = source;
    }

    @Override
    public void fillDB(final Map<Timestamp, Double> priceData) {
        if (configuration == null){
            throw new IllegalStateException("Configuration was not provided");
        }
        try {
            logger.info("Creating table " + TABLE_NAME);
            Statement createTableStatement = connection.createStatement();
            createTableStatement.execute(createTableCommand);
        } catch (SQLException ex){
            throw new RuntimeException("Unable to create table");
        }
        logger.info("Table created\nStarting filling insert statement...");
        StringBuilder commandBuilder = new StringBuilder();
        String insert_statement_initializer = "insert into " + TABLE_NAME + " values ";
        commandBuilder.append(insert_statement_initializer);
        String value = "(\'%s\',%.2f, \'%s\')";
        float current = 0;
        float total = priceData.size();
        for(Map.Entry<Timestamp, Double> entry : priceData.entrySet()){
            String timestamp = entry.getKey().toString();
            String formattedValue = String.format(value, timestamp.substring(0, timestamp.length() - 2), entry.getValue(),sourceName);
            commandBuilder.append(formattedValue);
            current++;
            String command = commandBuilder.toString();
            System.out.printf("Progress: %.2f%%\r", (current/total) * 100);
            System.out.flush();
            commandBuilder.append(",");
        }
        commandBuilder.deleteCharAt(commandBuilder.length() - 1);
        logger.info("Statement created");
        try{
            logger.info("Starting filling database...");
            Statement insertStatement = connection.createStatement();
            insertStatement.executeUpdate(commandBuilder.toString());
        } catch (SQLException ex){
            throw new RuntimeException("Unable to insert data");
        }
    }

    @Override
    public void setConfiguration(final SQLConfiguration configuration) {
        this.configuration = configuration;
        try {
            String connectionURL = String.format(unformatedURL,configuration.name(), configuration.login(), ((configuration.password() != null) ? "&password=" + configuration.password() : ""));
            connection = DriverManager.getConnection(connectionURL);
        } catch (SQLException ex){
            throw new RuntimeException("Unable to connect to requested database\n" +
                    "Database name: " + configuration.name() + " Login: " + configuration.login() + "\n" + ex.getMessage());
        }
        logger.info("Configuration for MySQL set");
    }

    @Override
    public void createDatasetProcedure(){
        String dropQuery = "drop procedure if exists create_dataset_table";
        logger.info("Dropping procedure");
        try {
            Statement dropStatement = connection.createStatement();
            dropStatement.executeUpdate(dropQuery);
        } catch (SQLException ex){
            throw new RuntimeException("Unable to drop procedure\n" + ex.getMessage());
        }

        logger.info("Procedure dropped");
        logger.info("Creating procedure");
        String procedureCreateStatement = "create procedure create_dataset_table() READS SQL DATA begin drop table if exists new_table; drop table if exists next_price_table; create table new_table(id int AUTO_INCREMENT,price double,next_price double, PRIMARY KEY(id)); insert into new_table (price) select price from brent_oil where source = 'RealTimeXMarkets' or source = 'HistoryProfinance' order by price_timestamp; create table next_price_table(id int AUTO_INCREMENT,next_price double, PRIMARY KEY(id)); insert into next_price_table(next_price) select price as next_price from((select price_timestamp, price from brent_oil where (price_timestamp != (select min(price_timestamp) from brent_oil where source = 'RealTimeXMarkets' or source = 'HistoryProfinance')) and (source = 'RealTimeXMarkets' or source = 'HistoryProfinance') order by price_timestamp) union all (select price_timestamp, price from brent_oil where (price_timestamp = (select max(price_timestamp) from brent_oil where source = 'RealTimeXMarkets' or source = 'HistoryProfinance')) and (source = 'RealTimeXMarkets' or source = 'HistoryProfinance'))) as tmp order by price_timestamp; update new_table join next_price_table on new_table.id = next_price_table.id set new_table.next_price = next_price_table.next_price;  end";
        try{
            Statement createStatement = connection.createStatement();
            createStatement.executeUpdate(procedureCreateStatement);
        } catch (SQLException ex){
            throw new RuntimeException("Unable to create procedure\n" + ex.getMessage());
        }

        logger.info("Procedure created");
        logger.info("Calling procedure and creating dataset");
        String callQuery = "call create_dataset_table()";
        try{
            Statement callStatement = connection.createStatement();
            callStatement.executeUpdate(callQuery);
        } catch (SQLException ex){
            throw new RuntimeException("Unable to call procedure\n" + ex.getMessage());
        }
        logger.info("Dataset created");
    }

    @Override
    public void createDailyDatasetAndTrigger(){
        String turnUpdateSchedulerOnQuery = "SET GLOBAL event_scheduler = on";
        logger.info("Turning on update scheduler");
        try{
            Statement turnScheduler = connection.createStatement();
            turnScheduler.executeUpdate(turnUpdateSchedulerOnQuery);
        } catch (SQLException ex){
            throw new RuntimeException("Unable to turn event scheduler on\n" + ex.getMessage());
        }
        logger.info("Scheduler turned on");

        String dropExistingEvent = "drop event if exists call_update_daily";
        logger.info("Dropping existing event");
        try{
            Statement dropEvent = connection.createStatement();
            dropEvent.executeUpdate(dropExistingEvent);
        } catch (SQLException ex){
            throw new RuntimeException("Unable to drop event\n" + ex.getMessage());
        }
        logger.info("Event dropped");

        String dropProcedureQuery = "drop procedure if exists update_daily";
        logger.info("Dropping update procedure");
        try{
            Statement dropProcedure = connection.createStatement();
            dropProcedure.executeUpdate(dropProcedureQuery);
        }catch (SQLException ex){
            throw new RuntimeException("Unable to drop procedure\n" + ex.getMessage());
        }
        logger.info("Procedure dropped");

        String createProcedureQuery = "create procedure update_daily() READS SQL DATA begin drop table if exists daily_table; drop table if exists daily_next_prices; create table daily_table(id int AUTO_INCREMENT,price double,next_price double, PRIMARY KEY(id)); insert into daily_table (price) select price from brent_oil where (source = 'RealTimeXMarkets' or source = 'HistoryProfinance') and(price_timestamp < (select max(price_timestamp) from brent_oil where source = 'RealTimeXMarkets' or source = 'HistoryProfinance') and price_timestamp > (select DATE_SUB((select max(price_timestamp) from brent_oil where source = 'RealTimeXMarkets' or source = 'HistoryProfinance'), INTERVAL 1 DAY))) order by price_timestamp; create table daily_next_prices(id int AUTO_INCREMENT,next_price double, PRIMARY KEY(id)); insert into daily_next_prices(next_price) select price as next_price from((select id, price from daily_table where (id != (select min(id) from daily_table)) order by id) union all (select id, price from daily_table where (id = (select max(id) from daily_table)))) as tmp order by id; update daily_table join daily_next_prices on daily_table.id = daily_next_prices.id set daily_table.next_price = daily_next_prices.next_price;  end";
        logger.info("Creating procedure");
        try{
            Statement createProcedure = connection.createStatement();
            createProcedure.executeUpdate(createProcedureQuery);
        } catch (SQLException ex){
            throw new RuntimeException("Unable to create procedure\n" + ex.getMessage());
        }
        logger.info("Procedure created");

        String createEventQuery = "create event call_update_daily ON SCHEDULE EVERY 5 second STARTS CURRENT_TIMESTAMP do begin call update_daily(); end";
        logger.info("Creating event");
        try{
            Statement createEvent = connection.createStatement();
            createEvent.executeUpdate(createEventQuery);
        }catch (SQLException ex){
            throw new RuntimeException("Unable to create event\n" + ex.getMessage());
        }
        logger.info("Event created");
    }
}
