package com.xd.aselab.chinabankmanager.activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.xd.aselab.chinabankmanager.R;

public class CBBenefitDetail extends AppCompatActivity {

    private RelativeLayout back;
    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbbenefit_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.act_cb_benefit_detail_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        web = (WebView) findViewById(R.id.act_cb_benefit_detail_web);
        web.loadUrl(getIntent().getStringExtra("content"));
    }
}
