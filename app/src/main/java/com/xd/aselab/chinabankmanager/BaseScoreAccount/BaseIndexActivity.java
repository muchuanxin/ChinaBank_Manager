package com.xd.aselab.chinabankmanager.BaseScoreAccount;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.util.ImageLoader;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import static com.xd.aselab.chinabankmanager.util.Constants.LOGIN_TO_MAIN_HOME;

public class BaseIndexActivity extends AppCompatActivity {

    private ImageView head_photo;
    private ImageLoader imageLoader;
    private SharePreferenceUtil sp;
    private TextView user_name;
    private long mExitTime;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_score_index);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        sp = new SharePreferenceUtil(BaseIndexActivity.this, "user");

        // 读取并显示用户名
        user_name = (TextView) findViewById(R.id.user_name);
        user_name.setText(sp.getName());
        Log.d("Dorise", sp.getName());
        Log.d("Dorise", "=============");

        // 读取并显示用户头像
        head_photo = (ImageView) findViewById(R.id.default_head);
        imageLoader = ImageLoader.getInstance();
        imageLoader.loadBitmap(BaseIndexActivity.this, sp.getPhotoUrl(), head_photo, R.drawable.portrait);

        // 通用功能的绑定和点击设置
        // 查看个人信息
        LinearLayout my_performance_row = (LinearLayout) findViewById(R.id.personal_info);
        my_performance_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BaseIndexActivity.this, BasePersonDetailActivity.class);
                startActivity(intent);
            }
        });

        //积分兑换
        LinearLayout score_exchange = (LinearLayout) findViewById(R.id.score_exchange);
        score_exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BaseIndexActivity.this, ScoreExchangeActivity.class);
                startActivity(intent);
            }
        });

        // 非通用功能的设置
        // 根据用户角色（MAJOR/MINOR/""）决定显示哪些功能
        role = sp.getRole();
        switch(role){
            case "MAJOR":
                // MAJOR可以设置切分
                LinearLayout score_divide = (LinearLayout) findViewById(R.id.score_divide);
                score_divide.setVisibility(View.VISIBLE);
                score_divide.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(BaseIndexActivity.this, SecondDivideActivity.class);
                        startActivity(intent);
                    }
                });
                break;
            case "MINOR":
                // MINOR啥特权都没有
                break;
            default:
                // 普通客户经理可以积分赠送
                LinearLayout score_transfer = (LinearLayout) findViewById(R.id.score_transfer);
                score_transfer.setVisibility(View.VISIBLE);
                score_transfer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(BaseIndexActivity.this, ScoreTransferActivity.class);
                        startActivity(intent);
                    }
                });
        }
    }

    // 头像的加载
    @Override
    protected void onResume() {
        super.onResume();
        imageLoader.loadBitmap(BaseIndexActivity.this, sp.getPhotoUrl(), head_photo, R.drawable.portrait);
    }

    // 首页的退出事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(BaseIndexActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
