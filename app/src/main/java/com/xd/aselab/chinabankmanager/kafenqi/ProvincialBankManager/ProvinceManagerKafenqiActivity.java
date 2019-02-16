package com.xd.aselab.chinabankmanager.kafenqi.ProvincialBankManager;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.activity.Manager.ManagerKafenqiCarPerformance;
import com.xd.aselab.chinabankmanager.activity.province.ErjiScoreDivisionActivity;
import com.xd.aselab.chinabankmanager.activity.province.VirtualListActivity;
import com.xd.aselab.chinabankmanager.kafenqi.manager.ErjiKafenqiCustomerActivity;
import com.xd.aselab.chinabankmanager.kafenqi.manager.ErjiKafenqiNonCarActivity;
import com.xd.aselab.chinabankmanager.kafenqi.manager.KBasePerformanceActivity;
import com.xd.aselab.chinabankmanager.kafenqi.manager.MannagerKafenqiActivity;

public class ProvinceManagerKafenqiActivity extends AppCompatActivity {

    private RelativeLayout back;
    private LinearLayout car_business;
    private LinearLayout non_car_business;
    private LinearLayout customer_business;
    private LinearLayout virtual_4s_business;
    private LinearLayout erji_score_division;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province_manager_kafenqi);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.act_province_kafenqi_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        car_business = (LinearLayout) findViewById(R.id.act_province_kafenqi_car_business);
        car_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(ProvinceManagerKafenqiActivity.this, KafenqiFirstActivity.class));
            }
        });

        non_car_business = (LinearLayout) findViewById(R.id.act_province_kafenqi_non_car_business);
        non_car_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(ProvinceManagerKafenqiActivity.this, ProvinceManagerKafenqiNonCarErjiActivity.class));
            }
        });

        customer_business = (LinearLayout) findViewById(R.id.act_province_kafenqi_customer_business);
        customer_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(ProvinceManagerKafenqiActivity.this, ProvinceManagerKafenqiCustomerErjiPerformance.class));
            }
        });

        virtual_4s_business = (LinearLayout) findViewById(R.id.act_province_kafenqi_virtual_4S_business);
        virtual_4s_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(ProvinceManagerKafenqiActivity.this, VirtualListActivity.class));
            }
        });

        erji_score_division = (LinearLayout) findViewById(R.id.act_province_kafenqi_erji_score_division);
        erji_score_division.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(ProvinceManagerKafenqiActivity.this, ErjiScoreDivisionActivity.class));
            }
        });



    }
}
