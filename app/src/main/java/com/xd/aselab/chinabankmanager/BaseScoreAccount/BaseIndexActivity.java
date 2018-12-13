package com.xd.aselab.chinabankmanager.BaseScoreAccount;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.util.ImageLoader;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

public class BaseIndexActivity extends AppCompatActivity {

    private ImageView head_photo;
    private ImageLoader imageLoader;
    private SharePreferenceUtil sp;
    private TextView user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_score_index);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        sp = new SharePreferenceUtil(BaseIndexActivity.this,"user");

        // 读取并显示用户名
        user_name= (TextView) findViewById(R.id.user_name);
        user_name.setText(sp.getName());
        Log.d("Dorise",sp.getName());
        Log.d("Dorise","=============");

        // 读取并显示用户头像
        head_photo = (ImageView)findViewById(R.id.default_head);
        imageLoader = ImageLoader.getInstance();
        imageLoader.loadBitmap(BaseIndexActivity.this,sp.getPhotoUrl(),head_photo,R.drawable.portrait);

        // 点击用户信息，跳转到信息详情页
        LinearLayout my_performance_row= (LinearLayout) findViewById(R.id.personal_info);
        my_performance_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(BaseIndexActivity.this, BasePersonDetailActivity.class);
                startActivity(intent);
            }
        });

        //
        LinearLayout score_exchange= (LinearLayout) findViewById(R.id.score_exchange);
        score_exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(BaseIndexActivity.this, ScoreExchangeActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout score_transfer= (LinearLayout) findViewById(R.id.score_transfer);
        score_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(BaseIndexActivity.this, ScoreTransferActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        imageLoader.loadBitmap(BaseIndexActivity.this,sp.getPhotoUrl(),head_photo,R.drawable.portrait);
    }
}
