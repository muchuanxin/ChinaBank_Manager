package com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.activity;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.kafenqi.KMyPreformenceActivity;

public class KWorkerManageActivity extends AppCompatActivity {

    private RelativeLayout back;
    private LinearLayout ll_worker_my_performance;
    private LinearLayout ll_worker_performance;
    private LinearLayout ll_worker_join_check;
    private LinearLayout ll_worker_cancel_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kworker_manage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout)findViewById(R.id.act_kafenqi_worker_manag_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ll_worker_my_performance = (LinearLayout) findViewById(R.id.act_kafenqi_worker_manag_my_perf);
        ll_worker_my_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("type","basic");
                intent.setClass(KWorkerManageActivity.this,KMyPreformenceActivity.class);
                startActivity(intent);
            }
        });

        ll_worker_performance = (LinearLayout)findViewById(R.id.act_kafenqi_worker_manag_perf);
        ll_worker_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(KWorkerManageActivity.this,WorkerPerformenceActivity.class);
                startActivity(intent);
            }
        });

        ll_worker_join_check = (LinearLayout)findViewById(R.id.act_kafenqi_worker_manag_join_verify);
        ll_worker_join_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(KWorkerManageActivity.this,KWorkerJoinCheckActivity.class);
                startActivity(intent);
            }
        });

        ll_worker_cancel_check = (LinearLayout) findViewById(R.id.act_kafenqi_worker_cancel_check);
        ll_worker_cancel_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(KWorkerManageActivity.this,KWorkerCancelCheckActivity.class);
                startActivity(intent);
            }
        });

    }


}
