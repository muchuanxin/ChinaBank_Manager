package com.xd.aselab.chinabankmanager.my;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.util.CircleImageView;
import com.xd.aselab.chinabankmanager.util.ImageLoader;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

public class ManagerMyActivity extends AppCompatActivity {

    private RelativeLayout back;
    private SharePreferenceUtil spu;
    private ImageLoader imageLoader;
    private CircleImageView iv_head_photo;
    private RelativeLayout rl_my_performance;
    private RelativeLayout rl_my_base_performance;
    private RelativeLayout rl_my_online_chat;
    private RelativeLayout rl_my_Information;
    private Button exit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_my);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        spu = new SharePreferenceUtil(ManagerMyActivity.this,"user");
        imageLoader = ImageLoader.getInstance();

        back = (RelativeLayout)findViewById(R.id.act_manager_my_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_head_photo = (CircleImageView)findViewById(R.id.manager_my_head);
        imageLoader.loadBitmap(ManagerMyActivity.this,spu.getPhotoUrl(),iv_head_photo,R.drawable.portrait);

        rl_my_performance = (RelativeLayout)findViewById(R.id.manager_my_performance);
        rl_my_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ManagerMyActivity.this,BaseMyAllPerformanceActivity.class);
                intent.putExtra("account",spu.getAccount());
                intent.putExtra("type","1");
                startActivity(intent);
            }
        });

        rl_my_base_performance = (RelativeLayout)findViewById(R.id.manager_my_base_performance);
        rl_my_base_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ManagerMyActivity.this,BasePerfAllListActivity.class);
                startActivity(intent);
            }
        });

        rl_my_online_chat = (RelativeLayout)findViewById(R.id.manager_my_online);
        rl_my_online_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        rl_my_Information = (RelativeLayout)findViewById(R.id.manager_my_information);
        rl_my_Information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ManagerMyActivity.this,PersonalInfo.class);
                startActivity(intent);
            }
        });

        exit = (Button)findViewById(R.id.manager_my_tuichu);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spu.setIsLogin(false);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageLoader.loadBitmap(ManagerMyActivity.this, spu.getPhotoUrl(), iv_head_photo, R.drawable.portrait);
    }

}
