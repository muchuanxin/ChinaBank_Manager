package com.xd.aselab.chinabankmanager.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.zxing.WriterException;
import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

public class MyQRcode extends AppCompatActivity {

    private RelativeLayout back;
    private ImageView qrcode;
    private SharePreferenceUtil sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qrcode);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.act_my_qrcode_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sp=new SharePreferenceUtil(MyQRcode.this,"user");

        qrcode = (ImageView) findViewById(R.id.act_my_qrcode_qrcode_img);
        createCode(sp.getAccount());

    }

    private void createCode(String code){

        Bitmap logoBitmap = BitmapFactory.decodeResource(MyQRcode.this.getResources(), R.mipmap.manager);
        Bitmap qrCodeBitmap = QRCodeEncoder.syncEncodeQRCode(code,
                BGAQRCodeUtil.dp2px(MyQRcode.this, 150), Color.BLACK, Color.WHITE, logoBitmap);

        qrcode.setImageBitmap(qrCodeBitmap);

    }
}
