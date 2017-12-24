package com.xd.aselab.chinabankmanager.activity;

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
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

public class MyManageBase extends AppCompatActivity {

    private RelativeLayout back;
    private LinearLayout my_performance;
    private LinearLayout shop_management;
    private ImageView img_qrCode,img_big_code;
    private Bitmap qrCodeBitmap = null;
    private PopupWindow pop;
    private SharePreferenceUtil sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_manage_base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.act_my_mana_base_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        my_performance = (LinearLayout) findViewById(R.id.act_my_mana_base_my_perf);
        my_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(MyManageBase.this, MyPerformance.class).putExtra("type",0));
            }
        });

        shop_management = (LinearLayout) findViewById(R.id.act_my_mana_base_shop_mana);
        shop_management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(MyManageBase.this, ShopManage.class));
            }
        });

        img_qrCode = (ImageView)findViewById(R.id.card_qrcode);
        img_qrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpWindow();
            }
        });

        sp=new SharePreferenceUtil(MyManageBase.this,"user");
        createCode(sp.getAccount()+"_1");

    }

    private void createCode(String code){

        Bitmap logoBitmap = BitmapFactory.decodeResource(MyManageBase.this.getResources(), R.mipmap.manager);
        qrCodeBitmap = QRCodeEncoder.syncEncodeQRCode(code,
                BGAQRCodeUtil.dp2px(MyManageBase.this, 150), Color.BLACK, Color.WHITE, logoBitmap);

        img_qrCode.setImageBitmap(qrCodeBitmap);

    }

    private void showPopUpWindow(){
        //设置contentView
        View contentView = LayoutInflater.from(MyManageBase.this).inflate(R.layout.big_code, null);
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
        View rootview = LayoutInflater.from(MyManageBase.this).inflate(R.layout.activity_my_manage_base, null);
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
