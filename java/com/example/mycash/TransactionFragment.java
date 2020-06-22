package com.example.mycash;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransactionFragment extends Fragment {
    private ListView userList;
    private DatabaseHelper databaseHelper;
    private  SQLiteDatabase db;
    private  Cursor userCursor;
    private  EditText userFilter;
    private  SimpleCursorAdapter userAdapter;
    private  TextView balance;
    private final int REQUEST_CODE_PERMISSION_READ_SMS=10;
    private int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
    private static Context contextthis;



        public TransactionFragment() {
        }

        public static TransactionFragment newInstance() {
            return new TransactionFragment();
        }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.transaction_fragment, null);
        userList = (ListView) v.findViewById(R.id.list);
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), TransactionActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);

            }
        });
        balance = (TextView)v.findViewById(R.id.balance);
        userFilter = (EditText)v.findViewById(R.id.userFilter);
        return  v;
    }

    public static Context getContextthis()
    {
        return contextthis;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        contextthis = getContext();
        databaseHelper = new DatabaseHelper(getActivity());
        db = databaseHelper.getReadableDatabase();


        int permissionStatus = ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.READ_SMS);
        //  int permissionStatus1 = ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.RECEIVE_SMS);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {

            SMSRead sc = new SMSRead();
            sc.getAllSms(getActivity());

        } else {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.READ_SMS},
                    REQUEST_CODE_PERMISSION_READ_SMS);
        }
    }
    /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_READ_SMS:
                if (grantResults.length ==2
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Cursor cursor1 = db.rawQuery("select * from "+ DatabaseHelper.TABLE, null);
                   // if (cursor1.getCount()==0){
                        SMSRead sc = new SMSRead();
                        sc.getAllSms(getActivity());
                    //}
                    cursor1.close();
                } else {
                    // permission denied
                }
                return;
        }
    }*/

    @Override
    public void onResume() {
        super.onResume();

        // открываем подключение
        db = databaseHelper.getReadableDatabase();

        //получаем данные из бд в виде курсора
        Cursor cursor = db.rawQuery("select sum("+ DatabaseHelper.COLUMN_BALANCE +") from "+ DatabaseHelper.TABLE_A, null);

        if (cursor.moveToFirst()) {
           Float Balance = cursor.getFloat(0);

        String formattedBalance = new DecimalFormat("#0.0").format(Balance);

        balance.setText(formattedBalance.concat(" ₽"));
        }
        cursor.close();


        userCursor =  db.rawQuery("select * from "+ DatabaseHelper.TABLE +" order by " + DatabaseHelper.COLUMN_DATE +" DESC ", null);
        // определяем, какие столбцы из курсора будут выводиться в ListView
        String[] headers = new String[] {DatabaseHelper.COLUMN_DESIGNATION, DatabaseHelper.COLUMN_DAY, DatabaseHelper.COLUMN_SUM};
        // создаем адаптер, передаем в него курсор
        userAdapter = new SimpleCursorAdapter(getActivity(), R.layout.my_list_item,
                userCursor, headers, new int[]{R.id.designat, R.id.dat, R.id.sum}, 0);
        userList.setAdapter(userAdapter);

        if(!userFilter.getText().toString().isEmpty())
            userAdapter.getFilter().filter(userFilter.getText().toString());

        userFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) { }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            // при изменении текста выполняем фильтрацию
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                userAdapter.getFilter().filter(s.toString());
            }
        });

        // устанавливаем провайдер фильтрации
        userAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {

                if (constraint == null || constraint.length() == 0) {

                    return db.rawQuery("select * from " + DatabaseHelper.TABLE +" order by " + DatabaseHelper.COLUMN_DATE +" DESC ", null);
                }
                else {
                    return db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                            DatabaseHelper.COLUMN_DESIGNATION + " like ? " +
                            " OR " + DatabaseHelper.COLUMN_DAY + " like ? "+
                            " OR " + DatabaseHelper.COLUMN_BANK + " like ? "+
                            " OR " + DatabaseHelper.COLUMN_TYPE + " like ? "+
                            " OR " + DatabaseHelper.COLUMN_SUM + " like ? "+
                            " OR " + DatabaseHelper.COLUMN_CATEGORY + " like ? "+
                            " order by " + DatabaseHelper.COLUMN_DATE +" DESC ", new String[]{"%" + constraint.toString() + "%", "%" + constraint.toString() + "%"
                            , "%" + constraint.toString() + "%", "%" + constraint.toString() + "%", "%" + constraint.toString() + "%", "%" + constraint.toString() + "%"});


                }
            }
        });

        userList.setAdapter(userAdapter);

        // db.close();
        cursor.close();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
         db.close();
     //userCursor.close();
    }



}
