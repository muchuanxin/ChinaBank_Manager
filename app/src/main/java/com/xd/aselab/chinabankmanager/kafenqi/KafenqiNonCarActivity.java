package com.xd.aselab.chinabankmanager.kafenqi;

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

public class KafenqiNonCarActivity extends AppCompatActivity {

    private RelativeLayout back;
    private LinearLayout non_car_recommend;
    private LinearLayout non_car_performance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kafenqi_non_car);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.act_kafenqi_non_car_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        non_car_recommend = (LinearLayout) findViewById(R.id.act_kafenqi_non_car_recommend);
        non_car_recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(KafenqiNonCarActivity.this, KafenqiRecommendActivity.class);
                intent.putExtra("flag", "KafenqiNonCarActivity");
                startActivity(intent);
            }
        });

        non_car_performance = (LinearLayout) findViewById(R.id.act_kafenqi_non_car_my_performance);
        non_car_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(KafenqiNonCarActivity.this, KafenqiNonCarPerformance.class);
                startActivity(intent);
            }
        });

    }


}
