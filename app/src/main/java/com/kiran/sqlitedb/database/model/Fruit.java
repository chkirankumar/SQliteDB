package com.kiran.sqlitedb.database.model;

public class Fruit {
    public static final String TABLE_NAME = "fruits";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FRUIT = "fruit";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String fruit;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_FRUIT + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public Fruit() {
    }

    public Fruit(int id, String fruit, String timestamp) {
        this.id = id;
        this.fruit = fruit;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getFruit() {
        return fruit;
    }

    public void setFruit(String fruit) {
        this.fruit = fruit;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}