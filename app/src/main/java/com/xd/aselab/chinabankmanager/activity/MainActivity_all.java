package com.xd.aselab.chinabankmanager.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.PermissionChecker;
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
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.activity.NewNotificationDetailActivity;
import com.xd.aselab.chinabankmanager.kafenqi.KafenqiActivity;
import com.xd.aselab.chinabankmanager.kafenqi.manager.MannagerKafenqiActivity;
import com.xd.aselab.chinabankmanager.marketingGuide.MarketingGuideNew;
import com.xd.aselab.chinabankmanager.my.BaseMyActivity;
import com.xd.aselab.chinabankmanager.my.ManagerMyActivity;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.ImageLoader;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;

public class MainActivity_all extends AppCompatActivity {

    private ImageCycleView imageCycleView;
    private ImageCycleView.ImageCycleViewListener mAdCycleViewListener;
    private TextView toast_i;
    private ImageView personal_info;
    private LinearLayout ll_card;
    private LinearLayout ll_kafenqi;
    private LinearLayout ll_gerenxiaodai;
    private LinearLayout hot_recommend;
    private LinearLayout china_bank_network;
    private LinearLayout china_bank_benefit;
    private SharePreferenceUtil spu;

    private Handler handler;
    private String extras;
    public static boolean isForeground = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_all);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }


        MobclickAgent.onResume(this);
        initViews();
        initDatas();
        initEvents();
        //registerMessageReceiver();  // used for receive msg

    }

    private void initViews(){
        toast_i = (TextView) findViewById(R.id.act_main_toast_i);
        personal_info = (ImageView) findViewById(R.id.act_main_personal_info);
        imageCycleView = (ImageCycleView) findViewById(R.id.act_main_ImageCycleView);
        ll_card = (LinearLayout)findViewById(R.id.act_main_card);
        ll_kafenqi = (LinearLayout)findViewById(R.id.act_main_kafenqi);
        ll_gerenxiaodai = (LinearLayout)findViewById(R.id.act_main_gerenxiaodai);
        hot_recommend = (LinearLayout)findViewById(R.id.act_main_hot_recommend);
        china_bank_network = (LinearLayout)findViewById(R.id.act_main_china_bank_network);
        china_bank_benefit = (LinearLayout)findViewById(R.id.act_main_china_bank_benefit);

    }

    private  void initDatas(){
        extras = getIntent().getStringExtra("extras");
        spu = new SharePreferenceUtil(MainActivity_all.this, "user");
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
                    imageLoader.loadBitmap(MainActivity_all.this, imageURL, imageView, 0);
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
                                    Toast.makeText(MainActivity_all.this, json.getString("message"), Toast.LENGTH_SHORT).show();
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
                                    android.util.Log.e("MyContact_Activity", MainActivity_all.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(MainActivity_all.this, MainActivity_all.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                android.util.Log.e("MyContact_Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        android.util.Log.e("MyContact_Activity", MainActivity_all.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

    }

    private void initEvents(){

        if(extras!=null){
            Intent intent = new Intent();
            intent.setClass(MainActivity_all.this, NewNotificationDetailActivity.class);
            intent.putExtra("extras",extras);
            startActivity(intent);
        }

        toast_i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spu.getisLogin()){
                    if(spu.getType().equals("BASIC")){
                        Toast.makeText(MainActivity_all.this, "银行卡客户经理，您好", Toast.LENGTH_SHORT).show();
                    }else
                        Toast.makeText(MainActivity_all.this, "二级行管理者，您好", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent();
                    intent.setClass(MainActivity_all.this, Login.class);
                    intent.putExtra("clickView", "toast_i");
                    startActivity(intent);
                }

            }
        });

        personal_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (spu.getisLogin()){
                    if(spu.getType().equals("BASIC")){
                        intent.setClass(MainActivity_all.this, BaseMyActivity.class);
                        startActivity(intent);
                        // startActivityForResult(intent, 187);
                    }else{
                        intent.setClass(MainActivity_all.this, ManagerMyActivity.class);
                        startActivity(intent);
                    }

                }else{
                    intent.setClass(MainActivity_all.this, Login.class);
                    intent.putExtra("clickView", "personalInfo");
                    startActivity(intent);
                }
                /*if(spu.getType().equals("BASIC")){
                    if (spu.getisLogin()){
                        intent.setClass(MainActivity_all.this, BaseMyActivity.class);
                        startActivity(intent);
                    }else{
                        intent.setClass(MainActivity_all.this, Login.class);
                        intent.putExtra("clickView", "baseMyActivity");
                        startActivity(intent);
                    }
                }else{
                    if (spu.getisLogin()){
                        intent.setClass(MainActivity_all.this, ManagerMyActivity.class);
                        startActivity(intent);
                    }else{
                        intent.setClass(MainActivity_all.this, Login.class);
                        intent.putExtra("clickView", "managerMyActivity");
                        startActivity(intent);
                    }
                }*/

            }
        });

        ll_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (spu.getisLogin()){
                    if(spu.getType().equals("BASIC")){
                        intent.setClass(MainActivity_all.this, MyManageBase.class);
                        startActivity(intent);
                        // startActivityForResult(intent, 187);
                    }else{
                        intent.setClass(MainActivity_all.this, MyManagement.class);
                        startActivity(intent);
                    }

                }else{
                    intent.setClass(MainActivity_all.this, Login.class);
                    intent.putExtra("clickView", "card");
                    startActivity(intent);
                }

            }
        });

        ll_kafenqi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (spu.getisLogin()){
                    if(spu.getType().equals("BASIC")){
                        intent.setClass(MainActivity_all.this, KafenqiActivity.class);
                        startActivity(intent);
                        // startActivityForResult(intent, 187);
                    }else{
                       // Toast.makeText(MainActivity_all.this,"分区经理卡分期管理界面",Toast.LENGTH_SHORT).show();
                        intent.setClass(MainActivity_all.this, MannagerKafenqiActivity.class);
                        startActivity(intent);
                    }
                }else{
                    intent.setClass(MainActivity_all.this, Login.class);
                    intent.putExtra("clickView", "kafenqi");
                    startActivity(intent);
                }
            }
        });
        ll_gerenxiaodai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity_all.this,"特惠商圈界面",Toast.LENGTH_SHORT).show();
                /*Intent intent = new Intent();
                if (spu.getisLogin()){
                    if(spu.getType().equals("BASIC")){
                        intent.setClass(MainActivity_all.this, GerenxiaodaiActivity.class);
                        startActivity(intent);
                        // startActivityForResult(intent, 187);
                    }else{
                        Toast.makeText(MainActivity_all.this,"分区经理卡个人消贷管理界面",Toast.LENGTH_SHORT).show();
//                        intent.setClass(MainActivity_all.this, MyManagement.class);
//                        startActivity(intent);
                    }
                }else{
                    intent.setClass(MainActivity_all.this, Login.class);
                    intent.putExtra("clickView", "gerenxiaodai");
                    startActivity(intent);
                }*/

            }
        });

        hot_recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(MainActivity_all.this,"营销导航界面",Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= 23) {

                    //判断有没有定位权限
                    if (PermissionChecker.checkSelfPermission(MainActivity_all.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            || PermissionChecker.checkSelfPermission(MainActivity_all.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        //请求定位权限
                        ActivityCompat.requestPermissions(MainActivity_all.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, 10012);
                    }
                    else {
                        Intent intent = new Intent();
                        intent.setClass(MainActivity_all.this, MarketingGuideNew.class);
                        startActivity(intent);
                    }
                }
                else {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity_all.this, MarketingGuideNew.class);
                    startActivity(intent);
                }
            }
        });

        china_bank_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity_all.this, CBNetwork.class);
                startActivity(intent);
            }
        });

        china_bank_benefit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity_all.this, ChinaBankBenefit.class);
                startActivity(intent);
            }
        });

        if (spu.getisLogin()){
            JPushInterface.setAlias(MainActivity_all.this,0,spu.getAccount());
        }

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
        isForeground = true;
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
       // LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        MobclickAgent.onPause(this);
    }

    /*//for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.xd.aselab.chinabankmanager.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    SharePreferenceUtil spu = new SharePreferenceUtil(MainActivity_all.this,"user");
                    spu.setExtras(extras);
                    Log.d("www","MessageReceiver.extras-Main---"+extras.replace("\\",""));
                    if (extras!=null) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                    // setCostomMsg(showMsg.toString());
                }else{
                    Log.d("www","MessageReceiver-!!!!!!!!!!!!!"+extras);
                }
            } catch (Exception e){
            }
        }
    }*/


}

