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
import com.xd.aselab.chinabankmanager.activity.MyPerformance;
import com.xd.aselab.chinabankmanager.activity.PerformanceRanking;

public class MyManagement extends AppCompatActivity {

    private RelativeLayout back;
    private LinearLayout my_performance;
    private LinearLayout base_performance;
    private LinearLayout lobby_performance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_management);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.act_my_mana_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        my_performance = (LinearLayout) findViewById(R.id.act_my_mana_my_perf);
        my_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(MyManagement.this, MyPerformance.class).putExtra("type",1));
            }
        });

        base_performance = (LinearLayout) findViewById(R.id.act_my_mana_base_performance);
        base_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(MyManagement.this, ManagerBasePerformance.class));
            }
        });

        lobby_performance = (LinearLayout) findViewById(R.id.act_my_mana_lobby_performance);
        lobby_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(MyManagement.this, LobbyPerformance.class));
            }
        });


    }
}
