package com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.chat.ui.ChatActivity;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.adapter.EvaluationItemAdapter;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.model.EvaluationItemVO;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.NoScrollListView;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewNotificationDetailNewActivity extends AppCompatActivity {

    //从推荐分期列表点进来，查看分期详情，申请人的住房、收入等信息从后台获取
    private RelativeLayout back;
    private RelativeLayout rl_show_information_text;
    private ImageView arrow;
    private boolean isShow = false;
    private LinearLayout ll_detail_information;
    private NoScrollListView lv_detail_information;
    private Button bt_refuse,bt_confirm;
    private Handler handler;
    private ImageView iv_communacate_worker;
    private ImageView iv_call_worker_tel;
    private ImageView iv_call_application_tel;
    private String myExtras;
    private String worker_tel_str,application_tel_str;
    private String worker_name_str,worker_head_str,worker_account_str;
    private TextView worker_name,applicate_time,worker_tel,worker_company,applicatin_name,application_tel,applicate_money,applicate_num,buy_commodity;
    private TextView self_score,system_score;
    private TextView tv_refuse,tv_affirm;

    private String id="";
    private List<EvaluationItemVO> list = new ArrayList<>();
    private EvaluationItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_notification_detail_new);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initView();
        initDatas();
        getAllInfo();
        initEvents();
        parseData();

    }


    void initView(){

        back = (RelativeLayout) findViewById(R.id.act_new_noti_new_back_btn);
        rl_show_information_text = (RelativeLayout) findViewById(R.id.show_information_new);
        arrow = (ImageView)findViewById(R.id.iv_right_arrow_new);
        lv_detail_information = (NoScrollListView) findViewById(R.id.detail_lv);
        bt_confirm = (Button)findViewById(R.id.new_notification_affirm_new);
        bt_refuse = (Button)findViewById(R.id.new_notification_refuse_new);
        iv_communacate_worker = (ImageView)findViewById(R.id.iv_worker_chat_new);
        iv_call_worker_tel = (ImageView)findViewById(R.id.iv_call_worker_tel_new);
        iv_call_application_tel = (ImageView)findViewById(R.id.iv_call_application_tel_new);
        worker_name = (TextView)findViewById(R.id.detail_worker_name_new);
        applicate_time = (TextView)findViewById(R.id.detail_applicate_time_new);
        worker_tel = (TextView)findViewById(R.id.detail_worker_tel_new);
        worker_company = (TextView)findViewById(R.id.detail_worker_company_new);
        applicatin_name = (TextView)findViewById(R.id.detail_applicatin_name_new);
        application_tel = (TextView)findViewById(R.id.detail_application_tel_new);
        applicate_money = (TextView)findViewById(R.id.detail_applicate_money_new);
        applicate_num = (TextView)findViewById(R.id.detail_applicate_num_new);
        buy_commodity = (TextView)findViewById(R.id.detail_buy_commodity_new);
        self_score = (TextView)findViewById(R.id.detail_self_score_new);
        system_score = (TextView)findViewById(R.id.detail_system_score_new);
        tv_refuse = (TextView) findViewById(R.id.tv_refuse);
        tv_affirm = (TextView) findViewById(R.id.tv_affirm);

    }

    void initDatas(){

        SharePreferenceUtil spu = new SharePreferenceUtil(NewNotificationDetailNewActivity.this,"user");
        myExtras = spu.getExtras();
        Log.d("www","myExtras---"+myExtras);
        if (myExtras!=null) {

            JSONObject json1 = null;
            try {
                json1 = new JSONObject(myExtras);
                String extra_str = json1.getString("extra");
                JSONObject json = new JSONObject(extra_str);
               // worker_name.setText(json.getString("worker_name"));
                id = json.getString("id");
                /*applicate_time.setText(json.getString("recommend_time"));
                worker_tel_str = json.getString("worker_telephone");
                worker_tel.setText("联系电话："+json.getString("worker_telephone"));
                worker_company.setText("工作单位："+json.getString("worker_company"));
                applicatin_name.setText("申请人："+json.getString("applicant"));
                application_tel_str = json.getString("telephone");
                application_tel.setText("联系电话："+application_tel_str);
                applicate_money.setText("申请金额："+json.getString("money"));
                applicate_num.setText("分期数："+json.getString("installment_num"));
                buy_commodity.setText("购买商品："+json.getString("car_type"));
                self_score.setText("评分："+json.getString("evaluation"));
                //返回什么
                system_score.setText(json.getString("car_type"));
                JSONArray jsonArray = json.getJSONArray("evaluation_detail");
                if(jsonArray.length()>0){
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject temp = (JSONObject) jsonArray.get(i);
                        Log.d("www","temp:"+jsonArray.get(i));
                        EvaluationItemVO evaluationItemVO = new EvaluationItemVO();
                        evaluationItemVO.setTitle(temp.getString("title"));
                        Log.d("www",""+temp.getString("title"));
                        evaluationItemVO.setContent(temp.getString("content"));
                        evaluationItemVO.setGoal(temp.getString("goal"));
                        list.add(evaluationItemVO);
                    }
                    adapter = new EvaluationItemAdapter(list,NewNotificationDetailNewActivity.this);
                    lv_detail_information.setAdapter(adapter);
                }else {
                    finish();
                }*/

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        else{
            finish();
        }
    }

    void initEvents(){

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rl_show_information_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShow){
                    arrow.setImageResource(R.drawable.arrow_right);
                    lv_detail_information.setVisibility(View.GONE);
                    isShow=false;
                }else {
                    arrow.setImageResource(R.drawable.arrow_down_hui);
                    lv_detail_information.setVisibility(View.VISIBLE);
                    isShow=true;
                }
            }
        });

        iv_call_worker_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+worker_tel_str));
               // startActivity(intent);
                if (Build.VERSION.SDK_INT >= 23) {

                    //判断有没有拨打电话权限
                    if (PermissionChecker.checkSelfPermission(NewNotificationDetailNewActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                        //请求拨打电话权限
                        ActivityCompat.requestPermissions(NewNotificationDetailNewActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 10012);

                    } else {
                        startActivity(intent);
                    }

                } else {
                    startActivity(intent);
                }
            }
        });

        iv_call_application_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+application_tel_str));
               // startActivity(intent);
                if (Build.VERSION.SDK_INT >= 23) {
                    //判断有没有拨打电话权限
                    if (PermissionChecker.checkSelfPermission(NewNotificationDetailNewActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        //请求拨打电话权限
                        ActivityCompat.requestPermissions(NewNotificationDetailNewActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 10013);
                    } else {
                        startActivity(intent);
                    }

                } else {
                    startActivity(intent);
                }
            }
        });

        iv_communacate_worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("www","----------onClick");
                Intent intent = new Intent(NewNotificationDetailNewActivity.this, ChatActivity.class);
                /*receiver = get_intent.getStringExtra("receiver");
                receiver_name = get_intent.getStringExtra("receiver_name");
                receiver_head = get_intent.getStringExtra("receiver_head");*/
                intent.putExtra("receiver",worker_account_str);
                intent.putExtra("receiver_name",worker_name_str);
                intent.putExtra("receiver_head",worker_head_str);
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

    void getAllInfo(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                final PostParameter[] params = new PostParameter[1];
                //id
                params[0] = new PostParameter("id", id);
                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetInstallmentWorkerRecommendDetail, params, ConnectUtil.POST);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();
    }

    void confirm(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                final PostParameter[] params = new PostParameter[2];
                //id
                params[0] = new PostParameter("id", id);
                params[1] = new PostParameter("confirm", "YES");
                String reCode = ConnectUtil.httpRequest(ConnectUtil.ConfirmInstallmentRecommend, params, ConnectUtil.POST);
                Message msg = new Message();
                msg.what = 1;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();
    }

    void refuse(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                final PostParameter[] params = new PostParameter[2];
                //id
                params[0] = new PostParameter("id", id);
                params[1] = new PostParameter("confirm", "NO");
                String reCode = ConnectUtil.httpRequest(ConnectUtil.ConfirmInstallmentRecommend, params, ConnectUtil.POST);
                Message msg = new Message();
                msg.what = 2;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();
    }

    void parseData(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        String reCode0 =(String) msg.obj;
                        if(reCode0!=null){
                            try {
                                JSONObject json = new JSONObject(reCode0);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    String message = json.getString("message");
                                    Toast.makeText(NewNotificationDetailNewActivity.this,""+message,Toast.LENGTH_SHORT).show();
                                    finish();
                                }else if("true".equals(status)) {
                                    worker_name_str = json.getString("worker_name");
                                    worker_head_str = json.getString("worker_head");
                                    worker_account_str= json.getString("worker_account");
                                    worker_name.setText(json.getString("worker_name"));
                                    applicate_time.setText(json.getString("time"));
                                    worker_tel_str = json.getString("worker_telephone");
                                    worker_tel.setText("联系电话："+json.getString("worker_telephone"));
                                    worker_company.setText("工作单位："+json.getString("worker_company"));
                                    applicatin_name.setText("申请人："+json.getString("applicant"));
                                    application_tel_str = json.getString("applicant_telephone");
                                    application_tel.setText("联系电话："+application_tel_str);
                                    applicate_money.setText("申请金额："+json.getString("money"));
                                    applicate_num.setText("分期数："+json.getString("installment_num"));
                                    buy_commodity.setText("购买商品："+json.getString("car_type"));
                                    self_score.setText("评分："+json.getString("evaluation"));
                                    //返回什么
                                    system_score.setText(json.getString("car_type"));
                                    JSONArray jsonArray = json.getJSONArray("evaluation_detail");
                                    if(jsonArray.length()>0){
                                        for(int i=0;i<jsonArray.length();i++){
                                            JSONObject temp = (JSONObject) jsonArray.get(i);
                                            Log.d("www","temp:"+jsonArray.get(i));
                                            EvaluationItemVO evaluationItemVO = new EvaluationItemVO();
                                            evaluationItemVO.setTitle(temp.getString("title"));
                                            Log.d("www",""+temp.getString("title"));
                                            evaluationItemVO.setContent(temp.getString("content"));
                                            evaluationItemVO.setGoal(temp.getString("goal"));
                                            list.add(evaluationItemVO);
                                        }
                                        adapter = new EvaluationItemAdapter(list,NewNotificationDetailNewActivity.this);
                                        lv_detail_information.setAdapter(adapter);
                                    }else {
                                        finish();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case 1:
                        String reCode =(String) msg.obj;
                        if(reCode!=null){
                            try {
                                JSONObject json = new JSONObject(reCode);
                                String message = json.getString("message");
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(NewNotificationDetailNewActivity.this,""+message,Toast.LENGTH_SHORT).show();
                                }else if("true".equals(status)) {
                                    Toast.makeText(NewNotificationDetailNewActivity.this,""+message,Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case 2:
                        String reCode2 =(String) msg.obj;
                        if(reCode2!=null){
                            try {
                                JSONObject json = new JSONObject(reCode2);
                                String message = json.getString("message");
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(NewNotificationDetailNewActivity.this,""+message,Toast.LENGTH_SHORT).show();
                                }else if("true".equals(status)) {
                                    Toast.makeText(NewNotificationDetailNewActivity.this,""+message,Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
            }
        };
    }

}
