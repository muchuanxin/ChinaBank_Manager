package com.xd.aselab.chinabankmanager.activity;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;

public class MyPerfTransparent extends AppCompatActivity {

    private TextView week;
    private TextView one_month;
    private TextView three_month;
    private TextView year;
    private TextView total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_perf_transparent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (getActionBar()!=null){
            getActionBar().hide();
        }

        week = (TextView) findViewById(R.id.act_my_perf_trans_week);
        one_month = (TextView) findViewById(R.id.act_my_perf_trans_one_month);
        three_month = (TextView) findViewById(R.id.act_my_perf_trans_three_month);
        year = (TextView) findViewById(R.id.act_my_perf_trans_year);
        total = (TextView) findViewById(R.id.act_my_perf_trans_total);

        View.OnClickListener myOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                switch (v.getId()){
                    case R.id.act_my_perf_trans_week :
                        intent.putExtra("time", "week");
                        break;
                    case R.id.act_my_perf_trans_one_month :
                        intent.putExtra("time", "one_month");
                        break;
                    case R.id.act_my_perf_trans_three_month :
                        intent.putExtra("time", "three_month");
                        break;
                    case R.id.act_my_perf_trans_year :
                        intent.putExtra("time", "year");
                        break;
                    case R.id.act_my_perf_trans_total :
                        intent.putExtra("time", "total");
                        break;
                }
                setResult(123,intent);
                finish();
            }
        };

        week.setOnClickListener(myOnClickListener);
        one_month.setOnClickListener(myOnClickListener);
        three_month.setOnClickListener(myOnClickListener);
        year.setOnClickListener(myOnClickListener);
        total.setOnClickListener(myOnClickListener);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }
}
