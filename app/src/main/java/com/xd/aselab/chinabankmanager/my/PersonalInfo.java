package com.xd.aselab.chinabankmanager.my;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.activity.ChangePhotoActivity;
import com.xd.aselab.chinabankmanager.activity.ChangePsw;
import com.xd.aselab.chinabankmanager.activity.ChangeTel;
import com.xd.aselab.chinabankmanager.activity.MyContact;
import com.xd.aselab.chinabankmanager.activity.MyQRcode;
import com.xd.aselab.chinabankmanager.activity.SecureQuestionActivity;
import com.xd.aselab.chinabankmanager.util.CircleImageView;
import com.xd.aselab.chinabankmanager.util.Constants;
import com.xd.aselab.chinabankmanager.util.ImageLoader;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import cn.jpush.android.api.JPushInterface;

public class PersonalInfo extends AppCompatActivity {

    private RelativeLayout back;
    private TextView my_contact;
    private TextView update;
    private RelativeLayout change_psw;
    private RelativeLayout change_tel;
    private RelativeLayout QRcode;
    private TextView name_label;
    private TextView name;
    private TextView job_number_label;
    private TextView job_number;
    private TextView type_label;
    private TextView type;
    private TextView tel;
    private TextView landline_label;
    private TextView landline;
    private TextView QRcode_label;
    private Button check_out;
    private CircleImageView head_photo;
    private ImageLoader imageLoader;

    private boolean state = true;
    private SharePreferenceUtil spu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        spu = new SharePreferenceUtil(PersonalInfo.this, "user");
        imageLoader = ImageLoader.getInstance();

        back = (RelativeLayout) findViewById(R.id.act_personal_info_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        head_photo = (CircleImageView)findViewById(R.id.head);
        imageLoader.loadBitmap(PersonalInfo.this, spu.getPhotoUrl(), head_photo, R.drawable.portrait);
        head_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalInfo.this,ChangePhotoActivity.class);
                intent.putExtra("jump","personal_info");
                startActivity(intent);
            }
        });

        my_contact = (TextView) findViewById(R.id.act_personal_info_my_contact);
        my_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PersonalInfo.this, MyContact.class);
                startActivity(intent);
            }
        });

        name_label = (TextView) findViewById(R.id.act_personal_info_name_label);
        job_number_label = (TextView) findViewById(R.id.act_personal_info_job_num_label);
        type_label = (TextView) findViewById(R.id.act_personal_info_emp_type_label);
        landline_label = (TextView) findViewById(R.id.act_personal_info_landline_label);
        QRcode_label = (TextView) findViewById(R.id.act_personal_info_QRcode_label);

        change_psw = (RelativeLayout) findViewById(R.id.act_personal_info_psw);
        change_tel = (RelativeLayout) findViewById(R.id.act_personal_info_tel);
        QRcode = (RelativeLayout) findViewById(R.id.act_personal_info_QRcode);

        update = (TextView) findViewById(R.id.act_personal_info_update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state){
                    update.setText("  完成");
                    changeTextColor(state);
                    state = false;
                    change_psw.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            if ("123456".equals(spu.getPassword())){
                                intent.setClass(PersonalInfo.this, SecureQuestionActivity.class);
                            }
                            else {
                                intent.setClass(PersonalInfo.this, ChangePsw.class);
                            }
                            startActivity(intent);
                        }
                    });
                    change_tel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(PersonalInfo.this, ChangeTel.class);
                            startActivity(intent);
                        }
                    });
                    QRcode.setOnClickListener(null);
                }
                else {
                    update.setText("  修改");
                    changeTextColor(state);
                    state = true;
                    change_psw.setOnClickListener(null);
                    change_tel.setOnClickListener(null);
                    QRcode.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(PersonalInfo.this, MyQRcode.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });

        QRcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PersonalInfo.this, MyQRcode.class);
                startActivity(intent);
            }
        });
        check_out = (Button) findViewById(R.id.act_personal_info_check_out);
        check_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //退出登录清空Alias和Tags
                spu.setIsLogin(false);
                JPushInterface.deleteAlias(PersonalInfo.this, 0);
                JPushInterface.cleanTags(PersonalInfo.this, 1);
                Intent intent = new Intent();
//                intent.putExtra("action", "check_out");
                setResult(Constants.INFO_TO_MAIN, intent);
                finish();
            }
        });

        name = (TextView) findViewById(R.id.act_personal_info_name_txt);
        name.setText(spu.getName());
        job_number = (TextView) findViewById(R.id.act_personal_info_job_num_txt);
        job_number.setText(spu.getJobNumber());
        tel = (TextView) findViewById(R.id.act_personal_info_tel_txt);
        tel.setText(spu.getTel());
        landline = (TextView) findViewById(R.id.act_personal_info_landline_txt);
        landline.setText("".equals(spu.getLandlineNumber()) ? "无" : spu.getLandlineNumber() );
        type = (TextView) findViewById(R.id.act_personal_info_emp_type_txt);
        switch (spu.getType()){
            case "MANAGER" :
                type.setText("二级行管理者");
                my_contact.setVisibility(View.GONE);
                QRcode.setVisibility(View.GONE);
                break;
            case "BASIC" :
                type.setText("银行卡客户经理");
                my_contact.setVisibility(View.VISIBLE);
                break;
            default:
                type.setText("经理");
                break;
        }
    }

    private void changeTextColor(boolean state){
        if (state){
            int grey = getResources().getColor(R.color.grey);
            name_label.setTextColor(grey);
            name.setTextColor(grey);
            job_number_label.setTextColor(grey);
            job_number.setTextColor(grey);
            type_label.setTextColor(grey);
            type.setTextColor(grey);
            landline_label.setTextColor(grey);
            landline.setTextColor(grey);
            QRcode_label.setTextColor(grey);
        }
        else {
            int black = getResources().getColor(R.color.black);
            name_label.setTextColor(black);
            name.setTextColor(black);
            job_number_label.setTextColor(black);
            job_number.setTextColor(black);
            type_label.setTextColor(black);
            type.setTextColor(black);
            landline_label.setTextColor(black);
            landline.setTextColor(black);
            QRcode_label.setTextColor(black);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tel.setText(spu.getTel());
        imageLoader.loadBitmap(PersonalInfo.this, spu.getPhotoUrl(), head_photo, R.drawable.portrait);
    }
}
