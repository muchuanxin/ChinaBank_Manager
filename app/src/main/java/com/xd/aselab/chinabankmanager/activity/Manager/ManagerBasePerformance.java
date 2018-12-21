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
import com.xd.aselab.chinabankmanager.activity.BasePerformance;
import com.xd.aselab.chinabankmanager.activity.PerformanceRanking;

public class ManagerBasePerformance extends AppCompatActivity {

    private LinearLayout base_ranking;
    private LinearLayout base_info;
    private LinearLayout base_performance;
    private RelativeLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_base_performance);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        base_ranking = (LinearLayout) findViewById(R.id.act_manager_base_ranking);
        base_info = (LinearLayout) findViewById(R.id.act_manager_base_info);
        base_performance = (LinearLayout) findViewById(R.id.act_manager_base_performance);
        back = (RelativeLayout) findViewById(R.id.act_my_mana_back_btn);


        base_ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(ManagerBasePerformance.this, PerformanceRanking.class));
            }
        });

        base_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(ManagerBasePerformance.this, BaseInfo.class));
            }
        });

        base_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(ManagerBasePerformance.this, BasePerformance.class));
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
