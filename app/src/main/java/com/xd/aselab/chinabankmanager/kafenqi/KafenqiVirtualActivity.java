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
import com.xd.aselab.chinabankmanager.grabOrder.GrabOrderActivity;
import com.xd.aselab.chinabankmanager.grabOrder.GrabPerformanceActivity;

public class KafenqiVirtualActivity extends AppCompatActivity {

    private LinearLayout virtual_grab;
    private LinearLayout virtual_performance;
    private RelativeLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kafenqi_virtual);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.act_kafenqi_virtual_4s_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        virtual_grab = (LinearLayout) findViewById(R.id.act_kafenqi_virtual_4s_grab);
        virtual_performance = (LinearLayout) findViewById(R.id.act_kafenqi_virtual_4s_performance);

        virtual_grab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
//                GrabOrderActivity.class
                intent.setClass(KafenqiVirtualActivity.this, GrabOrderActivity.class);
                startActivity(intent);
            }
        });

        virtual_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(KafenqiVirtualActivity.this, GrabPerformanceActivity.class);
                startActivity(intent);
            }
        });
    }
}
