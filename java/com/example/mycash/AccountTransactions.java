package com.example.mycash;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AccountTransactions extends AppCompatActivity {

    private ListView userList;
    private TextView header;
    private DatabaseHelper databaseHelper;
    private   SQLiteDatabase db;
    private Cursor userCursor;
    private  SimpleCursorAdapter userAdapter;
    private Float spendSum;
    private  Float incomeSum;
    private  TextView Spend;
    private  TextView Income;
    private TextView balance;
    private TextView name;
    private TextView month;
    private TextView month1;
    private ImageView image;
    long accountId = 0;
    private Date date = new Date();
    private SimpleDateFormat dateFormat = new SimpleDateFormat( "LLLL yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormat1 = new SimpleDateFormat( "LLLL", Locale.getDefault());
    private DecimalFormat decimalFormat =  new DecimalFormat("#0.0");

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_transactions);

        userList = (ListView) findViewById(R.id.list1);
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), TransactionActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);

            }
        });
        databaseHelper = new DatabaseHelper(this);

        Spend = (TextView) findViewById(R.id.spendSum);
        Income = (TextView) findViewById(R.id.incomeSum);
        month = (TextView) findViewById(R.id.month);
        month1 = (TextView) findViewById(R.id.month1);
        balance = (TextView) findViewById(R.id.balance);
        name = (TextView) findViewById(R.id.name);
        image = (ImageView) findViewById(R.id.image);



        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFFFF")));
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle("Данные счета");
        actionBar.setDisplayShowTitleEnabled(true);


    }

    @Override
    public void onResume() {
        super.onResume();

        // открываем подключение
        db = databaseHelper.getReadableDatabase();

       //ID СЧЕТА
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accountId = extras.getLong("id");
        }
        if (accountId > 0) {   //НАЗВАНИЕ И БАЛАНС СЧЕТА
            // получаем элемент по id из бд
            Cursor AccountCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_A + " where " +
                    DatabaseHelper.COLUMN_ID_A + "=?", new String[]{String.valueOf(accountId)});
            AccountCursor.moveToFirst();
            name.setText(AccountCursor.getString(1));

            Float Balance = AccountCursor.getFloat(2);
            balance.setText(decimalFormat.format(Balance).concat(" ₽"));

             //СУММА РАСХОДОВ
            Cursor cursorSpend = db.rawQuery(" select sum (" + DatabaseHelper.COLUMN_SUM + ")  from " + DatabaseHelper.TABLE + " WHERE " + DatabaseHelper.COLUMN_TYPE + "= ?" + " AND "
                    + DatabaseHelper.COLUMN_BANK + "= ?" + " AND " + DatabaseHelper.COLUMN_MONTH + "= ?", new String[]{"Расходы", AccountCursor.getString(1), dateFormat.format(date)});
            if (cursorSpend.moveToFirst()) {
                spendSum = cursorSpend.getFloat(0);
                Spend.setText(Float.toString(spendSum));
            }
            cursorSpend.close();

             //СУММА ДОХОДОВ
            Cursor cursorIncome = db.rawQuery(" select sum (" + DatabaseHelper.COLUMN_SUM + ")  from " + DatabaseHelper.TABLE + " WHERE " + DatabaseHelper.COLUMN_TYPE + "= ?" + " AND "
                    + DatabaseHelper.COLUMN_BANK + "= ?" + " AND " + DatabaseHelper.COLUMN_MONTH + "= ?", new String[]{"Доходы", AccountCursor.getString(1), dateFormat.format(date)});
            if (cursorIncome.moveToFirst()) {
                incomeSum = cursorIncome.getFloat(0);
                Income.setText(Float.toString(incomeSum));

            }
            cursorIncome.close();

            //получаем данные из бд в виде курсора

            userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " WHERE " + DatabaseHelper.COLUMN_BANK + "= ?" +
                    " order by " + DatabaseHelper.COLUMN_DATE + " DESC ", new String[]{AccountCursor.getString(1)});
            // определяем, какие столбцы из курсора будут выводиться в ListView
            String[] headers = new String[]{DatabaseHelper.COLUMN_DESIGNATION, DatabaseHelper.COLUMN_DAY, DatabaseHelper.COLUMN_SUM};
            // создаем адаптер, передаем в него курсор
            userAdapter = new SimpleCursorAdapter(this, R.layout.my_list_item,
                    userCursor, headers, new int[]{R.id.designat, R.id.dat, R.id.sum}, 0);

            userList.setAdapter(userAdapter);
            // db.close();
            // userCursor.close();

            month.setText(dateFormat1.format(date));
            month1.setText(dateFormat1.format(date));


            if(AccountCursor.getString(1).equals("Сбербанк")){
                image.setImageResource(R.drawable.sber);
            }
            else
                image.setImageResource(R.drawable.cash1);

        }

        db.close();
    }

    public void spend (View view){
        db.close();
        Intent intent = new Intent(this,AccountTransactionSpending.class);
        String acc = name.getText().toString();
        intent.putExtra("transactionType", view.getTag().toString().concat(" ").concat(acc));
        startActivity(intent);
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
                this.finish();
                return true;
            case R.id.action_edit:
                Intent intent = new Intent(this, AccountAdd.class);
                String acc = name.getText().toString();
                intent.putExtra("account", acc);
                this.startActivity(intent);
                return  true;
            case R.id.action_delete:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Вы действительно хотите удалить счет со всеми данными о нем?").setPositiveButton("Да", dialogClickListener)
                        .setNegativeButton("Нет", dialogClickListener).show();

                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(name.getText().toString().equals("Сбербанк")==false){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scrolling, menu);}
        return true;
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    db = databaseHelper.getReadableDatabase();
                    db.delete(DatabaseHelper.TABLE_A, "name = ?", new String[]{name.getText().toString()});
                    db.delete(DatabaseHelper.TABLE, "name_bank = ?", new String[]{name.getText().toString()});
                    db.close();
                    finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };
}
