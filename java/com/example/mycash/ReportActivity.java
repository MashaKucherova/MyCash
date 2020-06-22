package com.example.mycash;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    private ListView userList;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private  Cursor categoryCursor;
    private  String categoryTag;
    private  String month_year;
    private  String [] categoryType = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        userList = (ListView)findViewById(R.id.list);
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), TransactionActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);

            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFFFF")));
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        databaseHelper = new DatabaseHelper(this);

        categoryTag = getIntent().getStringExtra("category");
        month_year = getIntent().getStringExtra("month_year");


        switch (categoryTag){
            case "buy":
                categoryType[0]="Покупки";
                categoryType[1]="Расходы";
                break;
            case "pay":
                categoryType[0]="Платежи";
                categoryType[1]="Расходы";
                break;
            case "transfer":
                categoryType[0]="Переводы";
                categoryType[1]="Расходы";
                break;
            case "no":
                categoryType[0]="Без категории";
                categoryType[1]="Расходы";
                break;

        }

        actionBar.setTitle(categoryType[0]);
    }

    @Override
    public void onResume() {
        super.onResume();

        // открываем подключение
        db = databaseHelper.getReadableDatabase();

        //получаем данные из бд в виде курсора

        categoryCursor =  db.rawQuery("select * from "+ DatabaseHelper.TABLE +  " WHERE " + DatabaseHelper.COLUMN_CATEGORY +"= ?" +" AND "
                +  DatabaseHelper.COLUMN_TYPE +"= ?" +" AND "  +  DatabaseHelper.COLUMN_MONTH +"= ?" + " order by "
                + DatabaseHelper.COLUMN_DATE +" DESC ", new String[]{categoryType[0],categoryType[1],month_year});
        // определяем, какие столбцы из курсора будут выводиться в ListView
        String[] headers = new String[] {DatabaseHelper.COLUMN_DESIGNATION, DatabaseHelper.COLUMN_DAY, DatabaseHelper.COLUMN_SUM};
        // создаем адаптер, передаем в него курсор
        SimpleCursorAdapter  categoryAdapter = new SimpleCursorAdapter(this, R.layout.my_list_item,
                categoryCursor, headers, new int[]{R.id.designat, R.id.dat, R.id.sum}, 0);

        userList.setAdapter(categoryAdapter);

        db.close();

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
       categoryCursor.close();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
