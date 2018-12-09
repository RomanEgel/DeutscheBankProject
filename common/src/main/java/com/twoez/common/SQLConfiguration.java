package com.twoez.common;

import java.io.Serializable;

public class SQLConfiguration implements Serializable {
    private final String DB_NAME;

    private final String LOGIN;

    private final String PASSWORD;

    public SQLConfiguration(String dbName, String login, String password) {
        if(dbName == null || login == null){
            throw new IllegalArgumentException("Database name and login must be provided");
        }
        DB_NAME = dbName;
        LOGIN = login;
        PASSWORD = password;
    }

    public String name() {
        return DB_NAME;
    }

    public String login() {
        return LOGIN;
    }

    public String password() {
        return PASSWORD;
    }
}
