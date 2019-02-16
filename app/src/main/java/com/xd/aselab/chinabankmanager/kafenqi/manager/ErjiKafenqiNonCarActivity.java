package com.xd.aselab.chinabankmanager.kafenqi.manager;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xd.aselab.chinabankmanager.R;

public class ErjiKafenqiNonCarActivity extends AppCompatActivity {

    RelativeLayout back;
    LinearLayout my_performance;
    LinearLayout base_performance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erji_kafenqi_non_car);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.act_erji_kafenqi_non_car_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        my_performance = (LinearLayout) findViewById(R.id.act_erji_kafenqi_non_car_my_perf);
        my_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ErjiKafenqiNonCarActivity.this, ErjiKafenqiNonCarMyPerformance.class);
                startActivity(intent);
            }
        });

        base_performance = (LinearLayout) findViewById(R.id.act_erji_kafenqi_non_car_base_performance);
        base_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ErjiKafenqiNonCarActivity.this, ErjiKafenqiNonCarBasePerformance.class);
                startActivity(intent);
            }
        });
    }
}
