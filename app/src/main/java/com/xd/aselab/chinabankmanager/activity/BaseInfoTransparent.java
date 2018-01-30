package com.xd.aselab.chinabankmanager.activity;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;

public class BaseInfoTransparent extends AppCompatActivity {

    private TextView contact;
    private TextView cancel;
    private String FromWhere;
    private String title;
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_info_transparent);

        FromWhere=(String) getIntent().getSerializableExtra("fromwhere");
        title=(String) getIntent().getSerializableExtra("title");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (getActionBar()!=null){
            getActionBar().hide();
        }


        contact = (TextView) findViewById(R.id.act_base_info_trans_contact);
        tv=(TextView) findViewById(R.id.current_title);
        if(FromWhere.equals("BaseInfo"))
        {

            contact.setText("联系他");
            tv.setText("银行卡客户经理信息");

        }else if (FromWhere.equals("K4SShopPerformanceActivity"))
        {
            contact.setText("强制解约");
            tv.setText("4S店业绩");
        }


      /*
      //将二级行与4S店解约（通过4S店销售账号） 已删除
      else
        {
            contact.setText("强制解约");
            tv.setText(title);
        }*/

        if(contact.getText().equals("联系他")) {
            contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = getIntent();
                    Intent intent2 = new Intent();
                    intent2.putExtra("position", intent1.getIntExtra("position", 0));
                    intent2.putExtra("action", "contact");
                    setResult(143, intent2);
                    finish();
                    overridePendingTransition(R.anim.zoomout, R.anim.zoomin);
                }
            });
        }
        else if(tv.getText().equals("4S店业绩"))
        {
            contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = getIntent();
                    Intent intent2 = new Intent();
                    intent2.putExtra("position", intent1.getIntExtra("position", 0));
                    intent2.putExtra("action", "contact");
                    setResult(141, intent2);
                    finish();
                    overridePendingTransition(R.anim.zoomout, R.anim.zoomin);
                }
            });
        }


    /*
    //  将二级行与4S店解约（通过4S店销售账号） 已删除

     else
        {
            contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = getIntent();
                    Intent intent2 = new Intent();
                    intent2.putExtra("position", intent1.getIntExtra("position", 0));
                    intent2.putExtra("action", "contact");
                    setResult(140, intent2);
                    finish();
                    overridePendingTransition(R.anim.zoomout, R.anim.zoomin);
                }
            });
        }*/


        cancel = (TextView) findViewById(R.id.act_base_info_trans_cancel);
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
