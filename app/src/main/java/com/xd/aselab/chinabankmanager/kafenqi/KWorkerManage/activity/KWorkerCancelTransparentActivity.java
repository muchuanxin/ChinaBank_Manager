package com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.activity;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;

public class KWorkerCancelTransparentActivity extends AppCompatActivity {

    private TextView contact;
    private TextView agree;
    private TextView refuse;
    private TextView cancel;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kworker_cancel_transparent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (getActionBar()!=null){
            getActionBar().hide();
        }

        position = getIntent().getIntExtra("position", 0);

        contact = (TextView) findViewById(R.id.act_k_worker_cancel_trans_contact);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("action", "contact");
                intent.putExtra("position", position);
                setResult(903, intent);
                finish();
                overridePendingTransition(R.anim.zoomout, R.anim.zoomin);
            }
        });

        agree = (TextView) findViewById(R.id.act_k_worker_cancel_trans_agree);
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("action", "agree");
                intent.putExtra("position", position);
                setResult(903, intent);
                finish();
                overridePendingTransition(R.anim.zoomout, R.anim.zoomin);
            }
        });

        refuse = (TextView) findViewById(R.id.act_k_worker_cancel_trans_refuse);
        refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("action", "refuse");
                intent.putExtra("position", position);
                setResult(903, intent);
                finish();
                overridePendingTransition(R.anim.zoomout, R.anim.zoomin);
            }
        });

        cancel = (TextView) findViewById(R.id.act_k_worker_cancel_trans_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.zoomout, R.anim.zoomin);
            }
        });

    }

}
