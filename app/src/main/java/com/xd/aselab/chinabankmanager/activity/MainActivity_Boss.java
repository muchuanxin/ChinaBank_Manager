package com.xd.aselab.chinabankmanager.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.fragment.ImageCycleView;
import com.xd.aselab.chinabankmanager.my.PersonalInfo;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.ImageLoader;
import com.xd.aselab.chinabankmanager.util.PostParameter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity_Boss extends AppCompatActivity {

    private ImageCycleView imageCycleView;
    private ImageCycleView.ImageCycleViewListener mAdCycleViewListener;
    private TextView toast_i;
    private ImageView personal_info;
    private LinearLayout my_management;
    private LinearLayout hot_recommend;
    private LinearLayout china_bank_network;
    private LinearLayout china_bank_benefit;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_boss);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        MobclickAgent.onResume(this);

        imageCycleView = (ImageCycleView) findViewById(R.id.act_main_boss_ImageCycleView);

        mAdCycleViewListener = new ImageCycleView.ImageCycleViewListener() {
            @Override
            public void onImageClick(int position, View imageView) {
            }

            @Override
            public void displayImage(String imageURL, ImageView imageView) {
                if ("no_picture".equals(imageURL)){
                    imageView.setImageResource(R.drawable.placeholder2);
                }
                else {
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.loadBitmap(MainActivity_Boss.this, imageURL, imageView, 0);
                }
            }
        };

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0 :
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(MainActivity_Boss.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONArray jsonArray = json.getJSONArray("list");
                                    ArrayList<String> imageUrls = new ArrayList<>();
                                    for (int i=0; i<jsonArray.length(); i++){
                                        JSONObject temp = (JSONObject) jsonArray.get(i);
                                        String picture_url = temp.getString("picture_url");
                                        imageUrls.add(picture_url);
                                    }
                                    if (imageUrls.size()>0)
                                        imageCycleView.setImageResources(imageUrls, mAdCycleViewListener);
                                    else {
                                        imageUrls.add("no_picture");
                                        imageCycleView.setImageResources(imageUrls, mAdCycleViewListener);
                                    }
                                } else {
                                    Log.e("MyContact_Activity", MainActivity_Boss.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(MainActivity_Boss.this, MainActivity_Boss.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("MyContact_Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("MyContact_Activity", MainActivity_Boss.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };



        personal_info = (ImageView) findViewById(R.id.act_main_boss_personal_info);
        personal_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity_Boss.this, PersonalInfo.class);
                startActivityForResult(intent, 187);
            }
        });

        my_management = (LinearLayout) findViewById(R.id.act_main_boss_my_management);
        my_management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity_Boss.this, MyManagement.class);
                startActivity(intent);
            }
        });

        hot_recommend = (LinearLayout) findViewById(R.id.act_main_boss_hot_recommend);
        hot_recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity_Boss.this, MainHotRecommenderActivity.class);
                startActivity(intent);
            }
        });

        china_bank_network = (LinearLayout) findViewById(R.id.act_main_boss_china_bank_network);
        china_bank_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity_Boss.this, CBNetwork.class);
                startActivity(intent);
            }
        });

        china_bank_benefit = (LinearLayout) findViewById(R.id.act_main_boss_china_bank_benefit);
        china_bank_benefit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity_Boss.this, ChinaBankBenefit.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        new Thread(){
            @Override
            public void run() {
                super.run();
                PostParameter[] params = new PostParameter[1];
                params[0] = new PostParameter("rolling", "picture");
                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetRollingPicture, params, ConnectUtil.POST);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();

        imageCycleView.startImageCycle();
    }

    @Override
    public void onPause() {
        super.onPause();
        imageCycleView.pushImageCycle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==178 && "check_out".equals(data.getStringExtra("action")) ){
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MobclickAgent.onPause(this);
    }
}
