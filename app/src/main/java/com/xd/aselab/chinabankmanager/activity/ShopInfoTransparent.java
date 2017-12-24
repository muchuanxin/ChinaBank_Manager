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

public class ShopInfoTransparent extends AppCompatActivity {

    private TextView contact;
    private TextView release;
    private TextView cancel;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_info_transparent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (getActionBar()!=null){
            getActionBar().hide();
        }

        position = getIntent().getIntExtra("position", 0);

        contact = (TextView) findViewById(R.id.act_shop_info_trans_contact);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("action", "contact");
                intent.putExtra("position", position);
                setResult(185, intent);
                finish();
                overridePendingTransition(R.anim.zoomout, R.anim.zoomin);
            }
        });

        release = (TextView) findViewById(R.id.act_shop_info_trans_release);
        release.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("action", "release");
                intent.putExtra("position", position);
                setResult(185, intent);
                finish();
                overridePendingTransition(R.anim.zoomout, R.anim.zoomin);
            }
        });

        cancel = (TextView) findViewById(R.id.act_shop_info_trans_cancel);
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
