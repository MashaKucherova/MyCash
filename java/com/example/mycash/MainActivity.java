package com.example.mycash;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private int currentPageId = 0;
    PlanFragment a = new PlanFragment();

    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_content, fragment);
        ft.commit();
    }
    public void add(View view){
        Intent intent = new Intent(this, TransactionActivity.class);
        startActivity(intent);}



    public void addAccount(View view){
        Intent intent = new Intent(this, AccountAdd.class);
        startActivity(intent);}

        public void filtr (View view){
        Intent intent = new Intent(this,FiltrActivity.class);
        startActivity(intent);
        }


    public void spend (View view){
            Intent intent = new Intent(this,ReportActivity.class);
            String date = a.month.getSelectedItem().toString();
            intent.putExtra("category",view.getTag().toString());
            intent.putExtra("month_year", date);
            startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener(){

       // BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
          //      = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(currentPageId == item.getItemId())
                    return false;
                else {
                    currentPageId = item.getItemId();
                    switch (item.getItemId()) {
                        case R.id.bottomNavigationTransactionMenuId:
                             loadFragment(TransactionFragment.newInstance());
                             return true;
                        case R.id.bottomNavigationAccountMenuId:
                            loadFragment(AccountFragment.newInstance());
                              return true;
                        case R.id.bottomNavigationPlanMenuId:
                            loadFragment(PlanFragment.newInstance());
                             return true;

                }
                     return false;
                 // return true;
                }

            }

        });
        navigation.setSelectedItemId(R.id.bottomNavigationTransactionMenuId);



    }



}
