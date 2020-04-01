package com.kiran.sqlitedb.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kiran.sqlitedb.database.model.Fruit;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "fruits_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create fruits table
        db.execSQL(Fruit.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Fruit.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertFruit(String fruit) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Fruit.COLUMN_FRUIT, fruit);

        // insert row
        long id = db.insert(Fruit.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Fruit getFruit(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Fruit.TABLE_NAME,
                new String[]{Fruit.COLUMN_ID, Fruit.COLUMN_FRUIT, Fruit.COLUMN_TIMESTAMP},
                Fruit.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare fruit object
        Fruit fruit = new Fruit(
                cursor.getInt(cursor.getColumnIndex(Fruit.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Fruit.COLUMN_FRUIT)),
                cursor.getString(cursor.getColumnIndex(Fruit.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return fruit;
    }

    public List<Fruit> getAllFruits() {
        List<Fruit> fruits = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Fruit.TABLE_NAME + " ORDER BY " +
                Fruit.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Fruit fruit = new Fruit();
                fruit.setId(cursor.getInt(cursor.getColumnIndex(Fruit.COLUMN_ID)));
                fruit.setFruit(cursor.getString(cursor.getColumnIndex(Fruit.COLUMN_FRUIT)));
                fruit.setTimestamp(cursor.getString(cursor.getColumnIndex(Fruit.COLUMN_TIMESTAMP)));

                fruits.add(fruit);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return fruits list
        return fruits;
    }

    public int getFruitsCount() {
        String countQuery = "SELECT  * FROM " + Fruit.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateFruit(Fruit fruit) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Fruit.COLUMN_FRUIT, fruit.getFruit());

        // updating row
        return db.update(Fruit.TABLE_NAME, values, Fruit.COLUMN_ID + " = ?",
                new String[]{String.valueOf(fruit.getId())});
    }

    public void deleteFruit(Fruit fruit) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Fruit.TABLE_NAME, Fruit.COLUMN_ID + " = ?",
                new String[]{String.valueOf(fruit.getId())});
        db.close();
    }
}
