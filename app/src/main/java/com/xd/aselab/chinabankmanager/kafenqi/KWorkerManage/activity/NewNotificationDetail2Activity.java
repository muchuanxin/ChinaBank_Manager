package com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.chat.ui.ChatActivity;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.adapter.EvaluationItemAdapter;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.model.EvaluationItemVO;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.Constants;
import com.xd.aselab.chinabankmanager.util.NoScrollListView;
import com.xd.aselab.chinabankmanager.util.PostParameter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewNotificationDetail2Activity extends AppCompatActivity {

    //从推荐分期列表点进来，查看分期详情，申请人的住房、收入等信息从后台获取
    private String id = "";
    private int position = 0;
    private RelativeLayout back;
    private RelativeLayout rl_show_information_text;
    private ImageView arrow;
    private boolean isShow = false;
    private LinearLayout ll_detail_information;
    private Button bt_refuse, bt_confirm;
    private Handler handler;
    private ImageView iv_communacate_worker;
    private ImageView iv_call_worker_tel;
    private ImageView iv_call_application_tel;
    private String myExtras;
    private String worker_tel_str, application_tel_str;
    private String worker_name_str, worker_head_str, worker_account_str;
    private TextView worker_name, worker_status, applicate_time, worker_tel, worker_company, applicatin_name, application_tel, applicate_money, applicate_num, buy_commodity;
    private TextView self_score, system_score;
    private NoScrollListView lv_detail_information;
    private List<EvaluationItemVO> list = new ArrayList<>();
    private EvaluationItemAdapter adapter;
    private TextView applicate_house, applicate_diya, applicate_yueshouru, applicate_yuechangzhai, applicate_congye, muqianzhuzhishijian, applicate_hunyin,
            applicate_hujiqingkuang, applicate_wenhua, applicate_nianling, applicate_shixin;

    private TextView tv_refuse, tv_affirm, tv_mark;
    private LinearLayout ll_choose;
    private ImageView set_info;
    private String jsonstr;
    private View.OnClickListener listener;
    private String serial_num;
    private double money;
    private String confirm;
    private Intent intent;
    private TextView serial_num_text, get_money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_notification_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        id = getIntent().getStringExtra("id");
        position = getIntent().getIntExtra("position", 0);
        initView();
        parseData();
        initEvents();

    }

    void initView() {
        back = (RelativeLayout) findViewById(R.id.act_recommence_detail_back_btn);
//        rl_show_information_text = (RelativeLayout) findViewById(R.id.show_information);
        arrow = (ImageView) findViewById(R.id.iv_right_arrow);
//        ll_detail_information = (LinearLayout)findViewById(R.id.detail_application_information);
        bt_confirm = (Button) findViewById(R.id.new_notification_affirm);
        set_info = (ImageView) findViewById(R.id.set_info);
        bt_refuse = (Button) findViewById(R.id.new_notification_refuse);
        iv_communacate_worker = (ImageView) findViewById(R.id.iv_worker_chat);
        iv_call_worker_tel = (ImageView) findViewById(R.id.iv_call_worker_tel);
        iv_call_application_tel = (ImageView) findViewById(R.id.iv_call_application_tel);
        worker_name = (TextView) findViewById(R.id.detail_worker_name);
        worker_status = (TextView) findViewById(R.id.detail_worker_status);
        serial_num_text = (TextView) findViewById(R.id.serial_num);
        get_money = (TextView) findViewById(R.id.get_money);
        applicate_time = (TextView) findViewById(R.id.detail_applicate_time);
        worker_tel = (TextView) findViewById(R.id.detail_worker_tel);
        worker_company = (TextView) findViewById(R.id.detail_worker_company);
        applicatin_name = (TextView) findViewById(R.id.detail_applicatin_name);
        application_tel = (TextView) findViewById(R.id.detail_application_tel);
        applicate_money = (TextView) findViewById(R.id.detail_applicate_money);
        applicate_num = (TextView) findViewById(R.id.detail_applicate_num);
//        buy_commodity = (TextView)findViewById(R.id.detail_buy_commodity);
//        self_score = (TextView)findViewById(R.id.detail_self_score);
//        system_score = (TextView)findViewById(R.id.detail_system_score);
        lv_detail_information = (NoScrollListView) findViewById(R.id.detail_lv_2);
        tv_refuse = (TextView) findViewById(R.id.tv_refuse);
        tv_affirm = (TextView) findViewById(R.id.tv_affirm);
        tv_mark = (TextView) findViewById(R.id.tv_mark);
        ll_choose = (LinearLayout) findViewById(R.id.bottom_choose);
        /*applicate_house = (TextView)findViewById(R.id.detail_applicate_house);
        applicate_diya = (TextView)findViewById(R.id.detail_applicate_diya);
        applicate_yueshouru = (TextView)findViewById(R.id.detail_applicate_yueshouru);
        applicate_yuechangzhai = (TextView)findViewById(R.id.detail_applicate_yuechangzhai);
        applicate_congye = (TextView)findViewById(R.id.detail_applicate_congye);
        muqianzhuzhishijian = (TextView)findViewById(R.id.detail_applicate_muqianzhuzhishijian);
        applicate_hunyin = (TextView)findViewById(R.id.detail_applicate_hunyin);
        applicate_hujiqingkuang = (TextView)findViewById(R.id.detail_applicate_hujiqingkuang);
        applicate_wenhua = (TextView)findViewById(R.id.detail_applicate_wenhua);
        applicate_nianling = (TextView)findViewById(R.id.detail_applicate_nianling);
        applicate_shixin = (TextView)findViewById(R.id.detail_applicate_shixin);*/

    }

    void parseData() {

        new Thread() {
            @Override
            public void run() {
                super.run();
                final PostParameter[] params = new PostParameter[1];
                //id
                params[0] = new PostParameter("id", id);
                //卡分期——推广员推荐分期详情
                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetInstallmentWorkerRecommendDetail, params, ConnectUtil.POST);
                Log.d("Dorise  url", reCode);

                Message msg = new Message();
                msg.what = 0;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        String reCode0 = (String) msg.obj;
                        if (reCode0 != null) {
                            Log.e("www", "22222-recode:" + reCode0);
                            JSONObject json = null;
                            try {
                                json = new JSONObject(reCode0);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    String message = json.getString("message");
                                    Toast.makeText(NewNotificationDetail2Activity.this, "" + message, Toast.LENGTH_LONG).show();
                                    finish();
                                } else if ("true".equals(status)) {
                                    String worker_status_str = json.getString("worker_status");
                                    if ("正在申请解约".equals(worker_status) || "已解约".equals(worker_status)) {
                                        worker_status.setVisibility(View.VISIBLE);
                                        worker_status.setText(" (" + worker_status_str + ")");
                                    } else {
                                        worker_status.setVisibility(View.GONE);
                                    }

                                    worker_name_str = json.getString("worker_name");
                                    worker_head_str = json.getString("worker_head");
                                    worker_account_str = json.getString("worker_account");

                                    worker_name.setText(json.getString("worker_name"));
                                    id = json.getString("id");
                                    applicate_time.setText(json.getString("time"));
                                    worker_tel_str = json.getString("worker_telephone");
                                    worker_tel.setText("联系电话：" + json.getString("worker_telephone"));
                                    worker_company.setText("工作单位：" + json.getString("worker_company"));
                                    applicatin_name.setText("申请人：" + json.getString("applicant"));
                                    application_tel_str = json.getString("applicant_telephone");
                                    application_tel.setText("联系电话：" + application_tel_str);
                                    Double mm= Double.valueOf(json.getString("money"));
                                    applicate_money.setText("分期总金额(万元)：" + mm);
                                    applicate_num.setText("分期数：" + json.getString("installment_num"));
                                    serial_num = json.getString("serial_num");


                                    money = Double.parseDouble(json.getString("money"));
//                                    buy_commodity.setText("购买汽车品牌："+json.getString("car_type"));
//                                    self_score.setText("评分："+json.getString("evaluation"));
                                    //返回什么
//                                    system_score.setText(json.getString("car_type"));
                                    /*applicate_house.setText("住房权利："+json.getString("zhufang"));
                                    applicate_diya.setText("有无抵押："+json.getString("diya"));
                                    applicate_yueshouru.setText("个人月收入："+json.getString("yueshouru"));
                                    applicate_yuechangzhai.setText("月偿债："+json.getString("yuechangzhai"));
                                    applicate_congye.setText("从业情况："+json.getString("congye"));
                                    muqianzhuzhishijian.setText("在目前住址时间："+json.getString("zhuzhishijian"));
                                    applicate_hunyin.setText("婚姻状况："+json.getString("hunyin"));
                                    applicate_hujiqingkuang.setText("户籍情况："+json.getString("huji"));
                                    applicate_wenhua.setText("文化程度："+json.getString("wenhua"));
                                    applicate_nianling.setText("年龄："+json.getString("age"));
                                    applicate_shixin.setText("失信情况："+json.getString("shixin"));*/
                                    /*JSONArray jsonArray = json.getJSONArray("evaluation_detail");
                                    if (jsonArray.length() > 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject temp = (JSONObject) jsonArray.get(i);
                                            Log.d("www", "temp:" + jsonArray.get(i));
                                            EvaluationItemVO evaluationItemVO = new EvaluationItemVO();
                                            evaluationItemVO.setTitle(temp.getString("title"));
                                            Log.d("www", "" + temp.getString("title"));
                                            evaluationItemVO.setContent(temp.getString("content"));
                                            evaluationItemVO.setGoal(temp.getString("goal"));
                                            list.add(evaluationItemVO);
                                        }
                                        adapter = new EvaluationItemAdapter(list, NewNotificationDetail2Activity.this);
                                        lv_detail_information.setAdapter(adapter);
                                    } else {
                                        finish();
                                    }*/


                                    //获取当前confirm状态
                                    confirm = json.getString("confirm");


                                    if (confirm.equals("SUCCESS")) {
                                        set_info.setVisibility(View.GONE);
                                        serial_num_text.setText("流水号：" + serial_num);
                                        get_money.setText("放款金额：" + money);
                                    } else {
                                        set_info.setVisibility(View.VISIBLE);
                                        serial_num_text.setVisibility(View.GONE);
                                        get_money.setVisibility(View.GONE);
                                    }


                                    set_info.setOnClickListener(listener);


                                    Log.e("www", "----" + confirm);
                                    switch (confirm) {
                                        case "SUCCESS":
                                            ll_choose.setVisibility(View.GONE);
                                            tv_mark.setVisibility(View.VISIBLE);
                                            tv_mark.setText("业务通过");
                                            tv_mark.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                            break;
                                        case "YES":
                                            ll_choose.setVisibility(View.GONE);
                                            tv_mark.setVisibility(View.VISIBLE);
                                            tv_mark.setText("已确认");
                                            tv_mark.setTextColor(getResources().getColor(R.color.blue_text));
                                            break;
                                        case "NO":
                                            ll_choose.setVisibility(View.GONE);
                                            tv_mark.setVisibility(View.VISIBLE);
                                            tv_mark.setText("已拒绝");
                                            tv_mark.setTextColor(getResources().getColor(R.color.gray_text_most_1));
                                            break;
                                        case "CHECK":
                                            ll_choose.setVisibility(View.VISIBLE);
                                            tv_mark.setVisibility(View.GONE);
                                            break;
                                        case "FAIL":
                                            ll_choose.setVisibility(View.GONE);
                                            tv_mark.setVisibility(View.VISIBLE);
                                            tv_mark.setText("业务不通过");
                                            tv_mark.setTextColor(getResources().getColor(R.color.gray_text_most_1));
                                            break;

                                    }
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(NewNotificationDetail2Activity.this, "网络连接异常，请检查网络设置", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1:
                        String reCode = (String) msg.obj;
                        if (reCode != null) {
                            try {
                                JSONObject json = new JSONObject(reCode);
                                String message = json.getString("message");
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(NewNotificationDetail2Activity.this, "" + message, Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    bt_refuse.setVisibility(View.GONE);
                                    bt_confirm.setVisibility(View.VISIBLE);
                                    bt_confirm.setBackgroundResource(R.drawable.grey_corner);
                                    bt_confirm.setText("已确认");
                                    bt_confirm.setClickable(false);
                                    Toast.makeText(NewNotificationDetail2Activity.this, "" + message, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent();
                                    intent.putExtra("action", "confirm");
                                    intent.putExtra("position", position);
                                    setResult(1000, intent);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case 2:
                        String reCode2 = (String) msg.obj;
                        if (reCode2 != null) {
                            try {
                                JSONObject json = new JSONObject(reCode2);
                                String message = json.getString("message");
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(NewNotificationDetail2Activity.this, "" + message, Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    bt_refuse.setVisibility(View.GONE);
                                    bt_confirm.setVisibility(View.VISIBLE);
                                    bt_confirm.setBackgroundResource(R.drawable.grey_corner);
                                    bt_confirm.setText("已拒绝");
                                    bt_confirm.setClickable(false);
                                    Toast.makeText(NewNotificationDetail2Activity.this, "" + message, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent();
                                    intent.putExtra("action", "refuse");
                                    intent.putExtra("position", position);
                                    setResult(1000, intent);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        break;

                    case 4:
                        Toast.makeText(NewNotificationDetail2Activity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        try {
                            JSONObject obj = new JSONObject(msg.obj.toString());
                            Toast.makeText(NewNotificationDetail2Activity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            if (obj.get("status").equals("true")) {


                                applicate_money.setText("分期总金额(万元)：" + money);

                                Intent intent = new Intent();
                                intent.putExtra("action", "add_text");
                                intent.putExtra("position", position);
                                intent.putExtra("serial_num", serial_num);
                                intent.putExtra("money", money);
                                Log.d("Dorise 返回时候Monwy", money + "");
                                setResult(1000, intent);
                                confirm = "SUCCESS";

                                set_info.setVisibility(View.GONE);
                                serial_num_text.setVisibility(View.VISIBLE);
                                get_money.setVisibility(View.VISIBLE);
                                serial_num_text.setText("流水号：" + serial_num);
                                get_money.setText("放款金额：" + money);


                                ll_choose.setVisibility(View.GONE);
                                tv_mark.setVisibility(View.VISIBLE);
                                tv_mark.setText("业务通过");
                                tv_mark.setTextColor(getResources().getColor(R.color.colorPrimaryDark));


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;

                }
            }
        };
    }

    void initEvents() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
/*
        rl_show_information_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShow) {
                    arrow.setImageResource(R.drawable.arrow_right);
                    lv_detail_information.setVisibility(View.GONE);
                    isShow = false;
                } else {
                    arrow.setImageResource(R.drawable.arrow_down_hui);
                    lv_detail_information.setVisibility(View.VISIBLE);
                    isShow = true;
                }
            }
        });*/

        iv_call_worker_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(NewNotificationDetail2Activity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NewNotificationDetail2Activity.this, new String[]{Manifest.permission.CALL_PHONE},
                            Constants.ActivityCompatRequestPermissionsCode);
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + worker_tel_str));
                startActivity(intent);
            }
        });

    /*    set_info.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewNotificationDetail2Activity.this);
                final View view = getLayoutInflater().inflate(R.layout.manager_input_info, null);
                builder.setTitle("提交成功");
                builder.setView(view);

                //这个要不要控制输入非空  业务编号是啥
                builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread() {
                            Message msg = handler.obtainMessage();

                            @Override
                            public void run() {
                                super.run();
                                String serial_num = ((EditText) view.findViewById(R.id.serial_number)).getText().toString().trim();
                                String money = ((EditText) view.findViewById(R.id.money)).getText().toString().trim();
                                view.findViewById(R.id.serial_number);
                                PostParameter post[] = new PostParameter[3];
                                post[0] = new PostParameter("id", id);
                                Log.d("Dorise", id);
                                post[1] = new PostParameter("serial_num", serial_num);
                                Log.d("Dorise", serial_num);
                                post[2] = new PostParameter("money", money);
                                Log.d("Dorise", money);
                                //添加备注
                                jsonstr = ConnectUtil.httpRequest(ConnectUtil.AddInstallmentRecommendRemark, post, "POST");
                                if ("" == jsonstr || jsonstr == null) {
                                    msg.what = 4;
                                    msg.obj = "提交失败";
                                } else {
                                    msg.what = 5;
                                    msg.obj = jsonstr;
                                }
                                handler.sendMessage(msg);
                            }
                        }.start();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });*/

        iv_call_application_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + application_tel_str));
                // startActivity(intent);
                if (Build.VERSION.SDK_INT >= 23) {
                    //判断有没有拨打电话权限
                    if (PermissionChecker.checkSelfPermission(NewNotificationDetail2Activity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        //请求拨打电话权限
                        ActivityCompat.requestPermissions(NewNotificationDetail2Activity.this, new String[]{Manifest.permission.CALL_PHONE}, 10015);
                    } else {
                        startActivity(intent);
                    }

                } else {
                    startActivity(intent);
                }
            }
        });

        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewNotificationDetail2Activity.this);
                final View mview = getLayoutInflater().inflate(R.layout.manager_input_info, null);
               /* if (confirm.equals("SUCCESS")) {
                    mview.findViewById(R.id.serial_number).setVisibility(View.GONE);
                    TextView text = (TextView) mview.findViewById(R.id.flow);
                    text.setText("流水号：" + serial_num);
                    Log.d("Dorise流水号", serial_num);
                    mview.findViewById(R.id.money).setVisibility(View.GONE);
                    TextView text1 = (TextView) mview.findViewById(R.id.get_money);
                    text1.setText("放款金额(万元)：" + money);
                    Log.d("Dorise放款金额22222", money + "");
                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setView(mview);
                    builder.setTitle("备注信息");
                    builder.show();
                } else*/
                {

                    builder.setPositiveButton("提交", null);
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setView(mview);
                    builder.setTitle("添加备注");

                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            EditText temp1 = ((EditText) mview.findViewById(R.id.serial_number));
                            serial_num = (temp1.getText().toString().trim());
                            EditText temp2 = ((EditText) mview.findViewById(R.id.money));
                            money = Double.parseDouble(temp2.getText().toString().trim());

                            if ("".equals(serial_num)) {
                                Toast.makeText(NewNotificationDetail2Activity.this, "请输入流水号", Toast.LENGTH_SHORT).show();
                                Log.d("Dorise流水号", serial_num + "----------");
                                return;
                            } else if ("".equals(money)) {
                                Toast.makeText(NewNotificationDetail2Activity.this, "请输入放款金额", Toast.LENGTH_SHORT).show();
                                Log.d("Dorise放款金额", money + "----------");
                                return;
                            } else {
                                Log.d("Dorise  elseli面", money + "----------");
                                new Thread() {
                                    Message msg = handler.obtainMessage();

                                    @Override
                                    public void run() {
                                        super.run();

                                        PostParameter post[] = new PostParameter[4];
                                        post[0] = new PostParameter("id", id);
                                        Log.d("Dorise", id);
                                        post[1] = new PostParameter("serial_num", serial_num + "");
                                        Log.d("Dorise", serial_num + "");
                                        post[2] = new PostParameter("money", money + "");
                                        Log.d("Dorise", money + "");
                                        post[3] = new PostParameter("worker_account", worker_account_str + "");
                                        jsonstr = ConnectUtil.httpRequest(ConnectUtil.AddInstallmentRecommendRemark, post, "POST");
                                        if ("" == jsonstr || jsonstr == null) {
                                            msg.what = 4;
                                            msg.obj = "提交失败";
                                        } else {
                                            msg.what = 5;


                                            msg.obj = jsonstr;
                                        }
                                        handler.sendMessage(msg);
                                        dialog.dismiss();
                                    }
                                }.start();
                            }


                        }
                    });
                }

            }
        };


        iv_communacate_worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("www", "----------onClick");
                Intent intent = new Intent(NewNotificationDetail2Activity.this, ChatActivity.class);
                /*receiver = get_intent.getStringExtra("receiver");
                receiver_name = get_intent.getStringExtra("receiver_name");
                receiver_head = get_intent.getStringExtra("receiver_head");*/
                intent.putExtra("receiver", worker_account_str);
                intent.putExtra("receiver_name", worker_name_str);
                intent.putExtra("receiver_head", worker_head_str);
                startActivity(intent);
            }
        });

        tv_affirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });

        tv_refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refuse();
            }
        });


    }

    void confirm() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                final PostParameter[] params = new PostParameter[2];
                //id
                params[0] = new PostParameter("id", id);
                params[1] = new PostParameter("confirm", "YES");
                //卡分期——确认/拒绝推荐
                String reCode = ConnectUtil.httpRequest(ConnectUtil.ConfirmInstallmentRecommend, params, ConnectUtil.POST);
                Message msg = new Message();
                msg.what = 1;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();
    }

    void refuse() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                final PostParameter[] params = new PostParameter[2];
                //id
                params[0] = new PostParameter("id", id);
                params[1] = new PostParameter("confirm", "NO");
                String reCode = ConnectUtil.httpRequest(ConnectUtil.ConfirmInstallmentRecommend, params, ConnectUtil.POST);
                Log.e("reCode", "" + reCode);
                Message msg = new Message();
                msg.what = 2;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();
    }

}

