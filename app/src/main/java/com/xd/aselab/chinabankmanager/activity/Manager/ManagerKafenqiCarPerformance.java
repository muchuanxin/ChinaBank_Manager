package com.xd.aselab.chinabankmanager.activity.Manager;

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
import com.xd.aselab.chinabankmanager.kafenqi.KMyPreformenceActivity;
import com.xd.aselab.chinabankmanager.kafenqi.manager.BasePerformanceRandkingActivity;
import com.xd.aselab.chinabankmanager.kafenqi.manager.KBasePerformanceActivity;
import com.xd.aselab.chinabankmanager.kafenqi.manager.MannagerKafenqiActivity;

public class ManagerKafenqiCarPerformance extends AppCompatActivity {

    private RelativeLayout back;
    private LinearLayout my_performance;
    private LinearLayout base_ranking;
    private LinearLayout base_info;
    private LinearLayout base_performance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_kafenqi_car_performance);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.act_manager_kafenqi_car_perf_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        my_performance = (LinearLayout) findViewById(R.id.act_manager_kafenqi_car_perf_my_perf);
        my_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(ManagerKafenqiCarPerformance.this, KMyPreformenceActivity.class).putExtra("type","manager"));
            }
        });

        base_ranking = (LinearLayout) findViewById(R.id.act_manager_kafenqi_car_perf_base_ranking);
        base_ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(ManagerKafenqiCarPerformance.this, BasePerformanceRandkingActivity.class));
            }
        });

        base_info = (LinearLayout) findViewById(R.id.act_manager_kafenqi_car_perf_base_info);
        base_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(ManagerKafenqiCarPerformance.this, BaseInfo.class));
            }
        });

        base_performance = (LinearLayout) findViewById(R.id.act_manager_kafenqi_car_perf_base_perf);
        base_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(ManagerKafenqiCarPerformance.this, KBasePerformanceActivity.class));
            }
        });

    }
}
