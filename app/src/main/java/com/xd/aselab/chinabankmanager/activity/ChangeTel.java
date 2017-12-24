package com.xd.aselab.chinabankmanager.activity;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.Encode;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONObject;

public class ChangeTel extends AppCompatActivity {

    private RelativeLayout back;
    private EditText tel_edit;
    private Button save;

    private String tel;
    private SharePreferenceUtil spu;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_tel);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        spu = new SharePreferenceUtil(ChangeTel.this, "user");

        back = (RelativeLayout) findViewById(R.id.act_change_tel_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0 :
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                Log.e("ChangeTel：reCode", ""+reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(ChangeTel.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    Toast.makeText(ChangeTel.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                    spu.setTel(tel);
                                    finish();
                                } else {
                                    Log.e("ChangeTel_Activity", ChangeTel.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(ChangeTel.this, ChangeTel.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("ChangeTel_Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("ChangeTel_Activity", ChangeTel.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

        tel_edit = (EditText) findViewById(R.id.act_change_tel_tel);
        tel_edit.setText(spu.getTel());
        save = (Button) findViewById(R.id.act_change_tel_save_btn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tel = tel_edit.getText().toString().trim();
                //String telRegex = "[1][3578]\\d{9}";
                if (tel==null || "".equals(tel)){
                    Toast.makeText(ChangeTel.this, "请填写电话号码", Toast.LENGTH_SHORT).show();
                }
                else if (tel.equals(spu.getTel())){
                    Toast.makeText(ChangeTel.this, "请输入新的电话号码", Toast.LENGTH_SHORT).show();
                }
                else if (tel.length()!=11) {
                    Toast.makeText(ChangeTel.this, "电话号码不符合规格", Toast.LENGTH_SHORT).show();
                }
                /*else if (!tel.matches(telRegex)) {
                    Toast.makeText(ChangeTel.this, "电话号码不符合规格", Toast.LENGTH_SHORT).show();
                }*/
                else {
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            PostParameter[] params = new PostParameter[4];
                            params[0] = new PostParameter("account", spu.getAccount());
                            params[1] = new PostParameter("password", Encode.getEncode("MD5", spu.getPassword()));
                            params[2] = new PostParameter("teleNumber", tel);
                            params[3] = new PostParameter("cookie", spu.getCookie());
                            String reCode = ConnectUtil.httpRequest(ConnectUtil.CHANGE_TELEPHONE, params, ConnectUtil.POST);
                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = reCode;
                            handler.sendMessage(msg);
                        }
                    }.start();
                }
            }
        });
    }
}
