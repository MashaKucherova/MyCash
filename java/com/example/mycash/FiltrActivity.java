package com.example.mycash;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class FiltrActivity extends AppCompatActivity {
   private DatabaseHelper DatabaseHelper;
   private SQLiteDatabase db;
   private TextView dateStart;
    private TextView dateFinish;
    private Calendar date=new GregorianCalendar();
    private Calendar date1=new GregorianCalendar();
    private  SimpleDateFormat dateFormat = new SimpleDateFormat( "LLLL yyyy", Locale.getDefault());
    private  SimpleDateFormat dateFormat1 = new SimpleDateFormat( "M yyyy", Locale.getDefault());
    private int myYear ;
    private int myMonth ;
    private String month;

    private CheckBox typeSumm;
    private CheckBox typeIncome;
    private CheckBox typeSpend;

    private CheckBox categorySumm;
    private CheckBox categoryBuy;
    private CheckBox categoryPay;
    private CheckBox categoryTrans;
    private CheckBox categoryIncome;
    private CheckBox categoryNo;

    private CheckBox accountSumm;

    private ListView userList;
    private LinearLayout LLaccount;
    private Cursor categoryCursor;
    private CheckBox checkBox;

    int a;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filtr_activity);

        userList = (ListView) findViewById(R.id.listFiltr);
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), TransactionActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);

            }
        });
        LinearLayout type = (LinearLayout) findViewById(R.id.LLtype);
        LLaccount = (LinearLayout) findViewById(R.id.LLaccount);

        typeSpend = (CheckBox) findViewById(R.id.typeSpend);
        typeIncome = (CheckBox) findViewById(R.id.typeIncome);
        typeSumm = (CheckBox) findViewById(R.id.typeSumm);

        categoryBuy = (CheckBox) findViewById(R.id.categoryBuy);
        categoryPay = (CheckBox) findViewById(R.id.categoryPay);
        categoryTrans = (CheckBox) findViewById(R.id.categoryTrans);
        categoryIncome = (CheckBox) findViewById(R.id.categoryIncome);
        categoryNo = (CheckBox) findViewById(R.id.categoryNo);
        categorySumm = (CheckBox) findViewById(R.id.categorySumm);

        accountSumm = (CheckBox) findViewById(R.id.accountSumm);

        dateStart = (EditText) findViewById(R.id.dateStart);
        dateFinish = (EditText) findViewById(R.id.dateFinish);
        DatabaseHelper = new DatabaseHelper(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //  actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setIcon(R.drawable.sber);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFFFF")));
        actionBar.setTitle("Фильтр");

    }

    @Override
    public void onResume() {
        super.onResume();
        db = DatabaseHelper.getReadableDatabase();

        Cursor accountCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_A, null);
       a = accountCursor.getCount();

        try {
            accountCursor.moveToFirst();

                while (!accountCursor.isAfterLast()) {
                  //  for (int i = 1; i < a+1; i++) {
                    checkBox = new CheckBox(this);

                    String BoxAccount = (accountCursor.getString(accountCursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
                    checkBox.setText(BoxAccount);
                    checkBox.setTag(BoxAccount);

                    //    String y = Integer.toString(i);
                    // int fID;
                    // do {
                    //     fID = LLaccount.generateViewId();
                    //  } while (findViewById(fID) != null);

                    checkBox.setId(accountCursor.getPosition());
                    System.out.println("IDIDIIDIDIDDIDIDIDIDIIDIIDDIIDIDIDIIIDDIDIDI" + checkBox.getId());

                    // checkBox.setId(accountCursor.getCount()-1);
                    LLaccount.addView(checkBox);

                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                accountSumm.setChecked(false);

                            } else {
                                // Your code
                            }

                        }
                    });
                    accountCursor.moveToNext();

                    accountSumm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                for (int i = 0; i < a ; i++) {
                                    CheckBox accountCheckBox = (CheckBox) LLaccount.findViewById(i);
                                    accountCheckBox.setChecked(false);
                                }
                            } else {
                                // Your code
                            }
                        }
                    });
               // }
            }
        } finally {
            accountCursor.close();
            db.close();

        }

    }

    public void Apply(View view) {

        // открываем подключение
        db = DatabaseHelper.getReadableDatabase();


        StringBuffer stringQuery = new StringBuffer();
        String[] argsQuery = new String[100];


        String[] type = new String[2];
        String[] category = new String[5];
        String[] account = new String[a];
       int countAccount = 0;

          stringQuery.append(" SELECT * FROM  transactions  WHERE ");
        if(accountSumm.isChecked()){
            stringQuery.append("( ");
            for (int i = 0; i < a; i++) {
                CheckBox accountCheckBox = (CheckBox) LLaccount.findViewById(i);
                account[i] =accountCheckBox.getText().toString();
                stringQuery.append(" name_bank  = ? OR ");
                argsQuery[i]=account[i];

            }
            countAccount = a;
            stringQuery.setLength(stringQuery.length()-4);
            stringQuery.append(" )");


        }

    else {
            stringQuery.append("( ");
            for (int i = 0; i < a; i++) {
                CheckBox accountCheckBox = (CheckBox) LLaccount.findViewById(i+1);
                if (accountCheckBox.isChecked()){
                    account[i] =accountCheckBox.getText().toString();
                    stringQuery.append(" name_bank  = ? OR ");
                    argsQuery[i]=account[i];
                    countAccount ++;
                }

            }
            stringQuery.setLength(stringQuery.length()-4);
            stringQuery.append(" ) ");
        }



        if(typeSumm.isChecked()){
            type[0] = typeSpend.getText().toString();
            type[1] = typeIncome.getText().toString();

            stringQuery.append(" AND ( type_transaction  = ?  OR  type_transaction = ? )");
               argsQuery[countAccount+1]=type[0];
               argsQuery[countAccount+2]=type[1];

            countAccount = countAccount+2;


        }

        else
        {    stringQuery.append(" AND (");
            if (typeSpend.isChecked()) {
                type[0] = typeSpend.getText().toString();
                argsQuery[countAccount+1]=type[0];
                countAccount ++;
                stringQuery.append(" type_transaction = ? OR ");
            }

            if (typeIncome.isChecked()) {
                type[1] = typeIncome.getText().toString();
                argsQuery[countAccount+1]=type[1];
                countAccount ++;
                stringQuery.append(" type_transaction  = ? OR ");

            }

            stringQuery.setLength(stringQuery.length()-4);
            stringQuery.append(" ) ");

        }


        if (categorySumm.isChecked()){
            category[0] = categoryBuy.getText().toString();
            category[1] = categoryPay.getText().toString();
            category[2] = categoryTrans.getText().toString();
            category[3] = categoryIncome.getText().toString();
            category[4] = categoryNo.getText().toString();

            stringQuery.append(" AND ( category  = ? OR category = ? OR category = ? OR category = ? OR category = ? OR )");
            argsQuery[countAccount+1]=category[0];
            argsQuery[countAccount+2]=category[1];
            argsQuery[countAccount+3]=category[2];
            argsQuery[countAccount+4]=category[3];
            argsQuery[countAccount+5]=category[4];
            countAccount = countAccount+5;
        }

        else {

            stringQuery.append(" AND (");
            if (categoryBuy.isChecked()) {
                category[0] = categoryBuy.getText().toString();
                argsQuery[countAccount+1]=category[0];
                countAccount ++;
                stringQuery.append(" category = ? OR ");
            }

            if (categoryPay.isChecked()) {
                category[1] = categoryPay.getText().toString();
                argsQuery[countAccount+1]=category[1];
                countAccount ++;
                stringQuery.append(" category = ? OR ");
            }

            if (categoryTrans.isChecked()) {
                category[2] = categoryTrans.getText().toString();
                argsQuery[countAccount+1]=category[2];
                countAccount ++;
                stringQuery.append(" category = ? OR ");
            }

            if (categoryIncome.isChecked()) {
                category[3] = categoryIncome.getText().toString();
                argsQuery[countAccount+1]=category[3];
                countAccount ++;
                stringQuery.append(" category  = ? OR ");
            }


            if (categoryNo.isChecked()) {
                category[4] = categoryNo.getText().toString();
                argsQuery[countAccount+1]=category[4];
                countAccount ++;
                stringQuery.append(" category = ? OR ");
            }


           // stringQuery = stringQuery.concat("+ \")");
        }

        stringQuery.setLength(stringQuery.length()-4);
        stringQuery.append(" )");

        System.out.println(stringQuery.toString());
        System.out.println(countAccount);
        String[] argsQueryNew = new String[countAccount];
        int count = 0;
        for(int i =0; i<15; i++)
        {
            if(argsQuery[i] != null){
            argsQueryNew[count] = argsQuery[i];

            count++;
            }
        }


        categoryCursor =  db.rawQuery(stringQuery.toString() +" AND "+ DatabaseHelper.COLUMN_DATE + " BETWEEN " + date.getTimeInMillis() + " AND " + date1.getTimeInMillis()
                +" order by " + DatabaseHelper.COLUMN_DATE +" DESC ", argsQueryNew);
        String[] headers = new String[] {DatabaseHelper.COLUMN_DESIGNATION, DatabaseHelper.COLUMN_DAY, DatabaseHelper.COLUMN_SUM};
        SimpleCursorAdapter categoryAdapter = new SimpleCursorAdapter(this, R.layout.my_list_item,
                categoryCursor, headers, new int[]{R.id.designat, R.id.dat, R.id.sum}, 0);

        userList.setAdapter(categoryAdapter);

        categoryCursor.close();

    }


    public void onTypeClicked(View view) {
        CheckBox nameType = (CheckBox) view;
        boolean checked = nameType.isChecked();

        switch(view.getId()) {
            case R.id.typeSumm:
                if (checked)
                    typeSpend.setChecked(false);
                     typeIncome.setChecked(false);
                break;

            case R.id.typeIncome:
                if(checked)
                    typeSumm.setChecked(false);
                    categorySumm.setChecked(false);
                categoryPay.setChecked(false);
                categoryTrans.setChecked(false);
                categoryBuy.setChecked(false);
                categoryIncome.setChecked(true);

                break;

            case R.id.typeSpend:
                if(checked)
                    typeSumm.setChecked(false);
                categoryIncome.setChecked(false);
                categorySumm.setChecked(false);
                break;
        }

    }

    public void onCategoryClicked(View view) {
        CheckBox nameCategory = (CheckBox) view;
        boolean checked = nameCategory.isChecked();
        switch(view.getId()) {
            case R.id.categorySumm:
                if (checked) {
                    categoryNo.setChecked(false);
                     categoryIncome.setChecked(false);
                    categoryTrans.setChecked(false);
                    categoryPay.setChecked(false);
                    categoryBuy.setChecked(false);
                }
                break;

            case R.id.categoryNo:
                if(checked)
                    categorySumm.setChecked(false);
                break;

            case R.id.categoryIncome :
                if(checked)
                    categorySumm.setChecked(false);
                typeSpend.setChecked(false);
                break;
            case R.id.categoryTrans :
                if(checked)
                    categorySumm.setChecked(false);
                typeIncome.setChecked(false);
                break;
            case R.id.categoryPay :
                if(checked)
                    categorySumm.setChecked(false);
                typeIncome.setChecked(false);
                break;
            case R.id.categoryBuy :
                if(checked)
                    categorySumm.setChecked(false);
                typeIncome.setChecked(false);
                break;
        }
    }



    public void setDateStart(View v) {
        new DatePickerDialog(FiltrActivity.this, start,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    DatePickerDialog.OnDateSetListener start=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            date.set(Calendar.YEAR, year);
            date.set(Calendar.MONTH, monthOfYear);
            date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateStart.setText(DateUtils.formatDateTime(FiltrActivity.this, date.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE) );

            String  monthSMS = date.getDisplayName(Calendar.MONTH,Calendar.LONG_FORMAT, new Locale("ru"));
            myYear = year;
            myMonth = monthOfYear;

            try {
                month = dateFormat.format(dateFormat1.parse(String.valueOf(myMonth+1).concat(" ").concat(String.valueOf(myYear))));
                System.out.println(month);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateStart.setText( dayOfMonth + " "+ monthSMS );

        }
    };

    public void setDateFinish(View v) {
        new DatePickerDialog(FiltrActivity.this, finish,
                date1.get(Calendar.YEAR),
                date1.get(Calendar.MONTH),
                date1.get(Calendar.DAY_OF_MONTH))
                .show();
    }
    DatePickerDialog.OnDateSetListener finish=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            date1.set(Calendar.YEAR, year);
            date1.set(Calendar.MONTH, monthOfYear);
            date1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateFinish.setText(DateUtils.formatDateTime(FiltrActivity.this, date1.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE) );

            String  monthSMS = date1.getDisplayName(Calendar.MONTH,Calendar.LONG_FORMAT, new Locale("ru"));
            myYear = year;
            myMonth = monthOfYear;

            try {
                month = dateFormat.format(dateFormat1.parse(String.valueOf(myMonth+1).concat(" ").concat(String.valueOf(myYear))));
                System.out.println(month);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            dateFinish.setText( dayOfMonth + " "+ monthSMS );
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
           //     categoryCursor.close();
                db.close();
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

      //  categoryCursor.close();
        db.close();

        //userCursor.close();
    }
}
