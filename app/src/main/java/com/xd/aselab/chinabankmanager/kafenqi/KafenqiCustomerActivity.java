package com.xd.aselab.chinabankmanager.kafenqi;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xd.aselab.chinabankmanager.R;

public class KafenqiCustomerActivity extends AppCompatActivity {

    private RelativeLayout back;
    private LinearLayout customer_recommend;
    private LinearLayout customer_performance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kafenqi_customer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.act_kafenqi_customer_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        customer_recommend = (LinearLayout) findViewById(R.id.act_kafenqi_customer_recommend);
        customer_recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(KafenqiCustomerActivity.this, KafenqiRecommendActivity.class);
                intent.putExtra("flag", "KafenqiCustomerActivity");
                startActivity(intent);
            }
        });

        customer_performance = (LinearLayout) findViewById(R.id.act_kafenqi_customer_my_performance);
        customer_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(KafenqiCustomerActivity.this, KafenqiCustomerPerformance.class);
                startActivity(intent);
            }
        });
    }

}
