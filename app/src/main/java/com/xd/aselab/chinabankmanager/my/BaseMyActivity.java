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

import cn.jpush.android.api.JPushInterface;

public class BaseMyActivity extends AppCompatActivity {

    private SharePreferenceUtil spu;
    private RelativeLayout back;
    private CircleImageView iv_head_photo;
    private RelativeLayout rl_my_performance;
    private RelativeLayout rl_my_Information;
    private ImageLoader imageLoader;
    private Button exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_my);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        spu = new SharePreferenceUtil(BaseMyActivity.this,"user");
        imageLoader = ImageLoader.getInstance();

        back = (RelativeLayout) findViewById(R.id.act_base_my_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_head_photo=(CircleImageView)findViewById(R.id.base_my_head);
        imageLoader.loadBitmap(BaseMyActivity.this,spu.getPhotoUrl(),iv_head_photo,R.drawable.default_head);

        rl_my_performance = (RelativeLayout)findViewById(R.id.base_my_performance);
        rl_my_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(BaseMyActivity.this,BaseMyAllPerformanceActivity.class);
                intent.putExtra("account",spu.getAccount());
                intent.putExtra("type","0");
                startActivity(intent);
            }
        });

        rl_my_Information = (RelativeLayout)findViewById(R.id.base_my_information);
        rl_my_Information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(BaseMyActivity.this,PersonalInfo.class);
                startActivity(intent);
            }
        });

        exit = (Button)findViewById(R.id.base_my_tuichu);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spu.setIsLogin(false);
                JPushInterface.deleteAlias(BaseMyActivity.this,0);
//                Intent intent = new Intent();
//                intent.putExtra("action", "check_out");
//                setResult(178, intent);
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        imageLoader.loadBitmap(BaseMyActivity.this, spu.getPhotoUrl(), iv_head_photo, R.drawable.portrait);
    }
}
