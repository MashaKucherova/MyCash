package com.example.mycash;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Telephony;
import android.widget.Toast;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class SMSRead {
    public String numberSMS;
    public String body;
    public int yearSMS;
    public String monthSMS;
    public int daySMS;
    public int intMonthSMS;
    public Date dateSMS;
    public String DateSMS;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;


    SMSParser sc = new SMSParser();
    MainActivity sc1 = new MainActivity();
    TransactionActivity sc2 = new TransactionActivity();

    // DatabaseHelper databaseHelper1 = sc1.databaseHelper;
    public void getAllSms(Context context) {

        ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, null);
        //дата сейчас
        Calendar calendar = new GregorianCalendar();
        //Date date = calendar.getTime();
        int year = calendar.get(Calendar.YEAR);
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLLL yyyy", Locale.getDefault());
        //String month = dateFormat.format(calendar);
        String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, new Locale("ru"));
        //  System.out.println(date);

        int totalSMS = 0;
        if (c != null) {
            totalSMS = c.getCount();
            if (c.moveToLast()) {
                for (int j = 0; j < totalSMS; j++) {

                    String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));//номер смс
                    String smsDate = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE));//дата
                    //извлечение года и месяца из полученной даты смс
                    dateSMS = new Date(Long.valueOf(smsDate));
                    Calendar calendar1 = new GregorianCalendar();
                    calendar1.setTime(dateSMS);
                    // System.out.println(calendar1);
                    yearSMS = calendar1.get(Calendar.YEAR);
                    //  monthSMS = calendar1.getDisplayName(Calendar.MONTH,Calendar.LONG_FORMAT, new Locale("ru"));
                    monthSMS = dateFormat.format(dateSMS);
                    //daySMS = calendar1.get(Calendar.DAY_OF_MONTH);
                    SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd MMMM HH:mm");
                    DateSMS = dateFormat1.format(dateSMS);
                    //System.out.println(DateSMS);
                    //надо сделать чтобы выводило еще день недели
                    // смс только от номера 900
                    //  databaseHelper = new DatabaseHelper(TransactionFragment.getContextthis());
                    // db = databaseHelper.getWritableDatabase();

//                  Cursor cursor1 = db.rawQuery("select * from "+ DatabaseHelper.TABLE_T, null);


                    if (number.equals("900") && Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE))) == Telephony.Sms.MESSAGE_TYPE_INBOX
                            && year == yearSMS ) { // && month == mpnthSMS

                        databaseHelper = new DatabaseHelper(TransactionFragment.getContextthis());
                        db = databaseHelper.getWritableDatabase();
                        Cursor cursor1 = db.rawQuery("select * from "+ DatabaseHelper.TABLE_T, null);

                        cursor1.moveToFirst();

                        if(cursor1.getLong(1) < Long.parseLong(smsDate) ){

                            body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY));
                            sc.Parser(smsDate, body, monthSMS, DateSMS);

                            ContentValues values = new ContentValues();
                            values.put(DatabaseHelper.COLUMN_TIMESMS,smsDate);
                            db.update(DatabaseHelper.TABLE_T,values,null,null);

                        }

                         cursor1.close();
                    }
                    c.moveToPrevious();
                    // cursor1.close();
                }
                //
            }

            else {
                //Toast.makeText(this, "No message to show!", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(TransactionFragment.getContextthis());
                builder.setMessage("СМС-сообщения от \"Сбербанк\" не найдены").setPositiveButton("Ок", dialogClickListener).show();
            }
        }
        c.close();
db.close();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:

                    break;

            }
        }
    };
}
