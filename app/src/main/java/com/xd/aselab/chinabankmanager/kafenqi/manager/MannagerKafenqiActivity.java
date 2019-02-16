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
import com.xd.aselab.chinabankmanager.activity.BaseInfo;
import com.xd.aselab.chinabankmanager.activity.BasePerformance;
import com.xd.aselab.chinabankmanager.activity.Manager.ManagerKafenqiCarPerformance;
import com.xd.aselab.chinabankmanager.kafenqi.KMyPreformenceActivity;

public class MannagerKafenqiActivity extends AppCompatActivity {

    private RelativeLayout back;
    private LinearLayout car_business;
    private LinearLayout non_car_business;
    private LinearLayout customer_business;
    private LinearLayout virtual_4s_business;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mannager_kafenqi);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.act_manager_kafenqi_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        car_business = (LinearLayout) findViewById(R.id.act_manager_kafenqi_car_business);
        car_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(MannagerKafenqiActivity.this, ManagerKafenqiCarPerformance.class));
            }
        });

        non_car_business = (LinearLayout) findViewById(R.id.act_manager_kafenqi_non_car);
        non_car_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(MannagerKafenqiActivity.this, ErjiKafenqiNonCarActivity.class));
            }
        });

        customer_business = (LinearLayout) findViewById(R.id.act_manager_kafenqi_customer);
        customer_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(MannagerKafenqiActivity.this, ErjiKafenqiCustomerActivity.class));
            }
        });

        virtual_4s_business = (LinearLayout) findViewById(R.id.act_manager_kafenqi_virtual_4S);
        virtual_4s_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(MannagerKafenqiActivity.this, KBasePerformanceActivity.class));
            }
        });

    }
}
