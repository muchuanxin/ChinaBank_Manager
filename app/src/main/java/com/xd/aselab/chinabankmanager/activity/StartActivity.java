package com.xd.aselab.chinabankmanager.activity;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.activity.NewNotificationDetailActivity;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

public class StartActivity extends AppCompatActivity {

    public static boolean isForeground = false;
    private String extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        JPushInterface.setDebugMode(true);
        JPushInterface.init(StartActivity.this);
        CustomPushNotificationBuilder builder2 = new CustomPushNotificationBuilder(StartActivity.this,R.layout.customer_notitfication_layout,R.id.icon, R.id.title, R.id.text);
        builder2.layoutIconDrawable = R.mipmap.manager;
        builder2.developerArg0 = "developerArg2";
        JPushInterface.setPushNotificationBuilder(2, builder2);

        final Intent intent = new Intent();
        //intent.putExtra("extras",extras);
        //intent.setClass(this, MainActivity.class);
        intent.setClass(this, Login.class);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        };
        timer.schedule(task, 1000 * 2);

        //registerMessageReceiver();  // used for receive msg

        extras = getIntent().getStringExtra("KEY_EXTRAS");
        Log.e("my_extras",""+extras);
        if(extras!=null){
            Intent intent1 = new Intent();
            intent1.setClass(StartActivity.this, NewNotificationDetailActivity.class);
            intent1.putExtra("extras",""+extras);
            startActivity(intent1);
        }

        //清除极光推送的Alias和Tags
        SharePreferenceUtil spu = new SharePreferenceUtil(StartActivity.this, "user");
        if (!spu.getisLogin()){
            JPushInterface.deleteAlias(StartActivity.this, 0);
            JPushInterface.cleanTags(StartActivity.this, 1);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(StartActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = true;
        JPushInterface.onPause(StartActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);*/
        //unregisterReceiver(receiver);
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
    }*/

    /*public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    Log.e("www","MessageReceiver=========="+extras);
                    if (extras!=null) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                   // setCostomMsg(showMsg.toString());
                }else{
                    Log.e("www","MessageReceiver-!!!!!!!!!!!!!"+extras);
                }
            } catch (Exception e){
            }
        }
    }*/

}




