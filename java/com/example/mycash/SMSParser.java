package com.example.mycash;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.lang.*;


public class SMSParser  {
    public String type;// доход или расход

    public String designation;//указание к операции
    public String sum;//сумма
    public String category;
    String [][] transactions = new String[1000][6];
    int count =0;
    public  String DateSMSm;
    public  String dateSMS1;
    public String MonthSMS;
    public int countSum;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    String balance;
    String Balance;



    public void Parser(String dateSMS,String body, String monthSMS,String DateSMS) {


    databaseHelper = new DatabaseHelper(TransactionFragment.getContextthis());

        String bodySMS = body;
        DateSMSm = dateSMS;
        dateSMS1 = DateSMS;
        MonthSMS = monthSMS;


        //парсер смс покупки -

        if (bodySMS.matches("^[A-Z]{4}\\d{4}[\\s:.\\d]*Покупка\\s[\\d.]*р.*Баланс:\\s[\\d.]*р$")) {//НАПИСАТЬ РУГУЛЯРКУ
            String buySMS = bodySMS.substring(bodySMS.indexOf("Покупка") + 8, bodySMS.indexOf("Баланс"));
            String[] buy = buySMS.split(" ", 2);
            sum ="-".concat( buy[0].substring(0, buy[0].length() - 1));

            designation = buy[1];
            type = "Расходы";
            category = "Покупки";
            //if (balance == null){
                balance = (bodySMS.substring(bodySMS.indexOf("Баланс:") +8));
                balance = balance.substring(0,balance.length()-1);
           fillingDB(designation,sum,DateSMSm,type,dateSMS1,balance,category);
            //System.out.println(balance);

        }

        //смс перевода +
        if (bodySMS.matches("^Перевод\\s[\\d.]*р\\sот\\s[аА-яЯ].*\\sБаланс\\s[A-Z]{4}\\d{4}:\\s[\\d.]*р\\s*.*$")) { //НАПИСАТЬ РЕГУЛЯРКУ
            String transferSMS = bodySMS.substring(bodySMS.indexOf("Перевод") + 8, bodySMS.indexOf("Баланс"));
            String[] transfer = transferSMS.split(" ", 2);
            sum = transfer[0].substring(0, transfer[0].length() - 1);
            designation = transfer[1];
            type = "Доходы";
            category = "Зачисления";
           // if (balance == null){
                balance = (bodySMS.substring(bodySMS.indexOf("Баланс") +17));
                balance = balance.substring(0,balance.length()-1);//}
            fillingDB(designation,sum,DateSMSm,type,dateSMS1,balance,category);
          //  System.out.println(balance);

        }

        //смс оплаты -
        if (bodySMS.matches("^[A-Z]{4}\\d{4}[\\s:.\\d]*Оплата\\s[\\d.]*р.*Баланс:\\s[\\d.]*р$")) {//НАПИСАТЬ РЕГУЛЯРКУ
            String paymentSMS = bodySMS.substring(bodySMS.indexOf("Оплата") + 7, bodySMS.indexOf("Баланс"));
            String[] payment = paymentSMS.split(" ", 2);
            sum = "-".concat( payment[0].substring(0, payment[0].length() - 1));
            designation = "Оплата ".concat(payment[1]);
            type = "Расходы";
            category = "Платежи";
            //if (balance == null){
                balance = (bodySMS.substring(bodySMS.indexOf("Баланс:") +8));
                balance = balance.substring(0,balance.length()-1);//}
            fillingDB(designation,sum,DateSMSm,type,dateSMS1,balance,category);
           // System.out.println(balance);

        }
        //смс перевода от меня -
        if (bodySMS.matches("^[A-Z]{4}\\d{4}[\\s:.\\d]*перевод\\s[\\d.]*р\\sБаланс:\\s[\\d.]*р$")) { //НАПИСАТЬ РЕГУЛЯРКУ
            String myTransferSMS = bodySMS.substring(bodySMS.indexOf("перевод") + 8, bodySMS.indexOf("Баланс"));
            sum = "-".concat( myTransferSMS.substring(0, myTransferSMS.length() - 2));
            designation = "Перевод";
            type = "Расходы";
            category = "Переводы";
            //if (balance == null){
                balance = (bodySMS.substring(bodySMS.indexOf("Баланс:") +8));
                 balance = balance.substring(0,balance.length()-1);//}

            fillingDB(designation,sum,DateSMSm,type,dateSMS1,balance,category);
          //  System.out.println(balance);
        }

        if (bodySMS.matches("^[A-Z]{4}\\d{4}[\\s:.\\d]*[Зз]ачисление[зарплаты\\s]*[\\d.]*р\\sБаланс:\\s[\\d.]*р$")) {
            String receiptsSMS = bodySMS.substring(bodySMS.indexOf("ачисление") +10, bodySMS.indexOf("Баланс"));
            sum = receiptsSMS.substring(0,receiptsSMS.indexOf(" ")-1);
           // receipts = receiptsSMS.substring(0, receiptsSMS.length() - 1);
            designation = "Зачисление";
            type = "Доходы";
            category = "Зачисления";
            //if (balance == null){
                balance = (bodySMS.substring(bodySMS.indexOf("Баланс:") +8));
                balance = balance.substring(0,balance.length()-1);//}
            fillingDB(designation,sum,DateSMSm,type,dateSMS1,balance,category);
          //  System.out.println(balance);

        }

        }



    public void fillingDB(String designation, String sum, String DateSMSm, String type, String dateSMS,String balance, String category) {


        db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        ContentValues values1 = new ContentValues();
        ContentValues values2 = new ContentValues();
        //Cursor cursor = db.rawQuery("select * from "+ DatabaseHelper.TABLE, null);
        Cursor cursor1 = db.rawQuery("select * from "+ DatabaseHelper.TABLE_A, null);
        // ПЕРЕДЕЛАТЬ, ЧТОБЫ ДОБАЛЯЛИСЬ ЗАПИСИ КОГДА НЕТ ЗАПИСЕЙ СО СБЕРА, А НЕ КОГДА КУРСОР =1!!!!!!!!!!

        values2.put(DatabaseHelper.COLUMN_BALANCE,Float.valueOf(balance));
        values2.put(DatabaseHelper.COLUMN_NAME,"Сбербанк");
       // values1.put(DatabaseHelper.COLUMN_BALANCE_C,0);
        if(cursor1.getCount()==1)

        {
        db.insert(DatabaseHelper.TABLE_A, null,values2);
        }
        else{
        db.update(DatabaseHelper.TABLE_A, values2, DatabaseHelper.COLUMN_NAME + "= ?", new String[] {"Сбербанк"});
        }


        values.put(DatabaseHelper.COLUMN_DESIGNATION, designation);
        values.put(DatabaseHelper.COLUMN_SUM, Float.valueOf(sum));
        values.put(DatabaseHelper.COLUMN_DATE, DateSMSm);
        values.put(DatabaseHelper.COLUMN_BANK, "Сбербанк");
        values.put(DatabaseHelper.COLUMN_TYPE,type);
        values.put(DatabaseHelper.COLUMN_DAY, dateSMS);
        values.put(DatabaseHelper.COLUMN_MONTH, MonthSMS);
        values.put(DatabaseHelper.COLUMN_CATEGORY,category);
        db.insert(DatabaseHelper.TABLE, null, values);

        //  CountSum --;
        // if(CountSum < 1)
        //   break;

        //cursor.close();
        db.close();

    }
         }




