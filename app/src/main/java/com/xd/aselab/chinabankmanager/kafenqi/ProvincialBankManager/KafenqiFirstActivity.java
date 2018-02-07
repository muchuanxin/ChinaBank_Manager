package com.xd.aselab.chinabankmanager.kafenqi.ProvincialBankManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.kafenqi.KMyPreformenceActivity;
import com.xd.aselab.chinabankmanager.kafenqi.KOnlineCommunicate.KOnlineCommuActivity;
import com.xd.aselab.chinabankmanager.kafenqi.KPublishAnnouncement.SelectPromoterActivity;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.activity.KMyContactsActivity;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.activity.KWorkerManageActivity;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

public class KafenqiFirstActivity extends AppCompatActivity {

    private RelativeLayout back;

    private LinearLayout my_performance;
    private LinearLayout worker_management;
    private SharePreferenceUtil sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kafenqi_first);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        initViews();
        initDatas();
        initEvents();
    }

    private void initViews(){
        back = (RelativeLayout) findViewById(R.id.act_kafenqi_back_btn);

        my_performance = (LinearLayout) findViewById(R.id.act_kafenqi_my_perf);
        worker_management = (LinearLayout) findViewById(R.id.act_kafenqi_worker_management);


    }

    private void initDatas(){
        sp=new SharePreferenceUtil(KafenqiFirstActivity.this,"user");


    }



    private void initEvents(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        my_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(KafenqiFirstActivity.this,KSubManagerPerformanceActivity.class);
                startActivity(intent);
            }
        });
        worker_management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(KafenqiFirstActivity.this,KSubManagerPerformanceActivity2.class);
                startActivity(intent);

            }
        });


    }

    private void showPopUpWindow(){
        //设置contentView
        View contentView = LayoutInflater.from(KafenqiFirstActivity.this).inflate(R.layout.big_code, null);



        //显示PopupWindow
        View rootview = LayoutInflater.from(KafenqiFirstActivity.this).inflate(R.layout.activity_kafenqi, null);

        LinearLayout background = (LinearLayout) contentView.findViewById(R.id.big_code_layout);

    }

}
