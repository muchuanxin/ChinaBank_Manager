package com.xd.aselab.chinabankmanager.activity.Manager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xd.aselab.chinabankmanager.R;

public class ManagerKafenqiNonCar extends AppCompatActivity {

    private RelativeLayout back;
    private LinearLayout non_car_my_performance;
    private LinearLayout non_car_base_performance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_kafenqi_non_car);

        back = (RelativeLayout) findViewById(R.id.act_manager_kafenqi_non_car_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        non_car_my_performance = (LinearLayout) findViewById(R.id.act_manager_kafenqi_non_car_my_perf);
        non_car_my_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ManagerKafenqiNonCar.this, null);
                startActivity(intent);
            }
        });

        non_car_base_performance = (LinearLayout) findViewById(R.id.act_manager_kafenqi_non_car_base_perf);
        non_car_base_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ManagerKafenqiNonCar.this, null);
                startActivity(intent);
            }
        });
    }
}
