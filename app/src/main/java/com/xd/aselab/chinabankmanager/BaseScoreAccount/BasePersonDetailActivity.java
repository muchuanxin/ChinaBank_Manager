package com.xd.aselab.chinabankmanager.BaseScoreAccount;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.activity.Login;
import com.xd.aselab.chinabankmanager.util.CircleImageView;
import com.xd.aselab.chinabankmanager.util.ImageLoader;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import cn.jpush.android.api.JPushInterface;

public class BasePersonDetailActivity extends AppCompatActivity {

    private TextView my_account_text;
    private TextView my_mobile_text;
    private TextView user_name;
    private CircleImageView image;
    private TextView logout;
    private ImageLoader imageLoader;
    private SharePreferenceUtil sp;

    @Override
    protected void onResume() {
        super.onResume();
        // 变量的初始化
        // 用户姓名、账号、电话信息
        sp = new SharePreferenceUtil(BasePersonDetailActivity.this, "user");
        user_name = (TextView) findViewById(R.id.user_name);
        my_account_text = (TextView) findViewById(R.id.my_account_text);
        my_mobile_text = (TextView) findViewById(R.id.my_mobile_text);

        user_name.setText(sp.getName());
        my_account_text.setText(sp.getAccount());
        my_mobile_text.setText(sp.getTel());

        // 准备更新头像
        image = (CircleImageView) findViewById(R.id.image);
        imageLoader = ImageLoader.getInstance();


        //---------------------------------------
        Log.d("Daijie",sp.getPhotoUrl()+"");

        imageLoader.loadBitmap(BasePersonDetailActivity.this, sp.getPhotoUrl(), image, R.drawable.default_head);

        //点击头像更换照片
        //暂时不换，以后要用再开放by歹杰
//        image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(BasePersonDetailActivity.this, ChangePhotoActivity.class);
//                intent.putExtra("jump","personal_info");
//                startActivityForResult(intent,3);
//
//            }
//        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_score_person_detail);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // 设置退出登录按钮的点击事件
        logout = (TextView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //退出登录清空Alias和Tags
                sp.setIsLogin(false);
                JPushInterface.deleteAlias(BasePersonDetailActivity.this, 0);
                JPushInterface.cleanTags(BasePersonDetailActivity.this, 1);

                // 跳回到login界面
                Intent intent = new Intent(BasePersonDetailActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        // 设置左上角返回按钮点击事件
        ImageView back_btn = (ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 修改个人信息按钮点击事件
        // 去掉修改信息按钮，想修改去正常的页面，这里的积分账号不给改by歹杰
//        personalDetail_update = (TextView) findViewById(R.id.personDetail_update);
//        personalDetail_update.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                }
//            }
//        });

    }
}
