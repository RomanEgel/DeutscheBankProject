package com.twoez.historyuploader;


import picocli.CommandLine;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
@CommandLine.Command(name = "Uploader", mixinStandardHelpOptions = true, version = "0.1")
public class Uploader implements Callable<Integer> {

    @CommandLine.Option(names = {"-d","--db_type"}, required = true, description = "Type of database where you want to store your prices.\n" + "Currently available: [mysql]")

    String dbType;

    @CommandLine.Option(names = {"-n", "--db_name"},required = true, description = "Name of database where to store prices")

    String dbName;

    @CommandLine.Option(names = {"-u", "--user_name"}, required = true, description = "Login to your database")

    String login;

    @CommandLine.Option(names = {"-p","--password"}, required = false, description = "Password paired with provided login if it required")

    String password;

    Map<String, DBUploader> uploadingRules = new HashMap<>();

    public static void main(String[] args) throws IOException {
        CommandLine.call(new Uploader(), args);
    }

    private Uploader(){
        uploadingRules.put("MYSQL", new MySqlUploader());
    }

    @Override
    public Integer call() throws Exception {
        DBUploader uploader = uploadingRules.get(dbType.toUpperCase());
        uploader.setConfiguration(new SQLConfiguration(dbName,login,password));
        System.out.println("Starting downloading prices");
        JSONPriceGetter getter = new JSONPriceGetter();
        Map<Timestamp, Double> prices = getter.getPrices();
        System.out.println("Prices downloaded");
        uploader.fillDB(prices);
        System.out.println("Database filled");
        return 0;
    }
}
