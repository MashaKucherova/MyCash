package com.example.mycash;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AccountFragment extends Fragment {

    public AccountFragment() {
    }
    private  ListView userList;
    private Cursor accountCursor;
    private SimpleCursorAdapter userAdapter1;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private TextView balance;


    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.account_fragment, null);

        balance = (TextView)v.findViewById(R.id.balance);
        userList = (ListView) v.findViewById(R.id.list_account);
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), AccountTransactions.class);
                intent.putExtra("id", id);
                startActivity(intent);

            }
        });

        return v;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(getActivity());
       // db = databaseHelper.getReadableDatabase();

    }
    @Override
    public void onResume() {
        super.onResume();

        // открываем подключение
        db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select sum("+ DatabaseHelper.COLUMN_BALANCE +") from "+ DatabaseHelper.TABLE_A, null);

        if (cursor.moveToFirst()) {
            Float Balance = cursor.getFloat(0);

            String formattedBalance = new DecimalFormat("#0.0").format(Balance);

            balance.setText(formattedBalance.concat(" ₽"));
        }
        cursor.close();
        DecimalFormat decimalFormat =  new DecimalFormat("#0.0");

        accountCursor =  db.rawQuery("select _id, name,  balance_account from "+ DatabaseHelper.TABLE_A, null);
        String[] headersA = new String[] {DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_BALANCE};
        userAdapter1 = new SimpleCursorAdapter(getActivity(), R.layout.account_list_item,
                accountCursor, headersA, new int[]{R.id.nameAccount, R.id.balanceAccount}, 0);

        //СДЕЛАТЬ СВОЙ АДАПТЕР ЧТОБЫ ВЫВОДИЛИСЬ ЗНАЧКИ СЧЕТА


        userList.setAdapter(userAdapter1);


        db.close();
    }

    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        accountCursor.close();
    }
}
