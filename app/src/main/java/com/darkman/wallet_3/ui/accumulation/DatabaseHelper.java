package com.darkman.wallet_3.ui.accumulation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "WalletApp.db";
    // Версия 4, так как мы добавили колонки iconResId и colorResId
    private static final int DATABASE_VERSION = 4;

    // Константы таблиц
    public static final String TABLE_HISTORY = "History";
    public static final String TABLE_BALANCES = "Balances";

    // Колонки Balances
    public static final String COL_BAL_ID = "id";
    public static final String COL_BAL_NAME = "name";
    public static final String COL_BAL_SCORE = "score";
    public static final String COL_BAL_BASEDDAY = "basedDay";
    public static final String COL_BAL_TARGETDAY = "targetDay";
    public static final String COL_BAL_MAX_SCORE = "maxScore";
    public static final String COL_BAL_CATEGORY_ID = "categoryId";
    public static final String COL_BAL_ICON_RES = "iconResId";
    public static final String COL_BAL_COLOR_RES = "colorResId";

    // Колонки History
    public static final String COL_HIST_ID = "id";
    public static final String COL_HIST_BALANCE_ID = "balanceId";
    public static final String COL_HIST_CATEGORY = "category";
    public static final String COL_HIST_VALUE = "value";
    public static final String COL_HIST_DATA = "data";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создание таблицы History
        db.execSQL("CREATE TABLE " + TABLE_HISTORY + " (" +
                COL_HIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_HIST_BALANCE_ID + " INTEGER," +
                COL_HIST_CATEGORY + " INTEGER," +
                COL_HIST_VALUE + " INTEGER," +
                COL_HIST_DATA + " TEXT);");

        // Создание таблицы Balances
        db.execSQL("CREATE TABLE " + TABLE_BALANCES + " (" +
                COL_BAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_BAL_NAME + " TEXT," +
                COL_BAL_SCORE + " INTEGER," +
                COL_BAL_BASEDDAY + " LONG," +
                COL_BAL_TARGETDAY + " LONG," +
                COL_BAL_MAX_SCORE + " INTEGER," +
                COL_BAL_CATEGORY_ID + " INTEGER," +
                COL_BAL_ICON_RES + " INTEGER," +
                COL_BAL_COLOR_RES + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            // Обновляем структуру без удаления данных пользователя
            try {
                db.execSQL("ALTER TABLE " + TABLE_BALANCES + " ADD COLUMN " + COL_BAL_ICON_RES + " INTEGER DEFAULT 0");
                db.execSQL("ALTER TABLE " + TABLE_BALANCES + " ADD COLUMN " + COL_BAL_COLOR_RES + " INTEGER DEFAULT 0");
            } catch (Exception e) {
                Log.e("DB_UPGRADE", "Error adding columns", e);
            }
        }
    }

    // --- ОПЕРАЦИИ С BALANCE ---

    public void insertBalance(String name, int score, int maxScore, long basedDay, long targetDay, int categoryId, int iconRes, int colorRes) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COL_BAL_NAME, name);
            values.put(COL_BAL_SCORE, score);
            values.put(COL_BAL_MAX_SCORE, maxScore);
            values.put(COL_BAL_BASEDDAY, basedDay);
            values.put(COL_BAL_TARGETDAY, targetDay);
            values.put(COL_BAL_CATEGORY_ID, categoryId);
            values.put(COL_BAL_ICON_RES, iconRes);
            values.put(COL_BAL_COLOR_RES, colorRes);
            db.insert(TABLE_BALANCES, null, values);
        }
    }

    public void updateBalance(Balance balance) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COL_BAL_NAME, balance.name);
            values.put(COL_BAL_SCORE, balance.score);
            values.put(COL_BAL_MAX_SCORE, balance.maxScore);
            values.put(COL_BAL_BASEDDAY, balance.basedDay);
            values.put(COL_BAL_TARGETDAY, balance.targetDay);
            values.put(COL_BAL_CATEGORY_ID, balance.categoryId);
            values.put(COL_BAL_ICON_RES, balance.iconResId);
            values.put(COL_BAL_COLOR_RES, balance.colorResId);
            db.update(TABLE_BALANCES, values, COL_BAL_ID + " = ?", new String[]{String.valueOf(balance.id)});
        }
    }

    public List<Balance> getAllBalances() {
        List<Balance> balances = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BALANCES, null)) {
            if (cursor.moveToFirst()) {
                do {
                    balances.add(cursorToBalance(cursor));
                } while (cursor.moveToNext());
            }
        }
        return balances;
    }

    public Balance getBalanceById(int id) {
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BALANCES + " WHERE " + COL_BAL_ID + " = ?", new String[]{String.valueOf(id)})) {
            if (cursor.moveToFirst()) {
                return cursorToBalance(cursor);
            }
        }
        return null;
    }

    private Balance cursorToBalance(Cursor cursor) {
        return new Balance(
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_BAL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_BAL_NAME)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_BAL_SCORE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_BAL_MAX_SCORE)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_BAL_BASEDDAY)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_BAL_TARGETDAY)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_BAL_CATEGORY_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_BAL_ICON_RES)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_BAL_COLOR_RES))
        );
    }

    // --- ОПЕРАЦИИ С HISTORY ---

    public void insertHistory(int balanceId, int categoryId, int value, String data) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COL_HIST_BALANCE_ID, balanceId);
            values.put(COL_HIST_CATEGORY, categoryId);
            values.put(COL_HIST_VALUE, value);
            values.put(COL_HIST_DATA, data);
            db.insert(TABLE_HISTORY, null, values);
        }
    }

    public List<History> getHistoryForBalance(int balanceId, int balanceCategoryId) {
        List<History> historyList = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_HISTORY + " WHERE " + COL_HIST_BALANCE_ID + " = ? ORDER BY " + COL_HIST_ID + " DESC",
                     new String[]{String.valueOf(balanceId)})) {
            if (cursor.moveToFirst()) {
                do {
                    historyList.add(new History(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COL_HIST_ID)),
                            balanceId,
                            cursor.getInt(cursor.getColumnIndexOrThrow(COL_HIST_CATEGORY)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COL_HIST_VALUE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_HIST_DATA)),
                            balanceCategoryId
                    ));
                } while (cursor.moveToNext());
            }
        }
        return historyList;
    }

    public int getSumForBalance(int balanceId) {
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery("SELECT SUM(" + COL_HIST_VALUE + ") FROM " + TABLE_HISTORY + " WHERE " + COL_HIST_BALANCE_ID + " = ?",
                     new String[]{String.valueOf(balanceId)})) {
            if (cursor.moveToFirst()) return cursor.getInt(0);
        }
        return 0;
    }
}
