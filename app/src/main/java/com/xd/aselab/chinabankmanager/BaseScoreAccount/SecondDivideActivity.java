package com.xd.aselab.chinabankmanager.BaseScoreAccount;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONObject;

public class SecondDivideActivity extends AppCompatActivity {


    private SharePreferenceUtil spu;
    private ImageView iv_back;
    private EditText et_my_ratio;
    private EditText et_result_ratio;
    private Button bt_submit;

    private String new_ratio;

    private Handler handler_submit;
    private Handler handler_init;
    private ProgressDialog progDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_divide);

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
        et_my_ratio = (EditText) findViewById(R.id.my_ratio);
        et_result_ratio = (EditText) findViewById(R.id.result_ratio);
        spu = new SharePreferenceUtil(SecondDivideActivity.this, "user");

        // 提交信息用handler初始化，后边提交新的切分比例后处理返回数据
        handler_submit = new Handler() {
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                // loading图标消失
                dismissProgressDialog();
                // 开始处理返回数据
                try {
                    String reCode = (String) msg.obj;
                    if (reCode != null) {
                        Log.e("ScoreTransfer：reCode", reCode);
                        JSONObject json = new JSONObject(reCode);
                        Toast.makeText(SecondDivideActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
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
                 new_ratio = et_my_ratio.getText().toString().trim();
                // 如果用户输入的账号为空
                // 则重获焦点，提示用户
                if("".equals(new_ratio)){
                    Toast.makeText(SecondDivideActivity.this, "请您输入二级行管理者的切分比例", Toast.LENGTH_SHORT).show();
                    et_my_ratio.setFocusable(true);
                    et_my_ratio.requestFocus();
                    et_my_ratio.setFocusableInTouchMode(true);
                    et_my_ratio.requestFocusFromTouch();
                }else if (Integer.parseInt(new_ratio) > 30) {
                    // 二级行切分比例不能大于30
                    // 重获焦点并清空，提示用户
                    Toast.makeText(SecondDivideActivity.this, "切分比例不能大于30%，请重新输入", Toast.LENGTH_SHORT).show();
                    et_my_ratio.setText("");
                    et_my_ratio.setFocusable(true);
                    et_my_ratio.requestFocus();
                    et_my_ratio.setFocusableInTouchMode(true);
                    et_my_ratio.requestFocusFromTouch();
                }else{
                    // 更新客户经理的切分比例
                    et_result_ratio.setText(new String(100-Integer.parseInt(new_ratio)+""));
                    // 开始发送数据，显示loading图标
                    showProgressDialog();
                    new Thread(){
                        @Override
                        public void run(){
                            super.run();
                            PostParameter[] params = new PostParameter[2];
                            params[0] = new PostParameter("erji_num",spu.getBranchLevel2());
                            params[1] = new PostParameter("boss_rate",new_ratio);
                            // 发送数据，获取返回字符串
                            String reCode = ConnectUtil.httpRequest(ConnectUtil.ChangeDivideScoreRate, params, ConnectUtil.POST);
                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = reCode;
                            handler_submit.sendMessage(msg);
                        }
                    }.start();
                }
            }
        });
    }

    // 页面展示时的重载
    @Override
    protected void onResume() {
        super.onResume();

        // 切分比例初始化handler的初始化，在页面打开时显示
        handler_init = new Handler() {
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                // loading图标消失
                dismissProgressDialog();
                // 开始处理返回数据
                try {
                    String reCode = (String) msg.obj;
                    JSONObject json = new JSONObject(reCode);
                    String status = json.getString("status");
                    if ("false".equals(status)) {
                        Toast.makeText(SecondDivideActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                    } else if ("true".equals(status)) {
                        // 数据获取成功，把切分比例显示出来
                        et_my_ratio.setText(json.getString("boss_rate"));
                        et_result_ratio.setText(new String(100-Integer.parseInt(json.getString("boss_rate"))+""));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // 请求当前积分切分信息
        new Thread(){
            @Override
            public void run(){
                super.run();
                PostParameter[] params = new PostParameter[1];
                params[0] = new PostParameter("erji_num",spu.getBranchLevel2());
                // 发送数据，获取返回字符串
                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetDivideScoreRate, params, ConnectUtil.POST);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = reCode;
                handler_init.sendMessage(msg);
            }
        }.start();
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
    private void dismissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }
}
