package com.xd.aselab.chinabankmanager.BaseScoreAccount;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONObject;

import static com.xd.aselab.chinabankmanager.util.Constants.LOGIN_TO_MAIN_HOME;

public class ScoreTransferActivity extends AppCompatActivity {

    private SharePreferenceUtil spu;
    private ImageView iv_back;
    private EditText et_target_account;
    private EditText et_amount;
    private Button bt_submit;
    private ImageView iv_target_account_delete;
    private ImageView iv_amount_delete;

    private String target_account;
    private String amount;

    private Handler handler;
    private ProgressDialog progDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_score_transfer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // 返回按钮点击事件设置
        iv_back = (ImageView) findViewById(R.id.back_btn);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 积分赠予账号和数量编辑框的绑定
        et_target_account = (EditText) findViewById(R.id.target_account_text);
        et_amount = (EditText) findViewById(R.id.amount_text);
        spu = new SharePreferenceUtil(ScoreTransferActivity.this, "user");

        // 两个输入框的删除按钮绑定和事件定义
        iv_target_account_delete = (ImageView) findViewById(R.id.target_account_delete);
        iv_target_account_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                et_target_account.setText("");
            }
        });
        iv_amount_delete = (ImageView) findViewById(R.id.amount_delete);
        iv_amount_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                et_amount.setText("");
            }
        });

        // 对应两个输入框在状态变化时要控制删除按钮的显示和隐藏
        et_target_account.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    iv_target_account_delete.setVisibility(View.GONE);
                } else {
                    iv_target_account_delete.setVisibility(View.VISIBLE);
                }
            }
        });
        et_amount.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    iv_amount_delete.setVisibility(View.GONE);
                } else {
                    iv_amount_delete.setVisibility(View.VISIBLE);
                }
            }
        });

        // handler初始化，后边提交积分后处理返回数据要用
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                // loading图标消失
                dissmissProgressDialog();
                // 开始处理返回数据
                try {
                    String reCode = (String) msg.obj;
                    if (reCode != null) {
                        Log.e("ScoreTransfer：reCode", reCode);
                        JSONObject json = new JSONObject(reCode);
                        Toast.makeText(ScoreTransferActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // 提交按钮的绑定和点击
        bt_submit = (Button) findViewById(R.id.submit);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                target_account = et_target_account.getText().toString().trim();
                amount = et_amount.getText().toString();
                Log.e("Score_Info", spu.getAccount() + ' ' + target_account+' '+amount);
                // 如果用户输入的账号为空
                // 则重获焦点，提示用户
                if(target_account==null || "".equals(target_account)){
                    Toast.makeText(ScoreTransferActivity.this, "请您输入目标账号", Toast.LENGTH_SHORT).show();
                    et_target_account.setFocusable(true);
                    et_target_account.requestFocus();
                    et_target_account.setFocusableInTouchMode(true);
                    et_target_account.requestFocusFromTouch();
                }else if (Integer.parseInt(amount) <= 0) {
                    // 如果用户输入的积分数量非正
                    // 则重获焦点并清空，提示用户
                        Toast.makeText(ScoreTransferActivity.this, "积分数量不能为0，请重新输入", Toast.LENGTH_SHORT).show();
                        et_amount.setText("");
                        et_amount.setFocusable(true);
                        et_amount.requestFocus();
                        et_amount.setFocusableInTouchMode(true);
                        et_amount.requestFocusFromTouch();
                }else{
                    // 开始发送数据，显示loading图标
                    showProgressDialog();
                    new Thread(){
                        @Override
                        public void run(){
                            super.run();
                            PostParameter[] params = new PostParameter[3];
                            String account = spu.getAccount();
                            params[0] = new PostParameter("giver_account",account.substring(0,account.length()-3));
                            params[1] = new PostParameter("receiver_account",target_account);
                            params[2] = new PostParameter("score",amount);
                            // 发送数据，获取返回字符串
                            String reCode = ConnectUtil.httpRequest(ConnectUtil.GiveScore, params, ConnectUtil.POST);
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

    // 如果用户点了返回键，就会退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent intent = new Intent();
            setResult(LOGIN_TO_MAIN_HOME, intent);
            finish();
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
