package com.example.mycash;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class PlanFragment extends Fragment implements View.OnClickListener {

    private float values[];
    private  LinearLayout linear;
    private int width;
    private TextView buyCategory;
    private TextView payCategory;
    private TextView transferCategory;
    private TextView noCategory;
    private  TextView balance;
    public   Spinner month;
    private  DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private  Float BuyCategory;
    private  Float PayCategory;
    private  Float TransferCategory;
    private  Float NoCategory;

    private LinearLayout buy;
    private LinearLayout pay;
    private LinearLayout trans;
    private LinearLayout no;

    public PlanFragment() {
        // Required empty public constructor
    }

    public static PlanFragment newInstance() {
        return new PlanFragment();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_plan, null);

        linear = (LinearLayout) v.findViewById(R.id.linear);
        buyCategory = (TextView)v.findViewById(R.id.buyCategory);
        payCategory = (TextView)v.findViewById(R.id.payCategory);
        transferCategory = (TextView)v.findViewById(R.id.transferCategory);
        noCategory = (TextView)v.findViewById(R.id.noCategory);
        balance =(TextView)v.findViewById(R.id.balance);
        month =(Spinner) v.findViewById(R.id.month);

        buy = (LinearLayout)v.findViewById(R.id.layout_buy);
        pay = (LinearLayout)v.findViewById(R.id.layout_pay);
        trans = (LinearLayout)v.findViewById(R.id.layout_trans);
        no = (LinearLayout)v.findViewById(R.id.layout_no);

        buy.setOnClickListener(this);
        pay.setOnClickListener(this);
        trans.setOnClickListener(this);
        no.setOnClickListener(this);


        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(getActivity());

        WindowManager wm = (WindowManager) this.getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;


    }

    private float[] calculateData(float[] data) {
        // TODO Auto-generated method stub
        float total = 0;
        for (int i = 0; i < data.length; i++) {
            total += data[i];
        }
        for (int i = 0; i < data.length; i++) {
            data[i] = 360 * (data[i] / total);
        }
        return data;
    }

    public class MyGraphview extends View {
        private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private float[] value_degree;
        int[] COLORSTEXT = {Color.parseColor("#FFAA2C3B"), Color.parseColor("#FF3A25A6"), Color.parseColor("#FF1B8742"), Color.parseColor("#FF9C9912")};
        private int[] COLORS = {Color.parseColor("#e8475a"), Color.parseColor("#6247e8"), Color.parseColor("#47e882"), Color.parseColor("#e8e547")};
        RectF rectf = new RectF((width/2)-250, 50 , (width/2)+250, 550);


        float temp = 0f;

        public MyGraphview(Context context, float[] values) {

            super(context);
            value_degree = new float[values.length];
            for (int i = 0; i < values.length; i++) {
                value_degree[i] = values[i];
            }
        }


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int a= canvas.getWidth();
            int c = canvas.getHeight();
            Paint paintText = new Paint();
            paintText.setTextSize(35);
            paintText.setStyle(Paint.Style.FILL_AND_STROKE);




            for (int i = 0; i < value_degree.length; i++) {


                paint.setColor(COLORS[i]);
                paintText.setColor(COLORSTEXT[i]);

                canvas.drawArc(rectf, temp, value_degree[i], true, paint);

                System.out.println(temp+"ujn"+value_degree[i]);
                double Percent = (value_degree[i]) * 0.27778;

                int perсent = (int)Math.round(Percent);

                    float b = temp + value_degree[i]/2;

                    double tdeg =360-b ; // Calculated from arc percentage
                    int r =  canvas.getWidth()/4;      // Calculated from canvas width

                    double trad = tdeg * (Math.PI/180d); // = 5.1051

                    double x = r * Math.cos(trad);
                    double y =  r * Math.sin(trad);


                    if(b <90 )
                    canvas.drawText(String.valueOf(perсent) + "%", a/2+(float)x, c/2-(float)y, paintText);

                     else if (b < 180 && b > 90 )
                    canvas.drawText(String.valueOf(perсent) + "%", a/2+(float)x-80, c/2-(float)y, paintText);
                    //canvas.drawText("svsssss", 233, 135, paint);}

                    else if (b < 270 && b > 180 )
                        canvas.drawText(String.valueOf(perсent) + "%", a/2+(float)x-80, c/2-(float)y, paintText);

                    else if  (b < 360 && b > 270 )
                        canvas.drawText(String.valueOf(perсent) + "%", a/2+(float)x, c/2-(float)y, paintText);





                temp += (float) value_degree[i];

            }


        }
    }


    @Override
    public void onResume() {
        super.onResume();

        db = databaseHelper.getReadableDatabase();

        db = databaseHelper.getReadableDatabase();
        List<String> name_month = new ArrayList<>();
        Cursor monthCursor = db.rawQuery("select DISTINCT month from " + DatabaseHelper.TABLE + " order by " + DatabaseHelper.COLUMN_DATE +" DESC ", null);
        ArrayAdapter<String> adapterMonth = new ArrayAdapter<String>(getActivity(),
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
        month.setAdapter(adapterMonth);



        Cursor cursor = db.rawQuery("select sum("+ DatabaseHelper.COLUMN_BALANCE +") from "+ DatabaseHelper.TABLE_A, null);

        if (cursor.moveToFirst()) {
            Float Balance = cursor.getFloat(0);

            String formattedBalance = new DecimalFormat("#0.0").format(Balance);

            balance.setText(formattedBalance.concat(" ₽"));
        }
        cursor.close();


        month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //  db = databaseHelper.getReadableDatabase();
                String selectedItem = parentView.getItemAtPosition(position).toString();


        Cursor cursorBuy = db.rawQuery(" select sum (" + DatabaseHelper.COLUMN_SUM + ")  from "+ DatabaseHelper.TABLE + " WHERE "
                +  DatabaseHelper.COLUMN_MONTH +"= ?"+" AND "  +  DatabaseHelper.COLUMN_CATEGORY +"= ?" +" AND "
                +  DatabaseHelper.COLUMN_TYPE +"= ?",new String[]{selectedItem,"Покупки","Расходы"} );
        if( cursorBuy.moveToFirst()){
            BuyCategory=cursorBuy.getFloat(0);
            buyCategory.setText(Float.toString(BuyCategory).concat(" ₽"));
        }
        cursorBuy.close();

        Cursor cursorPay = db.rawQuery(" select sum (" + DatabaseHelper.COLUMN_SUM + ")  from "+ DatabaseHelper.TABLE + " WHERE "
                +  DatabaseHelper.COLUMN_MONTH +"= ?"+" AND "  +  DatabaseHelper.COLUMN_CATEGORY +"= ?"
                +" AND "  +  DatabaseHelper.COLUMN_TYPE +"= ?",new String[]{selectedItem,"Платежи","Расходы"} );
        if( cursorPay.moveToFirst()){
            PayCategory=cursorPay.getFloat(0);
            payCategory.setText(Float.toString(PayCategory).concat(" ₽"));
        }
        cursorPay.close();

        Cursor cursorTrans = db.rawQuery(" select sum (" + DatabaseHelper.COLUMN_SUM + ")  from "+ DatabaseHelper.TABLE + " WHERE "
                +  DatabaseHelper.COLUMN_MONTH +"= ?"+" AND "  +  DatabaseHelper.COLUMN_CATEGORY +"= ?"
                +" AND "  +  DatabaseHelper.COLUMN_TYPE+"= ?",new String[]{selectedItem,"Переводы","Расходы"} );
        if( cursorTrans.moveToFirst()){
            TransferCategory=cursorTrans.getFloat(0);
            transferCategory.setText(Float.toString(TransferCategory).concat(" ₽"));
        }
        cursorTrans.close();

        Cursor cursorNo = db.rawQuery(" select sum (" + DatabaseHelper.COLUMN_SUM + ")  from "+ DatabaseHelper.TABLE + " WHERE "
                +  DatabaseHelper.COLUMN_MONTH +"= ?"+" AND "  +  DatabaseHelper.COLUMN_CATEGORY +"= ?"
                +" AND "  +  DatabaseHelper.COLUMN_TYPE +"= ?",new String[]{selectedItem,"Без категории","Расходы"} );
        if( cursorNo.moveToFirst()){
            NoCategory=cursorNo.getFloat(0);
            noCategory.setText(Float.toString(NoCategory).concat(" ₽"));
        }
        cursorNo.close();
         linear.removeAllViews();
        //month.setText(dateFormat1.format(date));
        values = new float[]{BuyCategory, PayCategory, TransferCategory, NoCategory};
        values=calculateData(values);
        linear.addView(new MyGraphview(getActivity(),values));

      //  db.close();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

    }

    @Override
    public void onClick (View view) {
        Intent intent = new Intent(getActivity(), ReportActivity.class);
        String date = month.getSelectedItem().toString();
        intent.putExtra("category", view.getTag().toString());
        intent.putExtra("month_year", date);
        startActivity(intent);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();

    }



}




