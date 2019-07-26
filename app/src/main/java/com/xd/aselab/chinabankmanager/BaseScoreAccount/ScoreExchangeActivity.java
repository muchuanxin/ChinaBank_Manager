package com.xd.aselab.chinabankmanager.BaseScoreAccount;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class ScoreExchangeActivity extends AppCompatActivity {

    private TextView tv_all_score;
    private TextView tv_unexchanged_score;
    private TextView tv_exchanged_score;
    private SharePreferenceUtil sp;
    private ImageView iv_back_btn;
    private Button b_exchange;

    private int unexchanged_score = 0;

    // 接口数据处理，onResume负责请求数据
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                // 接口连接失败，提示
                case 0:
                    Log.d("Dorise", "当前未兑换积分00000");
                    Toast.makeText(ScoreExchangeActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                // 接口连接成功，开始处理
                case 1:
                    Log.d("Dorise", "当前未兑换积分111110");
                    try {
                        JSONObject obj = new JSONObject(msg.obj.toString());
                        // 是否成功获取数据
                        if ("true".equals(obj.getString("status"))) {
                            // 取出并显示积分数据
                            tv_all_score.setText(obj.getString("total_score"));
                            tv_exchanged_score.setText(obj.getString("exchange_score"));
                            tv_unexchanged_score.setText(obj.getString("not_exchange_score"));
                            // 以int保存未兑换积分，给后边用
                            unexchanged_score = obj.getInt("not_exchange_score");
                        } else {
                            Toast.makeText(ScoreExchangeActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_score_exchange);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        sp = new SharePreferenceUtil(ScoreExchangeActivity.this, "user");

        // 用户总积分、已/未兑换积分的控件获取
        tv_all_score = (TextView) findViewById(R.id.all_score_text);
        tv_unexchanged_score = (TextView) findViewById(R.id.unexchanged_score_text);
        tv_exchanged_score = (TextView) findViewById(R.id.exchanged_score_text);

        // 返回按钮点击事件设置
        iv_back_btn = (ImageView) findViewById(R.id.back_btn);
        iv_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 前往积分商城按钮设置
        b_exchange = (Button) findViewById(R.id.exchange);
        b_exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreExchangeActivity.this, ScoreShopActivity.class);
                intent.putExtra("not_exchange_score", unexchanged_score);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // 页面活动时发起数据请求线程
        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = handler.obtainMessage();
                PostParameter post[] = new PostParameter[1];
                String account = sp.getAccount();
                post[0] = new PostParameter("account", account.substring(0, account.length()-3));
                String jsonStr = ConnectUtil.httpRequest(ConnectUtil.GetNotExchangedScore, post, "POST");
                // 请求数据非法判断
                if (("").equals(jsonStr) || jsonStr == null) {
                    msg.what = 0;
                    msg.obj = "获取未兑换积分失败";
                } else {
                    msg.what = 1;
                    msg.obj = jsonStr;
                }
                // 转到handler处理数据
                handler.sendMessage(msg);
            }
        }.start();
    }
}
