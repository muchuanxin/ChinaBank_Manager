package com.xd.aselab.chinabankmanager.kafenqi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.google.zxing.WriterException;
import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.activity.MyManageBase;
import com.xd.aselab.chinabankmanager.kafenqi.KOnlineCommunicate.KOnlineCommuActivity;
import com.xd.aselab.chinabankmanager.kafenqi.KPublishAnnouncement.SelectPromoterActivity;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.activity.KMyContactsActivity;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.activity.KWorkerManageActivity;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

public class KafenqiActivity extends AppCompatActivity {

    private RelativeLayout back;
    private ImageView iv_my_contact;
    private LinearLayout my_performance;
    private LinearLayout worker_management;
    private LinearLayout online_communation;
    private LinearLayout publish_annoucement;
    private ImageView img_qrCode,img_big_code;
    Bitmap qrCodeBitmap = null;
    private PopupWindow pop;
    private SharePreferenceUtil sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kafenqi);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        initViews();
        initDatas();
        initEvents();
    }

    private void initViews(){
        back = (RelativeLayout) findViewById(R.id.act_kafenqi_back_btn);
        iv_my_contact = (ImageView)findViewById(R.id.act_iv_kafenqi_contact);
        my_performance = (LinearLayout) findViewById(R.id.act_kafenqi_my_perf);
        worker_management = (LinearLayout) findViewById(R.id.act_kafenqi_worker_management);
        online_communation = (LinearLayout) findViewById(R.id.act_kafenqi_online_communication);
        publish_annoucement = (LinearLayout)findViewById(R.id.act_kafenqi_announcement);
        img_qrCode = (ImageView) findViewById(R.id.kafenqi_qrcode);
    }

    private void initDatas(){
        sp=new SharePreferenceUtil(KafenqiActivity.this,"user");
        createCode(sp.getAccount()+"_2");

    }

    private void createCode(String code){

        Bitmap logoBitmap = BitmapFactory.decodeResource(KafenqiActivity.this.getResources(), R.mipmap.manager);
        qrCodeBitmap = QRCodeEncoder.syncEncodeQRCode(code,
                BGAQRCodeUtil.dp2px(KafenqiActivity.this, 150), Color.BLACK, Color.WHITE, logoBitmap);

        img_qrCode.setImageBitmap(qrCodeBitmap);

    }

    private void initEvents(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_my_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(KafenqiActivity.this,KMyContactsActivity.class);
                startActivity(intent);
            }
        });
        my_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("type","basic");
                intent.setClass(KafenqiActivity.this,KMyPreformenceActivity.class);
                startActivity(intent);
            }
        });
        worker_management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(KafenqiActivity.this,KWorkerManageActivity.class);
                startActivity(intent);

            }
        });
        online_communation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(KafenqiActivity.this,KOnlineCommuActivity.class);
                startActivity(intent);
            }
        });
        publish_annoucement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
               // intent.setClass(KafenqiActivity.this,KContactChooseActivity.class);
               // intent.setClass(KafenqiActivity.this,RecommendListActivity.class);
                intent.setClass(KafenqiActivity.this,SelectPromoterActivity.class);
                startActivity(intent);
            }
        });
        img_qrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpWindow();
            }
        });
    }

    private void showPopUpWindow(){
        //设置contentView
        View contentView = LayoutInflater.from(KafenqiActivity.this).inflate(R.layout.big_code, null);
        pop = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        pop.setContentView(contentView);
        pop.setTouchable(true); // 设置popupwindow可点击
        pop.setOutsideTouchable(true);  // 设置popupwindow外部可点击
        pop.setFocusable(true); //获取焦点
        //pop.update();
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                pop.dismiss();
            }
        });
        img_big_code = (ImageView)contentView.findViewById(R.id.big_code);
        img_big_code.setImageBitmap(qrCodeBitmap);

        //显示PopupWindow
        View rootview = LayoutInflater.from(KafenqiActivity.this).inflate(R.layout.activity_kafenqi, null);
        pop.showAtLocation(rootview, Gravity.CENTER, 0, 0);

        LinearLayout background = (LinearLayout) contentView.findViewById(R.id.big_code_layout);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });
    }

}
