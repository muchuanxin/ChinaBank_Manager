package com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.Constants;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

public class NewNotificationDetailActivity extends AppCompatActivity {

    //已废弃

    private RelativeLayout rl_show_information_text;
    private ImageView arrow;
    private boolean isShow = false;
    private LinearLayout ll_detail_information;
    private Button bt_refuse,bt_confirm;
    private Handler handler;
    private ImageView iv_communacate_worker;
    private ImageView iv_call_worker_tel;
    private ImageView iv_call_application_tel;
    private String myExtras;
    private String worker_tel_str,application_tel_str;

    private TextView worker_name,applicate_time,worker_tel,worker_company,applicatin_name,application_tel,applicate_money,applicate_num,buy_commodity;
    private TextView self_score,system_score;
    private TextView applicate_house,applicate_diya,applicate_yueshouru,applicate_yuechangzhai,applicate_congye,muqianzhuzhishijian,applicate_hunyin,
    applicate_hujiqingkuang,applicate_wenhua,applicate_nianling,applicate_shixin;

    private String id="";
    public static boolean isForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_notification_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        registerMessageReceiver();  // used for receive msg
        initView();
        initDatas();
        initEvents();
        parseData();

    }

    void initView(){
        rl_show_information_text = (RelativeLayout) findViewById(R.id.show_information);
        arrow = (ImageView)findViewById(R.id.iv_right_arrow);
        ll_detail_information = (LinearLayout)findViewById(R.id.detail_application_information);
        bt_confirm = (Button)findViewById(R.id.new_notification_affirm);
        bt_refuse = (Button)findViewById(R.id.new_notification_refuse);
        iv_communacate_worker = (ImageView)findViewById(R.id.iv_worker_chat);
        iv_call_worker_tel = (ImageView)findViewById(R.id.iv_call_worker_tel);
        iv_call_application_tel = (ImageView)findViewById(R.id.iv_call_application_tel);
        worker_name = (TextView)findViewById(R.id.detail_worker_name);
        applicate_time = (TextView)findViewById(R.id.detail_applicate_time);
        worker_tel = (TextView)findViewById(R.id.detail_worker_tel);
        worker_company = (TextView)findViewById(R.id.detail_worker_company);
        applicatin_name = (TextView)findViewById(R.id.detail_applicatin_name);
        application_tel = (TextView)findViewById(R.id.detail_application_tel);
        applicate_money = (TextView)findViewById(R.id.detail_applicate_money);
        applicate_num = (TextView)findViewById(R.id.detail_applicate_num);
        buy_commodity = (TextView)findViewById(R.id.detail_buy_commodity);
        self_score = (TextView)findViewById(R.id.detail_self_score);
        system_score = (TextView)findViewById(R.id.detail_system_score);
        applicate_house = (TextView)findViewById(R.id.detail_applicate_house);
        applicate_diya = (TextView)findViewById(R.id.detail_applicate_diya);
        applicate_yueshouru = (TextView)findViewById(R.id.detail_applicate_yueshouru);
        applicate_yuechangzhai = (TextView)findViewById(R.id.detail_applicate_yuechangzhai);
        applicate_congye = (TextView)findViewById(R.id.detail_applicate_congye);
        muqianzhuzhishijian = (TextView)findViewById(R.id.detail_applicate_muqianzhuzhishijian);
        applicate_hunyin = (TextView)findViewById(R.id.detail_applicate_hunyin);
        applicate_hujiqingkuang = (TextView)findViewById(R.id.detail_applicate_hujiqingkuang);
        applicate_wenhua = (TextView)findViewById(R.id.detail_applicate_wenhua);
        applicate_nianling = (TextView)findViewById(R.id.detail_applicate_nianling);
        applicate_shixin = (TextView)findViewById(R.id.detail_applicate_shixin);

    }

    void initDatas(){

        SharePreferenceUtil spu = new SharePreferenceUtil(NewNotificationDetailActivity.this,"user");
        myExtras = spu.getExtras();
        if (myExtras!=null) {

            JSONObject json1 = null;
            try {
                json1 = new JSONObject(myExtras);
                String extra_str = json1.getString("extra");
                JSONObject json = new JSONObject(extra_str);
                worker_name.setText(json.getString("worker_name"));
                id = json.getString("id");
                applicate_time.setText(json.getString("recommend_time"));
                worker_tel_str = json.getString("worker_telephone");
                worker_tel.setText("联系电话："+json.getString("worker_telephone"));
                worker_company.setText("工作单位："+json.getString("worker_company"));
                applicatin_name.setText("申请人："+json.getString("applicant"));
                application_tel_str = json.getString("telephone");
                application_tel.setText("联系电话："+application_tel_str);
                applicate_money.setText("申请金额："+json.getString("money"));
                applicate_num.setText("分期数："+json.getString("installment_num"));
                buy_commodity.setText("购买商品："+json.getString("car_type"));
                self_score.setText("评分："+json.getString("evaluation"));
                //返回什么
                system_score.setText(json.getString("car_type"));
                applicate_house.setText("住房权利："+json.getString("zhufang"));
                applicate_diya.setText("有无抵押："+json.getString("diya"));
                applicate_yueshouru.setText("个人月收入："+json.getString("yueshouru"));
                applicate_yuechangzhai.setText("月偿债："+json.getString("yuechangzhai"));
                applicate_congye.setText("从业情况："+json.getString("congye"));
                muqianzhuzhishijian.setText("在目前住址时间："+json.getString("zhuzhishijian"));
                applicate_hunyin.setText("婚姻状况："+json.getString("hunyin"));
                applicate_hujiqingkuang.setText("户籍情况："+json.getString("huji"));
                applicate_wenhua.setText("文化程度："+json.getString("wenhua"));
                applicate_nianling.setText("年龄："+json.getString("age"));
                applicate_shixin.setText("失信情况："+json.getString("shixin"));

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        else{
            finish();
        }
    }

    void parseData(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        break;
                    case 1:
                        String reCode =(String) msg.obj;
                        if(reCode!=null){
                            try {
                                JSONObject json = new JSONObject(reCode);
                                String message = json.getString("message");
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(NewNotificationDetailActivity.this,""+message,Toast.LENGTH_SHORT).show();
                                }else if("true".equals(status)) {
                                    Toast.makeText(NewNotificationDetailActivity.this,""+message,Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case 2:
                        String reCode2 =(String) msg.obj;
                        if(reCode2!=null){
                            try {
                                JSONObject json = new JSONObject(reCode2);
                                String message = json.getString("message");
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(NewNotificationDetailActivity.this,""+message,Toast.LENGTH_SHORT).show();
                                }else if("true".equals(status)) {
                                    Toast.makeText(NewNotificationDetailActivity.this,""+message,Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
            }
        };
    }

    void initEvents(){

        rl_show_information_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShow){
                    arrow.setImageResource(R.drawable.arrow_right);
                    ll_detail_information.setVisibility(View.GONE);
                    isShow=false;
                }else {
                    arrow.setImageResource(R.drawable.arrow_down);
                    ll_detail_information.setVisibility(View.VISIBLE);
                    isShow=true;
                }
            }
        });

        iv_call_worker_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(NewNotificationDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NewNotificationDetailActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                            Constants.ActivityCompatRequestPermissionsCode);
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+worker_tel_str));
                startActivity(intent);
            }
        });

        iv_call_application_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(NewNotificationDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NewNotificationDetailActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                            Constants.ActivityCompatRequestPermissionsCode);
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+application_tel_str));
                startActivity(intent);
            }
        });

        iv_communacate_worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });

        bt_refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refuse();
            }
        });


    }

    void confirm(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                final PostParameter[] params = new PostParameter[2];
                //id
                params[0] = new PostParameter("id", id);
                params[1] = new PostParameter("confirm", "YES");
                String reCode = ConnectUtil.httpRequest(ConnectUtil.ConfirmInstallmentRecommend, params, ConnectUtil.POST);
                Message msg = new Message();
                msg.what = 1;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();
    }

    void refuse(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                final PostParameter[] params = new PostParameter[2];
                //id
                params[0] = new PostParameter("id", id);
                params[1] = new PostParameter("confirm", "NO");
                String reCode = ConnectUtil.httpRequest(ConnectUtil.ConfirmInstallmentRecommend, params, ConnectUtil.POST);
                Message msg = new Message();
                msg.what = 2;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    //for receive customer msg from jpush server
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
        filter.addAction(JPushInterface.ACTION_NOTIFICATION_OPENED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    myExtras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    Log.e("www","MessageReceiver.extras.New----"+myExtras);
                    if (myExtras!=null) {
                        showMsg.append(KEY_EXTRAS + " : " + myExtras + "\n");
                    }
                    // setCostomMsg(showMsg.toString());
                }else{
                    Log.e("www","MessageReceiver.extras!!!!!!!!!"+myExtras);
                }
            } catch (Exception e){
            }
        }
    }


}
