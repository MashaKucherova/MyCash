package com.example.mycash;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class TransactionActivity extends AppCompatActivity {

    private EditText designationBox;
    private  EditText sumBox;
    private  Button delButton;
    private   Button saveButton;
    private  Spinner accountSpinner;
    private  Spinner typeSpinner;
    private  Spinner categorySpinner;
    private  DatabaseHelper sqlHelper;
    private  SQLiteDatabase db;
    private   Cursor userCursor;
    private  long userId = 0;
    private  Float Balance;
    private   Calendar date=new GregorianCalendar();
    private  TextView currentDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat( "LLLL yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormat1 = new SimpleDateFormat( "M yyyy", Locale.getDefault());

    private int myYear ;
    private  int myMonth ;
    private String month;

        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_transaction);

            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFFFF")));

            actionBar.setDisplayShowTitleEnabled(true);


            designationBox = (EditText) findViewById(R.id.designation);
            sumBox = (EditText) findViewById(R.id.sum);
            currentDate = (EditText) findViewById(R.id.date);
            delButton = (Button) findViewById(R.id.deleteButton);
            saveButton = (Button) findViewById(R.id.saveButton);
            accountSpinner =(Spinner) findViewById(R.id.accountSpinner) ;
            typeSpinner =(Spinner) findViewById(R.id.typeSpinner) ;
            categorySpinner =(Spinner) findViewById(R.id.categorySpinner) ;



            sqlHelper = new DatabaseHelper(this);
            db = sqlHelper.getWritableDatabase();

            List<String> name_account = new ArrayList<>();
           Cursor accountCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_A, null);

            try {
                accountCursor.moveToFirst();
                while (!accountCursor.isAfterLast()) {

                    name_account.add(accountCursor.getString(accountCursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
                    accountCursor.moveToNext();
                }
            } finally {
                accountCursor.close();
            }


            ArrayAdapter<String> adapterBank = new ArrayAdapter<String>(this,
                    R.layout.spinner_item1, name_account);
            adapterBank.setDropDownViewResource(R.layout.spinner);

            ArrayAdapter<?> adapterType =
                    ArrayAdapter.createFromResource(this,R.array.TypeNames, R.layout.spinner_item1);
            adapterType.setDropDownViewResource(R.layout.spinner);


            ArrayAdapter<?> adapterCategory =
                    ArrayAdapter.createFromResource(this,R.array.CategoryNames, R.layout.spinner_item1);
            adapterCategory.setDropDownViewResource(R.layout.spinner);

            accountSpinner.setAdapter(adapterBank);
            typeSpinner.setAdapter(adapterType);
            categorySpinner.setAdapter(adapterCategory);

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                userId = extras.getLong("id");
            }
            // если 0, то добавление
            if (userId > 0) {
                // получаем элемент по id из бд
                userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                        DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
                userCursor.moveToFirst();
                designationBox.setText(userCursor.getString(4));
                sumBox.setText(String.valueOf(userCursor.getFloat(5)));
                currentDate.setText(String.valueOf(userCursor.getString(6)));

                ArrayAdapter adapterBank1 = (ArrayAdapter) accountSpinner.getAdapter();
                accountSpinner.setSelection(adapterBank1.getPosition(userCursor.getString(1)));

                ArrayAdapter adapterType1 = (ArrayAdapter) typeSpinner.getAdapter();
                typeSpinner.setSelection(adapterType1.getPosition(userCursor.getString(3)));

                ArrayAdapter adapterCategory1 = (ArrayAdapter) categorySpinner.getAdapter();
                categorySpinner.setSelection(adapterCategory1.getPosition(userCursor.getString(9)));

               designationBox.setRawInputType(0x00000000);
               designationBox.setClickable(false);
               designationBox.setLongClickable(false);

                sumBox.setRawInputType(0x00000000);
                sumBox.setClickable(false);
                sumBox.setLongClickable(false);

                currentDate.setRawInputType(0x00000000);
                currentDate.setClickable(false);
                currentDate.setLongClickable(false);

                accountSpinner.setEnabled(false);

                typeSpinner.setEnabled(false);

                categorySpinner.setEnabled(false);
                saveButton.setVisibility(View.GONE);

                actionBar.setTitle("Информация об операции");

               // userCursor.close();
            } else {
                // скрываем кнопку удаления
                delButton.setVisibility(View.GONE);
                actionBar.setTitle("Новая операция");
            }


        }

        public void save (View view){

            if(designationBox.getText().toString().equals("") || sumBox.getText().toString().equals("")  || currentDate.getText().toString().equals("")  ) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Укажите все данные!").setPositiveButton("Ок", dialogClickListener).show();
            }
       else {
                ContentValues cv = new ContentValues();
                ContentValues cv_b = new ContentValues();

                // cursorAccount.moveToNext();

                Cursor cursorAccount = db.rawQuery("select * from " + DatabaseHelper.TABLE_A + " where " +
                        DatabaseHelper.COLUMN_NAME + "= ? ", new String[]{accountSpinner.getSelectedItem().toString()});

                cursorAccount.moveToFirst();
                switch (typeSpinner.getSelectedItem().toString()) {
                    case "Доходы":

                        Balance = cursorAccount.getFloat(2) + Float.parseFloat(sumBox.getText().toString());
                        cv_b.put(DatabaseHelper.COLUMN_BALANCE, 0.1 * Math.floor(10 * Balance));

                        break;
                    case "Расходы":

                        Balance = cursorAccount.getFloat(2) - Float.parseFloat(sumBox.getText().toString());
                        cv_b.put(DatabaseHelper.COLUMN_BALANCE, 0.1 * Math.floor(10 * Balance));

                        break;
                }


                if (typeSpinner.getSelectedItem().toString().equals("Расходы")) {
                    cv.put(DatabaseHelper.COLUMN_SUM, 0.1 * Math.floor(10 * (Float.parseFloat("-".concat(sumBox.getText().toString())))));
                } else {
                    cv.put(DatabaseHelper.COLUMN_SUM, 0.1 * Math.floor(10 * (Float.parseFloat(sumBox.getText().toString()))));
                }
                cv.put(DatabaseHelper.COLUMN_DESIGNATION, designationBox.getText().toString());
                cv.put(DatabaseHelper.COLUMN_DATE, date.getTimeInMillis());
                cv.put(DatabaseHelper.COLUMN_BANK, accountSpinner.getSelectedItem().toString());
                cv.put(DatabaseHelper.COLUMN_TYPE, typeSpinner.getSelectedItem().toString());
                cv.put(DatabaseHelper.COLUMN_CATEGORY, categorySpinner.getSelectedItem().toString());
                cv.put(DatabaseHelper.COLUMN_DAY, currentDate.getText().toString());
                cv.put(DatabaseHelper.COLUMN_MONTH, month);


                if (userId > 0) {
                    db.update(DatabaseHelper.TABLE, cv, DatabaseHelper.COLUMN_ID + "=" + String.valueOf(userId), null);
                } else {
                    db.insert(DatabaseHelper.TABLE, null, cv);
                    db.update(DatabaseHelper.TABLE_A, cv_b, DatabaseHelper.COLUMN_NAME + "= ? ", new String[]{accountSpinner.getSelectedItem().toString()});

                }


                goHome();
            }
        }


        public void delete (View view){

            Cursor cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_A + " where " +
                    DatabaseHelper.COLUMN_NAME + "=?", new String[]{accountSpinner.getSelectedItem().toString()});
            ContentValues cv_b = new ContentValues();
            cursor.moveToNext();

                Balance = cursor.getFloat(2) - Float.parseFloat(sumBox.getText().toString());
                cv_b.put(DatabaseHelper.COLUMN_BALANCE,0.1*Math.floor(10*Balance));



            db.update(DatabaseHelper.TABLE_A, cv_b, DatabaseHelper.COLUMN_NAME + "= ? ", new String[] {accountSpinner.getSelectedItem().toString()});
            db.delete(DatabaseHelper.TABLE, "_id = ?", new String[]{String.valueOf(userId)});
           // cursor.close();
            goHome();
        }


        private void goHome () {
            // закрываем подключение
            db.close();

            // переход к главной activity
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }

    public void setDate(View v) {
        new DatePickerDialog(TransactionActivity.this, d,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH))
                .show();
    }

             DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            date.set(Calendar.YEAR, year);
            date.set(Calendar.MONTH, monthOfYear);
            date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            currentDate.setText(DateUtils.formatDateTime(TransactionActivity.this, date.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE) );


              String  monthSMS = date.getDisplayName(Calendar.MONTH,Calendar.LONG_FORMAT, new Locale("ru"));
                myYear = year;
                myMonth = monthOfYear;
                //myDay = dayOfMonth;
                try {
                    month = dateFormat.format(dateFormat1.parse(String.valueOf(myMonth+1).concat(" ").concat(String.valueOf(myYear))));
                    System.out.println(month);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                currentDate.setText( dayOfMonth + " "+ monthSMS );
            }
    };

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


