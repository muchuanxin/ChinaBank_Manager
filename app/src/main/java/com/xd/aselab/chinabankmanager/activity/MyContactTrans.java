package com.xd.aselab.chinabankmanager.activity;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;

public class MyContactTrans extends AppCompatActivity {

    private TextView tel;
    private TextView landline;
    private TextView cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact_trans);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (getActionBar()!=null){
            getActionBar().hide();
        }

        Intent intent = getIntent();

        tel = (TextView) findViewById(R.id.act_my_cont_trans_tel);
        tel.setText(intent.getStringExtra("tel"));
        tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.putExtra("contact", tel.getText().toString());
                intent2.putExtra("action", "contact");
                setResult(125, intent2);
                finish();
                overridePendingTransition(R.anim.zoomout, R.anim.zoomin);
            }
        });

        landline = (TextView) findViewById(R.id.act_my_cont_trans_landline);
        landline.setText(intent.getStringExtra("landline"));
        landline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.putExtra("contact", landline.getText().toString());
                intent2.putExtra("action","contact");
                setResult(125, intent2);
                finish();
                overridePendingTransition(R.anim.zoomout, R.anim.zoomin);
            }
        });

        cancel = (TextView) findViewById(R.id.act_my_cont_trans_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.zoomout, R.anim.zoomin);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        overridePendingTransition(R.anim.zoomout, R.anim.zoomin);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.zoomout, R.anim.zoomin);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
