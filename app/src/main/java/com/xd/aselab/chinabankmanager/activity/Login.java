package com.xd.aselab.chinabankmanager.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.gerenxiaodai.GerenxiaodaiActivity;
import com.xd.aselab.chinabankmanager.kafenqi.KafenqiActivity;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.Encode;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONObject;

import java.util.Random;

import cn.jpush.android.api.JPushInterface;

import static com.xd.aselab.chinabankmanager.util.Constants.LOGIN_TO_MAIN_HOME;
import static com.xd.aselab.chinabankmanager.util.Constants.LOGIN_TO_MAIN_ME;

public class Login extends AppCompatActivity {

    private SharePreferenceUtil spu;
    private Button login;
    private EditText account_edit;
    private EditText psw_edit;
    private ImageView account_delete;
    private ImageView psw_delete;
    private TextView forget_psw;
    private ProgressDialog progDialog = null;

    private String account;
    private String psw;

    private String clickView = "";
    private Handler handler;
    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }


        clickView = getIntent().getStringExtra("clickView");

        login = (Button) findViewById(R.id.login);
        account_edit = (EditText) findViewById(R.id.act_login_account_edit);
        psw_edit = (EditText) findViewById(R.id.act_login_password_edit);
        account_delete = (ImageView) findViewById(R.id.act_login_account_delete) ;
        psw_delete = (ImageView) findViewById(R.id.act_login_password_delete) ;
        spu = new SharePreferenceUtil(Login.this, "user");
        if (!"".equals(spu.getAccount())){
            account_edit.setText(spu.getAccount());
            psw_edit.setText(spu.getPassword());
        }
        if (spu.getAccount().length()>0){
            account_delete.setVisibility(View.VISIBLE);
            psw_delete.setVisibility(View.VISIBLE);
        }

        account_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                account_edit.setText("");
            }
        });
        psw_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                psw_edit.setText("");
            }
        });


        account_edit.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    account_delete.setVisibility(View.GONE);
                } else {
                    account_delete.setVisibility(View.VISIBLE);
                }
            }
        });
        psw_edit.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    psw_delete.setVisibility(View.GONE);
                } else {
                    psw_delete.setVisibility(View.VISIBLE);
                }
            }
        });

        forget_psw = (TextView) findViewById(R.id.act_login_forget_psw);
        forget_psw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(Login.this, ForgetPsw1Activity.class));
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                dissmissProgressDialog();
                switch (msg.what){
                    case 0 :
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode != null) {
                                Log.e("Login_Activity：reCode", reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(Login.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    spu.setAccount(account);
                                    spu.setPassword(psw);
                                    spu.setPhotoUrl(json.getString("head_image"));
                                    spu.setJobNumber(json.getString("jobNumber"));
                                    spu.setTel(json.getString("teleNumber"));
                                    spu.setLandlineNumber(json.getString("landlineNumber"));
                                    spu.setName(json.getString("realName"));
                                    spu.setBranchLevel2(json.getString("branchLevel2"));
                                    spu.setBranchLevel4(json.getString("branchLevel4"));
                                    String job = json.getString("job");
                                    spu.setType(job);
                                    spu.setCookie(json.getString("cookie"));
                                    spu.setIsLogin(true);
                                    JPushInterface.setAlias(Login.this,new Random().nextInt(),account);
                                    /*if ("BASIC".equals(job)){
                                        startActivity(new Intent().setClass(Login.this, MainActivity_Base.class));
                                    }
                                    else if ("MANAGER".equals(job)) {
                                        startActivity(new Intent().setClass(Login.this, MainActivity_Boss.class));
                                    }*/
                                    if ("toast_i".equals(clickView)){
                                        finish();
                                    }
                                    else if ("personalInfo".equals(clickView)) {
                                        Log.e("www","personalInfo");
                                        /*
                                        if ("BASIC".equals(job)){
                                            Intent intent = new Intent();
                                            intent.setClass(Login.this, BaseMyActivity.class);
                                            finish();
                                            startActivityForResult(intent, 196);
                                        }
                                        else if ("MANAGER".equals(job)) {
                                            Intent intent = new Intent();
                                            intent.setClass(Login.this, ManagerMyActivity.class);
                                            finish();
                                            startActivityForResult(intent, 187);
                                        }*/
                                        Intent intent = new Intent();
                                        setResult(LOGIN_TO_MAIN_ME, intent);
                                        finish();
                                    }
                                    else if ("card".equals(clickView)) {
                                        if ("BASIC".equals(job)){
                                            Intent intent = new Intent();
                                            intent.setClass(Login.this, MyManagement.class);
                                            finish();
                                            startActivity(intent);
                                        }
                                        else if ("MANAGER".equals(job)) {
                                            //Toast.makeText(Login.this,"分区经理卡分期管理界面",Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent();
                                            intent.setClass(Login.this, MyManageBase.class);
                                            finish();
                                            startActivity(intent);
                                        }

                                    }
                                    else if ("kafenqi".equals(clickView)) {
                                        Intent intent = new Intent();
                                        intent.setClass(Login.this, KafenqiActivity.class);
                                        finish();
                                        startActivity(intent);
                                    }
                                    else if ("gerenxiaodai".equals(clickView)) {
                                        Intent intent = new Intent();
                                        intent.setClass(Login.this, GerenxiaodaiActivity.class);
                                        finish();
                                        startActivity(intent);
                                    }
                                } else {
                                    Log.e("Login_Activity", Login.this.getResources().getString(R.string.status_exception));
                                }
                            } else {
                                Log.e("Login_Activity", "reCode为空");
                                if (ConnectUtil.isNetworkAvailable(Login.this)){
                                    Toast.makeText(Login.this, Login.this.getResources().getString(R.string.server_repairing), Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(Login.this, Login.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("Login_Activity",Login.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = account_edit.getText().toString().trim();
                psw = psw_edit.getText().toString().trim();
                if (account==null || "".equals(account) || psw==null || "".equals(psw)){
                    Toast.makeText(Login.this, "请输入账号密码", Toast.LENGTH_SHORT).show();
                }
                else {
                    showProgressDialog();
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            PostParameter[] params = new PostParameter[2];
                            params[0] = new PostParameter("account",account);
                            params[1] = new PostParameter("password", Encode.getEncode("MD5", psw));
                            String reCode = ConnectUtil.httpRequest(ConnectUtil.MANAGER_LOGIN, params, ConnectUtil.POST);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (!"".equals(spu.getAccount())){
            account_edit.setText(spu.getAccount());
            psw_edit.setText(spu.getPassword());
        }
        if (spu.getAccount().length()>0){
            account_delete.setVisibility(View.VISIBLE);
            psw_delete.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent intent = new Intent();
            setResult(LOGIN_TO_MAIN_HOME, intent);
            finish();

            /*if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(Login.this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                System.exit(0);
            }
            return false;*/
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("请稍候...");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }
}
