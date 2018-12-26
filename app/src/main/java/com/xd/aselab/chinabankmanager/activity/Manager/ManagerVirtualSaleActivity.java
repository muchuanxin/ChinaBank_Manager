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

public class ManagerVirtualSaleActivity extends AppCompatActivity {

    private LinearLayout ll_performanceRank;
    private LinearLayout ll_grabRank;
    private RelativeLayout rl_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_virtual_sale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initView();
        initEvents();
    }

    private void initView() {
        rl_back = (RelativeLayout) findViewById(R.id.mvs_back_btn);
        ll_performanceRank = (LinearLayout) findViewById(R.id.mvs_performance_rank);
        ll_grabRank = (LinearLayout) findViewById(R.id.mvs_grab_rank);

    }

    private void initEvents() {
        // 返回事件
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 业绩排名
        ll_performanceRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(ManagerVirtualSaleActivity.this, VirtualPerformanceRankActivity.class).putExtra("type", "manager"));
            }
        });

        // 抢单能力排名
        ll_grabRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(ManagerVirtualSaleActivity.this, VirtualGrabRankActivity.class));
            }
        });
    }

}
