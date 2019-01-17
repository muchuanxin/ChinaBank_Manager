package com.xd.aselab.chinabankmanager.activity.province;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.activity.Manager.VirtualGrabRankActivity;
import com.xd.aselab.chinabankmanager.activity.Manager.VirtualPerformanceRankActivity;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

public class VirtualListActivity extends AppCompatActivity {

    private RelativeLayout rl_back;
    private LinearLayout ll_performance_rank;
    private LinearLayout ll_grab_rank;
    private LinearLayout ll_white_list;
    private LinearLayout ll_black_list;
    private PopupWindow pop;
    private SharePreferenceUtil spu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        initViews();
        initDatas();
        initEvents();
    }

    private void initViews(){
        rl_back = (RelativeLayout) findViewById(R.id.vl_back_btn);
        ll_performance_rank = (LinearLayout) findViewById(R.id.vl_performance_rank);
        ll_grab_rank = (LinearLayout) findViewById(R.id.vl_grab_ability_rank);
        ll_white_list = (LinearLayout) findViewById(R.id.vl_white_list);
        ll_black_list = (LinearLayout) findViewById(R.id.vl_black_list);
    }

    private void initDatas(){
        spu = new SharePreferenceUtil(VirtualListActivity.this,"user");
    }

    private void initEvents(){
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ll_performance_rank.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(VirtualListActivity.this, VirtualPerformanceRankActivity.class);
                startActivity(intent);
            }
        });

        ll_grab_rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(VirtualListActivity.this, VirtualGrabRankActivity.class);
                startActivity(intent);
            }
        });

        ll_white_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(VirtualListActivity.this, WhiteListActivity.class);
                startActivity(intent);
            }
        });
        ll_black_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(VirtualListActivity.this, BlackListActivity.class);
                startActivity(intent);
            }
        });
    }
}
