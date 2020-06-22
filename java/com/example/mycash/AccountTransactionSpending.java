package com.example.mycash;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AccountTransactionSpending  extends AppCompatActivity {

    private ListView userList;
    private DatabaseHelper databaseHelper;
    private  SQLiteDatabase db;
    private Cursor userCursor;
    private Cursor totalCursor;
    private SimpleCursorAdapter userAdapter;
    private TextView total;
    private  TextView typeName;
    private Spinner monthName;
    private ImageView image;
    private String transactionTypeTag;
    private static Context contextthis;

     String Type;
    String Account;

    public static Context getContextthis()
    {
        return contextthis;
    }
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_transaction_spend);
        contextthis = this;
        userList = (ListView)findViewById(R.id.listSpend);
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), TransactionActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);

            }
        });
        databaseHelper = new DatabaseHelper(this);
       // db = databaseHelper.getReadableDatabase();
        typeName = (TextView) findViewById(R.id.type);
        monthName = (Spinner) findViewById(R.id.month);
        image = (ImageView) findViewById(R.id.image);
        total = (TextView) findViewById(R.id.total);

        transactionTypeTag = getIntent().getStringExtra("transactionType");
        String transactionType [] = transactionTypeTag.split(" ");

        Type = transactionType[0];
        Account = transactionType[1];



        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
      //  actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setIcon(R.drawable.sber);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFFFF")));
        actionBar.setTitle(Account);

    }
    @Override
    public void onResume() {
        super.onResume();

        // открываем подключение
        db = databaseHelper.getReadableDatabase();
        List<String> name_month = new ArrayList<>();
        Cursor monthCursor = db.rawQuery("select DISTINCT month from " + DatabaseHelper.TABLE + " order by " + DatabaseHelper.COLUMN_DATE +" DESC ", null);
        ArrayAdapter<String> adapterMonth = new ArrayAdapter<String>(this,
                R.layout.spinner_item, name_month);
        adapterMonth.setDropDownViewResource(R.layout.spinner);

        try {
            monthCursor.moveToFirst();
            while (!monthCursor.isAfterLast()) {

                name_month.add(monthCursor.getString(monthCursor.getColumnIndex(DatabaseHelper.COLUMN_MONTH)));
                monthCursor.moveToNext();
            }
        } finally {
            monthCursor.close();
        }

        typeName.setText(Type);
        monthName.setAdapter(adapterMonth);
        //получаем данные из бд в виде курсора

       // db.close();

        monthName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
              //  db = databaseHelper.getReadableDatabase();
                String selectedItem = parentView.getItemAtPosition(position).toString();
                totalCursor = db.rawQuery(" select sum (" + DatabaseHelper.COLUMN_SUM + ")  from " + DatabaseHelper.TABLE +  " WHERE " + DatabaseHelper.COLUMN_BANK +"= ?" +" AND "
                        +  DatabaseHelper.COLUMN_TYPE +"= ?" +" AND "  +  DatabaseHelper.COLUMN_MONTH +"= ?", new String[]{Account,Type,selectedItem});
                userCursor =  db.rawQuery("select * from "+ DatabaseHelper.TABLE +  " WHERE " + DatabaseHelper.COLUMN_BANK +"= ?" +" AND "
                        +  DatabaseHelper.COLUMN_TYPE +"= ?" +" AND "  +  DatabaseHelper.COLUMN_MONTH +"= ?" + " order by "
                        + DatabaseHelper.COLUMN_DATE +" DESC ", new String[]{Account,Type,selectedItem});
                // определяем, какие столбцы из курсора будут выводиться в ListView
                String[] headers = new String[] {DatabaseHelper.COLUMN_DESIGNATION, DatabaseHelper.COLUMN_DAY, DatabaseHelper.COLUMN_SUM};
                // создаем адаптер, передаем в него курсор
                userAdapter = new SimpleCursorAdapter(getContextthis(), R.layout.my_list_item,
                        userCursor, headers, new int[]{R.id.designat, R.id.dat, R.id.sum}, 0);
                // userList.setAdapter(userAdapter);
                //  header.setText("Найдено элементов: " + String.valueOf(userCursor.getCount()));
                if (totalCursor.moveToFirst()) {
                    Float totalSum = totalCursor.getFloat(0);
                    total.setText(Float.toString(totalSum).concat(" ₽"));
                }
                userList.setAdapter(userAdapter);
               // db.close();
                //userCursor.close();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        //db.close();
        }



    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        userCursor.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                db.close();
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
