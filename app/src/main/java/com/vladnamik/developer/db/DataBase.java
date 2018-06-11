package com.vladnamik.developer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vladnamik.developer.entities.Day;
import com.vladnamik.developer.entities.Dish;
import com.vladnamik.developer.entities.Exercise;
import com.vladnamik.developer.utils.MyDate;
import com.vladnamik.developer.utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataBase {
    private static final String DB_NAME = "CalorieCounterDB2";
    private static final int DB_VERSION = 1;

    private static final String DAYS_TABLE = "days";
    private static final String EXERCISE_TABLE = "exercises";
    private static final String DISH_TABLE = "dishes";
    private static final String DAYS_EXERCISE_TABLE = "days_exercises";
    private static final String DAYS_DISHES_TABLE = "days_dishes";

    public static final String COLUMN_ID = "_id";
    public static final String DAYS_COLUMN_DATE = "date";
    public static final String DAYS_COLUMN_RECORD = "record";
    public static final String EXERCISE_COLUMN_NAME = "exercise";
    public static final String EXERCISE_COLUMN_QUANTITY_COEFF = "quantity_coeff";
    public static final String EXERCISE_COLUMN_TIME_COEFF = "time_coeff";
    public static final String DISH_COLUMN_NAME = "dish";
    public static final String DISH_COLUMN_CALORIES_PER_100_GM = "calories";
    public static final String DAYS_EXERCISE_COLUMN_TIME = "time";
    public static final String DAYS_EXERCISE_COLUMN_QUANTITY = "quantity";
    public static final String DAYS_DISH_COLUMN_WEIGHT = "weight";

    private static final String sampleExercisesNames[] = {"Pulling up the rear grip", "Push-ups", "Jumping rope", "Running on the spot", "Pulling up the front grip", "Squats"};
    private static final int sampleExercisesTimeCoeff[] = {1, 1, 0, 0, 4, 6};
    private static final int sampleExercisesQuantityCoeff[] = {12, 7, 4, 0, 0, 6};
    private static final String sampleDishesNames[] = {"Rice", "Army noodles", "Potato", "Hot dog", "Tea", "Cucumbers", "Lemon", "Apple"};
    private static final int sampleDishesCalories[] = {117, 248, 148, 44, 785, 154, 556, 232};


    public static final int MAX_RECORD_WIDTH = 700;
    public static int dishSortId = 0; // will allow to keep the sorting we need for dishes when updating the screen
    public static int exercisesSortId = 0;

    private static Context context;
    private static DataBase database;
    private static DBHelper dbHelper; // required to create and update the database
    private static SQLiteDatabase mDB; // database management

    private DataBase(Context context) {
        DataBase.context = context;
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        mDB = dbHelper.getWritableDatabase();
    }

    public static DataBase getDataBase(Context context) {
        if (DataBase.context == null && database != null) {
            database.close();
        }
        if (database == null || DataBase.context == null) {
            database = new DataBase(context);
        }
        return database;
    }

    public void close() {
        if (dbHelper != null)
            dbHelper.close();
        database = null;
        context = null;
    }


    public Cursor getDishes() {
        switch (dishSortId) {
            case 1:
                return mDB.query(DISH_TABLE, null, null, null, null, null, DISH_COLUMN_NAME);
            case 2:
                return mDB.query(DISH_TABLE, null, null, null, null, null, DISH_COLUMN_CALORIES_PER_100_GM);
        }
        return mDB.query(DISH_TABLE, null, null, null, null, null, null);
    }

    public Dish getDish(String name) {
        Cursor cursor = mDB.query(DISH_TABLE, null, DISH_COLUMN_NAME + "='" + name + "'", null, null, null, null);
        if (!cursor.moveToFirst())
            return null;
        else
            return new Dish(cursor.getString(cursor.getColumnIndex(DISH_COLUMN_NAME)), cursor.getInt(cursor.getColumnIndex(DISH_COLUMN_CALORIES_PER_100_GM)));
    }

    public void addDish(String name, int calories) {
        ContentValues cv = new ContentValues();
        cv.put(DISH_COLUMN_NAME, name);
        cv.put(DISH_COLUMN_CALORIES_PER_100_GM, calories);
        mDB.insert(DISH_TABLE, null, cv);
    }

    public void addDish(Dish dish) {
        addDish(dish.getName(), dish.getCaloriesPer100Gm());
    }

    public void updateDish(String name, int calories) {
        ContentValues cv = new ContentValues();
        cv.put(DISH_COLUMN_NAME, name);
        cv.put(DISH_COLUMN_CALORIES_PER_100_GM, calories);
        mDB.update(DISH_TABLE, cv, DISH_COLUMN_NAME + "='" + name + "'", null);
    }

    public void updateDish(Dish dish) {
        updateDish(dish.getName(), dish.getCaloriesPer100Gm());
    }

    public void deleteDish(String name) {
        mDB.delete(DISH_TABLE, DISH_COLUMN_NAME + "='" + name + "'", null);
        mDB.delete(DAYS_DISHES_TABLE, DISH_COLUMN_NAME + "='" + name + "'", null);
    }


    public Cursor getExercises() {
        switch (exercisesSortId) {
            case 1:
                return mDB.query(EXERCISE_TABLE, null, null, null, null, null, EXERCISE_COLUMN_NAME);
            case 2:
                return mDB.query(EXERCISE_TABLE, null, null, null, null, null, EXERCISE_COLUMN_TIME_COEFF);
            case 3:
                return mDB.query(EXERCISE_TABLE, null, null, null, null, null, EXERCISE_COLUMN_QUANTITY_COEFF);
        }
        return mDB.query(EXERCISE_TABLE, null, null, null, null, null, null);
    }

    public Exercise getExercise(String name) {
        Cursor cursor = mDB.query(EXERCISE_TABLE, null, EXERCISE_COLUMN_NAME + "='" + name + "'", null, null, null, null);
        if (!cursor.moveToFirst())
            return null;
        else
            return new Exercise(cursor.getString(cursor.getColumnIndex(EXERCISE_COLUMN_NAME)), cursor.getInt(cursor.getColumnIndex(EXERCISE_COLUMN_TIME_COEFF)), cursor.getInt(cursor.getColumnIndex(EXERCISE_COLUMN_QUANTITY_COEFF)));

    }

    public void updateExercise(String name, int timeCoeff, int quantityCoeff) {
        ContentValues cv = new ContentValues();
        cv.put(EXERCISE_COLUMN_NAME, name);
        cv.put(EXERCISE_COLUMN_TIME_COEFF, timeCoeff);
        cv.put(EXERCISE_COLUMN_QUANTITY_COEFF, quantityCoeff);
        mDB.update(EXERCISE_TABLE, cv, EXERCISE_COLUMN_NAME + "='" + name + "'", null);
    }

    public void updateExercise(Exercise exercise) {
        updateExercise(exercise.getName(), exercise.getTimeCoefficient(), exercise.getQuantityCoefficient());
    }

    public void addExercise(String name, int timeCoeff, int quantityCoeff) {
        ContentValues cv = new ContentValues();
        cv.put(EXERCISE_COLUMN_NAME, name);
        cv.put(EXERCISE_COLUMN_TIME_COEFF, timeCoeff);
        cv.put(EXERCISE_COLUMN_QUANTITY_COEFF, quantityCoeff);
        mDB.insert(EXERCISE_TABLE, null, cv);
    }

    public void addExercise(Exercise exercise) {
        addExercise(exercise.getName(), exercise.getTimeCoefficient(), exercise.getQuantityCoefficient());
    }

    public void deleteExercise(String name) {
        mDB.delete(EXERCISE_TABLE, EXERCISE_COLUMN_NAME + "='" + name + "'", null);
        mDB.delete(DAYS_EXERCISE_TABLE, EXERCISE_COLUMN_NAME + "='" + name + "'", null);
    }


    public Cursor getDaysExercisesData() {
        return mDB.query(DAYS_EXERCISE_TABLE, null, null, null, null, null, null);
    }

    public Cursor getAllDayExercises(MyDate date) {
        Cursor cursor = mDB.query(DAYS_EXERCISE_TABLE, null, DAYS_COLUMN_DATE + "=" + date.getTime(), null, null, null, null);
        if (cursor.moveToFirst())
            return cursor;
        else
            return null;
    }

    public Cursor getDayExercise(MyDate date, String exerciseName) {
        Cursor cursor = mDB.query(DAYS_EXERCISE_TABLE, null, DAYS_COLUMN_DATE + " = " + date.getTime() + " AND " + EXERCISE_COLUMN_NAME + " = '" + exerciseName + "'", null, null, null, null);
        if (cursor.moveToFirst())
            return cursor;
        else
            return null;
    }

    public void updateDayExercise(MyDate date, String exerciseName, int time, int quantity) {
        ContentValues cv = new ContentValues();
        cv.put(DAYS_COLUMN_DATE, date.getTime());
        cv.put(EXERCISE_COLUMN_NAME, exerciseName);
        cv.put(DAYS_EXERCISE_COLUMN_QUANTITY, quantity);
        cv.put(DAYS_EXERCISE_COLUMN_TIME, time);
        mDB.update(DAYS_EXERCISE_TABLE, cv, DAYS_COLUMN_DATE + " = " + date.getTime() + " AND " + EXERCISE_COLUMN_NAME + " = '" + exerciseName + "'", null);
    }

    public void addDayExercise(MyDate date, String exerciseName, int time, int quantity) {
        if (time == 0 && quantity == 0)
            return;
        Cursor cursor = getDayExercise(date, exerciseName);
        if (cursor == null) {
            ContentValues cv = new ContentValues();
            cv.put(DAYS_COLUMN_DATE, date.getTime());
            cv.put(EXERCISE_COLUMN_NAME, exerciseName);
            cv.put(DAYS_EXERCISE_COLUMN_QUANTITY, quantity);
            cv.put(DAYS_EXERCISE_COLUMN_TIME, time);
            mDB.insert(DAYS_EXERCISE_TABLE, null, cv);
        } else {
            updateDayExercise(date, exerciseName, time, quantity);
        }
    }

    public void deleteDayExercise(MyDate date, String exerciseName) {
        mDB.delete(DAYS_EXERCISE_TABLE, DAYS_COLUMN_DATE + " = " + date.getTime() + " AND " + EXERCISE_COLUMN_NAME + " = '" + exerciseName + "'", null);
    }

    public void deleteDayExercises(MyDate date) {
        mDB.delete(DAYS_EXERCISE_TABLE, DAYS_COLUMN_DATE + " = " + date.getTime(), null);
    }

    public Cursor getDaysDishesData() {
        return mDB.query(DAYS_DISHES_TABLE, null, null, null, null, null, null);
    }

    public Cursor getAllDayDishes(MyDate date) {
        Cursor cursor = mDB.query(DAYS_DISHES_TABLE, null, DAYS_COLUMN_DATE + "=" + date.getTime(), null, null, null, null);
        if (cursor.moveToFirst())
            return cursor;
        else
            return null;
    }

    public Cursor getDayDish(MyDate date, String dishName) {
        Cursor cursor = mDB.query(DAYS_DISHES_TABLE, null, DAYS_COLUMN_DATE + " = " + date.getTime() + " AND " + DISH_COLUMN_NAME + " = '" + dishName + "'", null, null, null, null);
        if (cursor.moveToFirst())
            return cursor;
        else
            return null;
    }

    public void updateDayDish(MyDate date, String dishName, int weight) {
        ContentValues cv = new ContentValues();
        cv.put(DAYS_COLUMN_DATE, date.getTime());
        cv.put(DISH_COLUMN_NAME, dishName);
        cv.put(DAYS_DISH_COLUMN_WEIGHT, weight);
        mDB.update(DAYS_DISHES_TABLE, cv, DAYS_COLUMN_DATE + " = " + date.getTime() + " AND " + DISH_COLUMN_NAME + " = '" + dishName + "'", null);

    }

    public void addDayDish(MyDate date, String dishName, int weight) {
        if (weight == 0)
            return;
        Cursor cursor = getDayDish(date, dishName);
        if (cursor == null) {
            ContentValues cv = new ContentValues();
            cv.put(DAYS_COLUMN_DATE, date.getTime());
            cv.put(DISH_COLUMN_NAME, dishName);
            cv.put(DAYS_DISH_COLUMN_WEIGHT, weight);
            mDB.insert(DAYS_DISHES_TABLE, null, cv);
        } else {
            updateDayDish(date, dishName, weight);
        }
    }

    public void deleteDayDish(MyDate date, String dishName) {
        mDB.delete(DAYS_DISHES_TABLE, DAYS_COLUMN_DATE + " = " + date.getTime() + " AND " + DISH_COLUMN_NAME + " = '" + dishName + "'", null);
    }

    public void deleteDayDishes(MyDate date) {
        mDB.delete(DAYS_DISHES_TABLE, DAYS_COLUMN_DATE + " = " + date.getTime(), null);
    }

    public Cursor getDaysRecords() {
        return mDB.query(DAYS_TABLE, null, null, null, null, null, null);
    }

    public Cursor getDayRecord(MyDate date) {
        Cursor cursor = mDB.query(DAYS_TABLE, null, DAYS_COLUMN_DATE + "=" + date.getTime(), null, null, null, null);
        if (!cursor.moveToFirst())
            return null;
        else
            return cursor;
    }

    public void updateDayRecord(MyDate date, String record) {
        ContentValues cv = new ContentValues();
        cv.put(DAYS_COLUMN_DATE, date.getTime());
        cv.put(DAYS_COLUMN_RECORD, record);
        mDB.update(DAYS_TABLE, cv, DAYS_COLUMN_DATE + " = " + date.getTime(), null);
    }

    public void addDayRecord(MyDate date, String record) {
        ContentValues cv = new ContentValues();
        cv.put(DAYS_COLUMN_DATE, date.getTime());
        cv.put(DAYS_COLUMN_RECORD, record);
        mDB.insert(DAYS_TABLE, null, cv);
    }

    public void deleteDayRecord(MyDate date) {
        mDB.delete(DAYS_TABLE, DAYS_COLUMN_DATE + " = " + date.getTime(), null);
    }


    public Day getDay(MyDate date) {
        Day day = new Day(date);
        if (getDayRecord(date) == null)
            return day;
        int cc = getDayRecord(date).getColumnIndex(DAYS_COLUMN_RECORD);
        String rec = getDayRecord(date).getString(cc);
        day.setRecord(rec);
        Cursor cursor = getAllDayDishes(date);
        if (cursor != null) {
            do {
                day.addDish(getDish(cursor.getString(cursor.getColumnIndex(DISH_COLUMN_NAME))), cursor.getInt(cursor.getColumnIndex(DAYS_DISH_COLUMN_WEIGHT)));
            } while (cursor.moveToNext());
        }
        cursor = getAllDayExercises(date);
        if (cursor != null) {
            do {
                day.addExercise(getExercise(cursor.getString(cursor.getColumnIndex(EXERCISE_COLUMN_NAME))), cursor.getInt(cursor.getColumnIndex(DAYS_EXERCISE_COLUMN_TIME)), cursor.getInt(cursor.getColumnIndex(DAYS_EXERCISE_COLUMN_QUANTITY)));
            } while (cursor.moveToNext());
        }
        return day;
    }

    public void saveDay(Day day) {
        MyDate date = day.getDate();
        Cursor cursor;
        cursor = getDayRecord(date);
        if (cursor == null)
            addDayRecord(date, day.getRecord());
        else
            updateDayRecord(date, day.getRecord());
        cursor = getAllDayDishes(date);
        if (cursor == null)
            for (Map.Entry<Dish, Integer> dish : day.getDishes().entrySet())
                addDayDish(date, dish.getKey().getName(), dish.getValue());
        else {
            deleteDayDishes(date);
            for (Map.Entry<Dish, Integer> dish : day.getDishes().entrySet())
                addDayDish(date, dish.getKey().getName(), dish.getValue());
        }
        cursor = getAllDayExercises(date);
        if (cursor == null)
            for (Map.Entry<Exercise, Pair<Integer, Integer>> exercise : day.getExercises().entrySet())
                addDayExercise(date, exercise.getKey().getName(), exercise.getValue().first, exercise.getValue().second);
        else {
            deleteDayExercises(date);
            for (Map.Entry<Exercise, Pair<Integer, Integer>> exercise : day.getExercises().entrySet())
                addDayExercise(date, exercise.getKey().getName(), exercise.getValue().first, exercise.getValue().second);
        }

    }


    private static class DBHelper extends SQLiteOpenHelper {
        DBHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, dbName, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + DISH_TABLE + " ( " + COLUMN_ID + " integer, " + DISH_COLUMN_NAME + " text primary key," + DISH_COLUMN_CALORIES_PER_100_GM + " integer not null" + ");"); // create table dishes
            db.execSQL("create table " + EXERCISE_TABLE + " ( " + COLUMN_ID + " integer, " + EXERCISE_COLUMN_NAME + " text primary key, " + EXERCISE_COLUMN_TIME_COEFF + " integer default 0," + EXERCISE_COLUMN_QUANTITY_COEFF + " integer default 0" + ");"); // create table exercises
            db.execSQL("create table " + DAYS_DISHES_TABLE + " ( " + DAYS_COLUMN_DATE + " integer, " + DISH_COLUMN_NAME + " text, " + DAYS_DISH_COLUMN_WEIGHT + " integer default 100," + "FOREIGN KEY(" + DAYS_COLUMN_DATE + ") REFERENCES " + DAYS_TABLE + "(" + DAYS_COLUMN_DATE + "), FOREIGN KEY(" + DISH_COLUMN_NAME + ") REFERENCES " + DISH_TABLE + "(" + DISH_COLUMN_NAME + ") " + ");");//create table days_dishes
            db.execSQL("create table " + DAYS_EXERCISE_TABLE + " ( " + DAYS_COLUMN_DATE + " integer, " + EXERCISE_COLUMN_NAME + " text, " + DAYS_EXERCISE_COLUMN_TIME + " integer default 0," + DAYS_EXERCISE_COLUMN_QUANTITY + " integer default 0," + "FOREIGN KEY(" + DAYS_COLUMN_DATE + ") REFERENCES " + DAYS_TABLE + "(" + DAYS_COLUMN_DATE + "), FOREIGN KEY(" + EXERCISE_COLUMN_NAME + ") REFERENCES " + EXERCISE_TABLE + "(" + EXERCISE_COLUMN_NAME + ") " + ");");//create table days_exercises
            db.execSQL("create table " + DAYS_TABLE + " ( " + DAYS_COLUMN_DATE + " integer primary key, " + DAYS_COLUMN_RECORD + " varchar(" + MAX_RECORD_WIDTH + ") default ''" + ");");//create table days
            defaultFillExerciseTable(db); // fill table of exercises
            defaultFillDishTable(db); // fill table of dishes
        }

        private void defaultFillExerciseTable(SQLiteDatabase db) {
            ContentValues cv = new ContentValues();
            for (int i = 0; i < sampleExercisesNames.length; i++) {
                cv.put(EXERCISE_COLUMN_NAME, sampleExercisesNames[i]);
                cv.put(EXERCISE_COLUMN_TIME_COEFF, sampleExercisesTimeCoeff[i]);
                cv.put(EXERCISE_COLUMN_QUANTITY_COEFF, sampleExercisesQuantityCoeff[i]);
                db.insert(EXERCISE_TABLE, null, cv);
            }
        }

        private void defaultFillDishTable(SQLiteDatabase db) {
            ContentValues cv = new ContentValues();
            for (int i = 0; i < sampleDishesNames.length; i++) {
                cv.put(DISH_COLUMN_NAME, sampleDishesNames[i]);
                cv.put(DISH_COLUMN_CALORIES_PER_100_GM, sampleDishesCalories[i]);
                db.insert(DISH_TABLE, null, cv);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    public static ArrayList<Map<String, Object>> cursorToArrayList(Cursor cursor) {
        ArrayList<Map<String, Object>> data = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Map<String, Object> map = new HashMap<>();
                for (String colName : cursor.getColumnNames()) {
                    map.put(colName, cursor.getString(cursor.getColumnIndex(colName)));
                }
                data.add(map);
            } while (cursor.moveToNext());
        }
        return data;
    }

}
