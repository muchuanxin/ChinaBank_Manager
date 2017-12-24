package com.xd.aselab.chinabankmanager.gerenxiaodai;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

public class GerenxiaodaiActivity extends AppCompatActivity {
    private RelativeLayout back;
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
        setContentView(R.layout.activity_gerenxiaodai);
        initViews();
        initDatas();
        initEvents();
    }
    private void initViews(){
        back = (RelativeLayout) findViewById(R.id.act_gerenxiaodai_back_btn);
        my_performance = (LinearLayout) findViewById(R.id.act_gerenxiaodai_my_perf);
        worker_management = (LinearLayout) findViewById(R.id.act_gerenxiaodai_worker_management);
        online_communation = (LinearLayout) findViewById(R.id.act_gerenxiaodai_online_communication);
        publish_annoucement = (LinearLayout)findViewById(R.id.act_gerenxiaodai_announcement);
        img_qrCode = (ImageView) findViewById(R.id.gerenxiaodai_qrcode);
    }

    private void initDatas(){
        sp=new SharePreferenceUtil(GerenxiaodaiActivity.this,"user");
        createCode(sp.getAccount()+"_3");

    }

    private void createCode(String code){

        Bitmap logoBitmap = BitmapFactory.decodeResource(GerenxiaodaiActivity.this.getResources(), R.mipmap.manager);
        qrCodeBitmap = QRCodeEncoder.syncEncodeQRCode(code,
                BGAQRCodeUtil.dp2px(GerenxiaodaiActivity.this, 150), Color.BLACK, Color.WHITE, logoBitmap);

        img_qrCode.setImageBitmap(qrCodeBitmap);

    }

    private void initEvents(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        my_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        worker_management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        View contentView = LayoutInflater.from(GerenxiaodaiActivity.this).inflate(R.layout.big_code, null);
        pop = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(contentView);
        pop.setTouchable(true); // 设置popupwindow可点击
        pop.setOutsideTouchable(true);  // 设置popupwindow外部可点击
        pop.setFocusable(true); //获取焦点
        //pop.update();
        backgroundAlpha(0.7f);
        pop.setBackgroundDrawable(new ColorDrawable(0));
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                backgroundAlpha(1f);
                pop.dismiss();
            }
        });
        img_big_code = (ImageView)contentView.findViewById(R.id.big_code);
        img_big_code.setImageBitmap(qrCodeBitmap);

        //显示PopupWindow
        View rootview = LayoutInflater.from(GerenxiaodaiActivity.this).inflate(R.layout.activity_gerenxiaodai, null);
        pop.showAtLocation(rootview, Gravity.CENTER, 0, 0);
    }

    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }
}
