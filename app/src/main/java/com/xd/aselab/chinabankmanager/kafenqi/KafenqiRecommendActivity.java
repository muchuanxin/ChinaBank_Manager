package com.xd.aselab.chinabankmanager.kafenqi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class KafenqiRecommendActivity extends AppCompatActivity {

    private RelativeLayout back;
    private Spinner choose_type;
    private LinearLayout recommend_type;
    private EditText recommend_name;
    private EditText recommend_tel;
    private EditText recommend_money;
    private EditText recommend_month;
    private ImageView submit;
    private ImageView calculator;

    private String choose_type_text;
    private String recommend_name_text;
    private String recommend_tel_text;
    private String recommend_money_text;
    private String recommend_month_text;

    private String jsonStr;
    private String flag;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kafenqi_recommend);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        initDatas();
        initViews();
        initEvents();
        getRecommendType();
        parseData();
        getPreviousActivity(flag);

    }

    void initDatas() {
        flag = getIntent().getStringExtra("flag");
    }

    void initViews() {
        back = (RelativeLayout) findViewById(R.id.act_kafenqi_recommend_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        choose_type = (Spinner) findViewById(R.id.choose_type);
        recommend_type = (LinearLayout) findViewById(R.id.act_kafenqi_recommend_type_row);
        recommend_name = (EditText) findViewById(R.id.act_kafenqi_recommend_name);
        recommend_tel = (EditText) findViewById(R.id.act_kafenqi_recommend_tel);
        recommend_money = (EditText) findViewById(R.id.act_kafenqi_recommend_money);
        recommend_month = (EditText) findViewById(R.id.act_kafenqi_recommend_month);
        submit = (ImageView) findViewById(R.id.act_kafenqi_recommend_submit);
        calculator = (ImageView) findViewById(R.id.act_kafenqi_recommend_calculator);

    }

    void initEvents() {

        calculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(KafenqiRecommendActivity.this, KafenqiRecommendCalculator.class);
                startActivity(intent);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(recommend_name);
                hideKeyboard(recommend_tel);
                hideKeyboard(recommend_money);
                hideKeyboard(recommend_month);

                recommend_name_text = recommend_name.getText().toString().trim();
                recommend_tel_text = recommend_tel.getText().toString().trim();
                recommend_money_text = recommend_money.getText().toString().trim();
                recommend_month_text = recommend_month.getText().toString().trim();

//                if(choose_type_text == null || choose_type_text.equals("")) {
//                    if("KafenqiNonCarActivity".equals(flag)) {
//                        Toast.makeText(KafenqiRecommendActivity.this, "请选择业务类型！", Toast.LENGTH_SHORT).show();
//                    }
//                }
                if(recommend_name_text.equals("")) {
                    Toast.makeText(KafenqiRecommendActivity.this, "请输入申请人姓名！", Toast.LENGTH_SHORT).show();
                }
                else if(recommend_tel_text.equals("")) {
                    Toast.makeText(KafenqiRecommendActivity.this, "请输入申请人联系方式！", Toast.LENGTH_SHORT).show();
                }
                else if(recommend_money_text.equals("")) {
                    Toast.makeText(KafenqiRecommendActivity.this, "请输入分期总金额！", Toast.LENGTH_SHORT).show();
                }
                else if(recommend_month_text.equals("")) {
                    Toast.makeText(KafenqiRecommendActivity.this, "请输入分期数！", Toast.LENGTH_SHORT).show();
                }

                else {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            String jsonStr;
                            SharePreferenceUtil spu = new SharePreferenceUtil(KafenqiRecommendActivity.this, "user");
                            if("KafenqiCustomerActivity".equals(flag)) {
                                PostParameter[] parameters = new PostParameter[5];
                                parameters[0] = new PostParameter("account", spu.getAccount());
                                parameters[1] = new PostParameter("applicant", recommend_name_text);
                                parameters[2] = new PostParameter("telephone", recommend_tel_text);
                                parameters[3] = new PostParameter("money", recommend_money_text);
                                parameters[4] = new PostParameter("installment_num", recommend_month_text);
                                jsonStr = ConnectUtil.httpRequest(ConnectUtil.RecommendYouke, parameters, ConnectUtil.POST);
                                Message msg = new Message();
                                if(jsonStr.equals("") || jsonStr == null) {
                                    msg.what = 1;
                                    msg.obj = "提交失败";
                                } else {
                                    msg.what = 0;
                                    msg.obj = jsonStr;
                                }
                                handler.sendMessage(msg);

                            } else if("KafenqiNonCarActivity".equals(flag)) {
                                PostParameter[] parameters = new PostParameter[6];
                                parameters[0] = new PostParameter("account", spu.getAccount());
                                parameters[1] = new PostParameter("product_type", choose_type_text);
                                parameters[2] = new PostParameter("applicant", recommend_name_text);
                                parameters[3] = new PostParameter("telephone", recommend_tel_text);
                                parameters[4] = new PostParameter("money", recommend_money_text);
                                parameters[5] = new PostParameter("installment_num", recommend_month_text);
                                jsonStr = ConnectUtil.httpRequest(ConnectUtil.RecommendNotCar, parameters, ConnectUtil.POST);
                                Message msg = new Message();
                                if(jsonStr.equals("") || jsonStr == null) {
                                    msg.what = 1;
                                    msg.obj = "提交失败";
                                } else {
                                    msg.what = 0;
                                    msg.obj = jsonStr;
                                }
                                handler.sendMessage(msg);
                            }
                        }
                    }.start();
                }
            }
        });
    }

    void parseData() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                jsonStr = (String) msg.obj;

                switch (msg.what) {
                    case 0:
                        try {
                            JSONObject object = new JSONObject(jsonStr);
                            if(object.getString("status").equals("true")) {
                                String number = object.getString("number");
                                String score = object.getString("score");
                                String exchange_score = object.getString("exchange_score");
                                String this_score = object.getString("this_score");

                                AlertDialog.Builder builder = new AlertDialog.Builder(KafenqiRecommendActivity.this);
                                final View dialog_content = LayoutInflater.from(KafenqiRecommendActivity.this).inflate(R.layout.kafenqi_recommend_toast, null);
                                TextView recommend_num = (TextView) dialog_content.findViewById(R.id.number);
                                TextView recommend_score = (TextView) dialog_content.findViewById(R.id.score);
                                TextView recommend_exchangeScore = (TextView) dialog_content.findViewById(R.id.exchange_score);
                                TextView recommend_thisScore = (TextView) dialog_content.findViewById(R.id.this_score);

                                recommend_num.setText("您本月累计推荐" + number + "笔业务");
                                recommend_score.setText("累计获得" + score + "积分");
                                recommend_exchangeScore.setText("已兑换" + exchange_score + "积分");
                                recommend_thisScore.setText("本次预计获得" + this_score + "积分");

                                builder.setTitle(object.getString("message"));
                                builder.setView(dialog_content);
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                                builder.show();

                            } else if(object.getString("status").equals("false")) {
                                Toast.makeText(KafenqiRecommendActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;

                    case 1:
                        break;

                    default:
                        break;
                }
            }
        };
    }

    void getRecommendType() {

        // 类型下拉框的处理
        final String[] spinnerItems = {"车位分期", "家装分期", "旅游分期"};
        final String[] productTypes = {"parking", "decoration", "tour"};
        //简单的string数组适配器：样式res，数组
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, spinnerItems);
        //下拉的样式res
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        choose_type.setAdapter(spinnerAdapter);
        choose_type.setSelection(0);
        //选择监听
        choose_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //parent就是父控件spinner
            //view就是spinner内填充的textview,id=@android:id/text1
            //position是值所在数组的位置
            //id是值所在行的位置，一般来说与positin一致
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                choose_type_text = productTypes[pos];
//                LogUtil.i("onItemSelected : parent.id=" + parent.getId() +
//                        ",isSpinnerId=" + (parent.getId() == R.id.spinner_1) +
//                        ",viewid=" + view.getId() + ",pos=" + pos + ",id=" + id);
//                ToastUtil.showShort(instance, "选择了[" + spinnerItems[pos] + "]");
                //设置spinner内的填充文字居中
                //((TextView)view).setGravity(Gravity.CENTER);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }

    //用于返回界面隐藏软键盘
    private void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    //用于判断上一页面是优客还是非车业务
    private void getPreviousActivity(String flag) {
        if("KafenqiNonCarActivity".equals(flag)) {
            recommend_type.setVisibility(View.VISIBLE);
        } else if("KafenqiCustomerActivity".equals(flag)) {
            recommend_type.setVisibility(View.GONE);
            choose_type_text = null;
        } else {
            Log.e("KafenqiRecommend", flag);
        }
    }
}
