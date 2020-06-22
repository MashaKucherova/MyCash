package com.example.mycash;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.Context;
import android.content.ContentValues;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "transaction.db";
    private static final int SCHEMA = 134;

    static final String TABLE = "transactions";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_BANK = "name_bank";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_MONTH = "month";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_TYPE = "type_transaction";
    public static final String COLUMN_DESIGNATION = "designation";
    public static final String COLUMN_SUM = "sum";
    public static final String COLUMN_CATEGORY = "category";
    static final String TABLE_A = "accounts";
    public static final String COLUMN_ID_A = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_BALANCE = "balance_account";
    static final String TABLE_T = "time";
    public static final String COLUMN_ID_T = "_id";
    public static final String COLUMN_TIMESMS = "timesms";






    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
        //context.deleteDatabase("transaction.db");
       // context.deleteDatabase("transaction1.db");
       // context.deleteDatabase("transactions.db");
    }
    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL("CREATE TABLE IF NOT EXISTS transactions (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_BANK
                + " TEXT, " + COLUMN_DATE + " TEXT, "+ COLUMN_TYPE + " TEXT, "+ COLUMN_DESIGNATION + " TEXT, " + COLUMN_SUM + " REAL, "+  COLUMN_DAY +
                " TEXT, "+ COLUMN_TIME + " TEXT, "+ COLUMN_MONTH + " TEXT, "+ COLUMN_CATEGORY + " TEXT );");


        db.execSQL("CREATE TABLE IF NOT EXISTS accounts (" + COLUMN_ID_A
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME + " TEXT, "+  COLUMN_BALANCE + " REAL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS time (" + COLUMN_ID_T
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_TIMESMS + " TEXT);");

       ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, "Наличные");
        values.put(DatabaseHelper.COLUMN_BALANCE, 0);

        db.insert(DatabaseHelper.TABLE_A,null,values);

        ContentValues values1 = new ContentValues();
        values1.put(DatabaseHelper.COLUMN_TIMESMS, "0");
        db.insert(DatabaseHelper.TABLE_T,null,values1);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      // db.execSQL("DROP DATABASE transaction.db, transaction1.db ");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_A);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_T);
        onCreate(db);
    }

    public void oldfillingDB(String [][] transaction){
        /*    String[][] transactions = transaction;
        int CountSum = countSum;

        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Cursor cursor =  db.rawQuery("select * from "+ DatabaseHelper.TABLE, null);

            for (int i = 0; i < transactions.length; i++) {
                int j = 0;
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        if (!transaction[i][j].equals(cursor.getString(4)) &&
                                !transaction[i][j + 1].equals(cursor.getString(5)) &&
                                !transaction[i][j + 2].equals(cursor.getString(2))) {

                            values.put(DatabaseHelper.COLUMN_DESIGNATION, transactions[i][j]);
                            values.put(DatabaseHelper.COLUMN_SUM, transactions[i][j + 1]);
                            values.put(DatabaseHelper.COLUMN_DATE, transaction[i][j + 2]);

                            db.insert(DatabaseHelper.TABLE, null, values);
                            CountSum --;
                            if(CountSum < 1)
                                break;
                            break;
                        }
                    }while (cursor.moveToNext());
                }
            } db.close();
            cursor.close();

*/
    }
}
