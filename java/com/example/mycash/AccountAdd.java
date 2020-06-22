package com.example.mycash;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class AccountAdd extends AppCompatActivity {

    private EditText nameAccount;
    private EditText balanceAccount;
    private DatabaseHelper dbHelper;
    private  SQLiteDatabase db;
    private String Account;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_add);

        nameAccount = (EditText) findViewById(R.id.nameAccount);
        balanceAccount = (EditText) findViewById(R.id.balanceAccount);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFFFF")));

        actionBar.setDisplayShowTitleEnabled(true);

        dbHelper = new DatabaseHelper(this);
        //db = dbHelper.getWritableDatabase();

        Account = getIntent().getStringExtra("account");
        System.out.println(Account);


    }
    @Override
    public void onResume() {
        super.onResume();
        db = dbHelper.getWritableDatabase();
        ActionBar actionBar = getSupportActionBar();
        if(Account == null){
            actionBar.setTitle("Новый счет");
        }

        if(Account != null) {

            Cursor cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_A + " where " +
                    DatabaseHelper.COLUMN_NAME + "=?", new String[]{Account});
            cursor.moveToFirst();
            nameAccount.setText(cursor.getString(1));

            Float Balance = cursor.getFloat(2);
            balanceAccount.setText(String.valueOf(Balance));
            actionBar.setTitle("Редактировать счет");
        }

    }
    public void saveAccount(View view) {
        if(nameAccount.getText().toString().equals("")|| balanceAccount.getText().toString().equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Укажите все данные!").setPositiveButton("Ок", dialogClickListener).show();
        }

         else {
     ContentValues values = new ContentValues();
     ContentValues values1 = new ContentValues();

     values.put(DatabaseHelper.COLUMN_NAME, nameAccount.getText().toString());
     values.put(DatabaseHelper.COLUMN_BALANCE, 0.1 * Math.floor(10 * (Float.parseFloat(balanceAccount.getText().toString()))));

     values1.put(DatabaseHelper.COLUMN_BANK, nameAccount.getText().toString());

     if (Account == null)
         db.insert(DatabaseHelper.TABLE_A, null, values);

     else {
         db.update(DatabaseHelper.TABLE_A, values, DatabaseHelper.COLUMN_NAME + "= ?", new String[]{Account});
         db.update(DatabaseHelper.TABLE, values1, DatabaseHelper.COLUMN_BANK + "= ?", new String[]{Account});
     }
     //  goHome();
            db.close();
     this.finish();
        }
    }

    private void goHome () {
        // закрываем подключение
        db.close();

        // переход к главной activity
        Intent intent = new Intent(this, AccountFragment.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
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
