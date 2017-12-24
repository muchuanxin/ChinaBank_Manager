package com.xd.aselab.chinabankmanager.activity;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xd.aselab.chinabankmanager.R;

public class ShopManage extends AppCompatActivity {

    private RelativeLayout back;
    private LinearLayout shop_info;
    private LinearLayout shop_perf;
    private LinearLayout shop_join;
    private LinearLayout shop_release;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_manage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.act_shop_mana_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        shop_info = (LinearLayout) findViewById(R.id.act_shop_mana_shop_info);
        shop_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(ShopManage.this, ShopInfo.class));
            }
        });

        shop_perf = (LinearLayout) findViewById(R.id.act_shop_mana_shop_perf);
        shop_perf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(ShopManage.this, ShopPerformance.class));
            }
        });

        shop_join = (LinearLayout) findViewById(R.id.act_shop_mana_shop_join);
        shop_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(ShopManage.this, ShopJoin.class));
            }
        });

        shop_release = (LinearLayout) findViewById(R.id.act_shop_mana_shop_release);
        shop_release.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(ShopManage.this, ShopRelease.class));
            }
        });

    }

}