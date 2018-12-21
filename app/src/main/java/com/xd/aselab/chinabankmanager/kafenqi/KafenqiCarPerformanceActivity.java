package com.xd.aselab.chinabankmanager.kafenqi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;

import java.util.Calendar;

public class KafenqiCarPerformanceActivity extends AppCompatActivity {
    private RelativeLayout select_time;
    private RelativeLayout back;
    private ImageView click;
    private TextView recommend_num;
    private TextView recommend_money;
    private TextView success_num;
    private TextView success_money;
    private RelativeLayout gray_bar_top;
    private TextView gray_bar;
    private LinearLayout ll_chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kafenqi_car_performance);

        initViews();
        initEvents();
    }

    void initViews() {
        recommend_num = (TextView) findViewById(R.id.act_kafenqi_car_perf_recommend_num_text);
        recommend_money = (TextView) findViewById(R.id.act_kafenqi_car_perf_recommend_money_text);
        success_num = (TextView) findViewById(R.id.act_kafenqi_car_perf_success_num_text);
        success_money = (TextView) findViewById(R.id.act_kafenqi_car_perf_success_money_text);
        back = (RelativeLayout) findViewById(R.id.act_fenqi_my_perf_back_btn);
        select_time = (RelativeLayout) findViewById(R.id.rl_my_perf_select_time);
        gray_bar_top = (RelativeLayout)findViewById(R.id.act_kafenqi_my_perf_gray_bar_top);
        gray_bar = (TextView)findViewById(R.id.act_kafenqi_my_perf_gray_bar);
        gray_bar.setText(Calendar.getInstance().get(Calendar.YEAR)+"年各月分期业务情况分析");
        click = (ImageView) findViewById(R.id.click);
        ll_chart = (LinearLayout) findViewById(R.id.ll_chart);
    }

    void initEvents() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(KafenqiCarPerformanceActivity.this, KafenqiCarPerformanceDetail.class);
                startActivity(intent);
            }
        });
    }
}
