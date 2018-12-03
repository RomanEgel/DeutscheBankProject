package com.twoez.historyuploader;

import java.sql.*;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class MySqlUploader implements DBUploader {

    private Connection connection;

    private String unformatedURL = "jdbc:mysql://localhost/%s?useLegacyDatetimeCode=false&serverTimezone=America/New_York&user=%s%s";

    private SQLConfiguration configuration;

    private String TABLE_NAME = "brent_oil";

    private String createTableCommand = "create table if not exists " + TABLE_NAME +" (price_timestamp datetime, price double, source varchar(50), PRIMARY KEY(price_timestamp, source))";

    MySqlUploader(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (Exception ex){
            throw new RuntimeException("Driver for MySQL database was not found");
        }
    }

    @Override
    public void fillDB(final Map<Timestamp, Double> priceData) {
        if (configuration == null){
            throw new IllegalStateException("Configuration was not provided");
        }
        try {
            System.out.println("Creating table " + TABLE_NAME);
            Statement createTableStatement = connection.createStatement();
            createTableStatement.execute(createTableCommand);
        } catch (SQLException ex){
            throw new RuntimeException("Unable to create table");
        }
        System.out.println("Table created\nStarting filling insert statement...");
        StringBuilder commandBuilder = new StringBuilder();
        String insert_statement_initializer = "insert into " + TABLE_NAME + " values ";
        commandBuilder.append(insert_statement_initializer);
        float current = 0;
        float total = priceData.size();
        for(Map.Entry<Timestamp, Double> entry : priceData.entrySet()){
            String timestamp = entry.getKey().toString();
            String formattedValue = "('" + timestamp.substring(0, timestamp.length() - 2) + "', " + String.format(Locale.ROOT, "%.2f", entry.getValue()) + ", '" + JSONPriceGetter.SOURCE_NAME + "')";
            commandBuilder.append(formattedValue);
            current++;
            String command = commandBuilder.toString();
            System.out.printf("Progress: %.2f%%\r", (current/total) * 100);
            System.out.flush();
            commandBuilder.append(",");
        }
        commandBuilder.deleteCharAt(commandBuilder.length() - 1);
        System.out.println("Statement created");
        try{
            System.out.println("Starting filling database...");
            Statement insertStatement = connection.createStatement();
            insertStatement.executeUpdate(commandBuilder.toString());
        } catch (SQLException ex){
            ex.printStackTrace();
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
        System.out.println("Configuration for MySQL set");
    }
}
