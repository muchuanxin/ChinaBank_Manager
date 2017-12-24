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

public class ChangePsw extends AppCompatActivity {

    private RelativeLayout back;
    private EditText old_psw_txt;
    private EditText new_psw_txt;
    private EditText new_psw_again_txt;
    private Button submit;

    private String old_psw;
    private String new_psw;
    private String new_psw_again;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_psw);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.act_change_psw_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        old_psw_txt = (EditText) findViewById(R.id.act_change_psw_old_psw_txt);
        new_psw_txt = (EditText) findViewById(R.id.act_change_psw_new_psw_txt);
        new_psw_again_txt = (EditText) findViewById(R.id.act_change_psw_new_psw_again_txt);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0 :
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                Log.e("ChangePsw：reCode", ""+reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(ChangePsw.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    Toast.makeText(ChangePsw.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                    SharePreferenceUtil spu = new SharePreferenceUtil(ChangePsw.this, "user");
                                    spu.setPassword(new_psw);
                                    finish();
                                } else {
                                    Log.e("ChangePsw_Activity", ChangePsw.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(ChangePsw.this, ChangePsw.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("ChangePsw_Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("ChangePsw_Activity", ChangePsw.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

        submit = (Button) findViewById(R.id.act_change_psw_submit_btn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                old_psw = old_psw_txt.getText().toString().trim();
                new_psw = new_psw_txt.getText().toString().trim();
                new_psw_again = new_psw_again_txt.getText().toString().trim();
                if (old_psw==null || "".equals(old_psw) ){
                    Toast.makeText(ChangePsw.this, "请输入原密码", Toast.LENGTH_SHORT).show();
                }
                else if ( new_psw==null || "".equals(new_psw) || new_psw_again==null || "".equals(new_psw_again) ){
                    Toast.makeText(ChangePsw.this, "请输入新密码，并确认密码", Toast.LENGTH_SHORT).show();
                }
                else if (new_psw.length()<6){
                    Toast.makeText(ChangePsw.this, "新密码至少6位", Toast.LENGTH_SHORT).show();
                }
                else if ("123456".equals(new_psw)){
                    Toast.makeText(ChangePsw.this, "新密码不能为默认密码", Toast.LENGTH_SHORT).show();
                }
                else if (!new_psw.equals(new_psw_again)){
                    Toast.makeText(ChangePsw.this, "确认密码须与新密码一致", Toast.LENGTH_SHORT).show();
                }
                else if ( old_psw.equals(new_psw)){
                    Toast.makeText(ChangePsw.this, "新密码不能与原密码相同", Toast.LENGTH_SHORT).show();
                }
                else {
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            SharePreferenceUtil spu = new SharePreferenceUtil(ChangePsw.this, "user");
                            PostParameter[] params = new PostParameter[4];
                            params[0] = new PostParameter("account", spu.getAccount());
                            params[1] = new PostParameter("password", Encode.getEncode("MD5", old_psw));
                            params[2] = new PostParameter("newPassword", Encode.getEncode("MD5", new_psw));
                            params[3] = new PostParameter("cookie", spu.getCookie());
                            String reCode = ConnectUtil.httpRequest(ConnectUtil.CHANGE_PASSWORD, params, ConnectUtil.POST);
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
