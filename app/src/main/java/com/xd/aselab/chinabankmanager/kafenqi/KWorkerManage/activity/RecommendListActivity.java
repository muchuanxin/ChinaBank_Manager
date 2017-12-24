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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.chat.ui.ChatActivity;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.adapter.WorkerRecommendListAdapter;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.model.ApplicationsVO;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.Constants;
import com.xd.aselab.chinabankmanager.util.PostParameter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecommendListActivity extends AppCompatActivity {

    private RelativeLayout back;
    private TextView tv_name;
    private TextView tv_worker_status;
    private TextView tv_worker_tel;
    private ImageView iv_worker_tel;
    private ImageView iv_worker_communacate;
    private TextView tv_worker_address;
    private List<ApplicationsVO> applicationsVOList = new ArrayList<>();
    private ListView recommend_listView;
    private String worker_account,range;
    private String worker_name,worker_tel,worker_company;
    private String worker_name_str,worker_head_str,worker_account_str;
    private Handler handler;
    private WorkerRecommendListAdapter adaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        worker_account = getIntent().getStringExtra("worker_account");
        range = getIntent().getStringExtra("range");

        initViews();
        getListData();
        parseData();
        initEvents();

    }

    void initViews(){
        back = (RelativeLayout)findViewById(R.id.act_recommend_list_back_btn);
        tv_name = (TextView)findViewById(R.id.detail_name);
        tv_worker_status = (TextView)findViewById(R.id.detail_worker_status);
        iv_worker_communacate = (ImageView) findViewById(R.id.act_worker_communacate);
        tv_worker_tel = (TextView)findViewById(R.id.detail_tel);
        iv_worker_tel = (ImageView)findViewById(R.id.act_worker_tel);
        tv_worker_address = (TextView)findViewById(R.id.detail_address);
        recommend_listView = (ListView)findViewById(R.id.recommend_list);
    }

    void getListData(){

        new Thread(){
            @Override
            public void run() {
                super.run();
                final PostParameter[] params = new PostParameter[2];
                params[0] = new PostParameter("worker_account", worker_account);
                params[1] = new PostParameter("range", range);
                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetInstallmentWorkerRecommendList, params, ConnectUtil.POST);
                Log.e("reCode",""+reCode);
                Message msg = new Message();
                msg.what = 0;
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
                    case 0 :
                        setListData(msg);
                        break;
                    case 1:
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                Toast.makeText(RecommendListActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                if("true".equals(status)){
                                    applicationsVOList.get(msg.arg1).setState("YES");
                                    adaper.setList(applicationsVOList);
                                    adaper.notifyDataSetChanged();
                                }

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                Toast.makeText(RecommendListActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                if("true".equals(status)){
                                    applicationsVOList.get(msg.arg1).setState("NO");
                                    adaper.setList(applicationsVOList);
                                    adaper.notifyDataSetChanged();
                                }

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };

    }

    void setListData(Message msg){
        try {
            String reCode = (String) msg.obj;
            if (reCode!=null){
                JSONObject json = new JSONObject(reCode);
                String status = json.getString("status");

                if ("false".equals(status)) {
                    Toast.makeText(RecommendListActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                } else if ("true".equals(status)) {

                    worker_name_str = json.getString("name");
                    worker_head_str = json.getString("head_image");
                    worker_account_str= json.getString("account");

                    worker_name = json.getString("name");
                    tv_name.setText(worker_name);
                    String worker_status = json.getString("worker_status");
                    switch (worker_status){
                        case "已加盟":
                            tv_worker_status.setVisibility(View.GONE);
                            break;
                        case "正在申请解约":
                            tv_worker_status.setVisibility(View.VISIBLE);
                            tv_worker_status.setText(worker_status);
                            break;
                        case "已解约":
                            tv_worker_status.setVisibility(View.VISIBLE);
                            tv_worker_status.setText(worker_status);
                            break;
                    }

                    worker_tel = json.getString("telephone");
                    tv_worker_tel.setText("联系电话："+worker_tel);
                    worker_company = json.getString("company");
                    tv_worker_address.setText("工作单位："+worker_company);

                    JSONArray recommendJA = json.getJSONArray("recommend");

                    for(int i=0;i<recommendJA.length();i++){
                        JSONObject temp = recommendJA.getJSONObject(i);
                        ApplicationsVO applicationsVO = new ApplicationsVO();
                        applicationsVO.setApplicationID(temp.getString("id"));
                        applicationsVO.setApplicatinName(temp.getString("applicant"));
                        applicationsVO.setApplicateTime(temp.getString("time"));
                        applicationsVO.setFenqi_money(temp.getInt("money"));
                        applicationsVO.setFenqi_num(temp.getInt("installment_num"));
                        applicationsVO.setBuy_commodity(temp.getString("car_type"));
                        applicationsVO.setScore(temp.getInt("evaluation"));
                        applicationsVO.setState(temp.getString("confirm"));
                        applicationsVO.setTel(temp.getString("telephone"));
                        applicationsVOList.add(applicationsVO);
                    }

                    adaper.setList(applicationsVOList);
                    adaper.notifyDataSetChanged();

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void initEvents(){

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_worker_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(RecommendListActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RecommendListActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                            Constants.ActivityCompatRequestPermissionsCode);
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+worker_tel));
                startActivity(intent);
            }
        });

        iv_worker_communacate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecommendListActivity.this, ChatActivity.class);
                intent.putExtra("receiver",worker_account_str);
                intent.putExtra("receiver_name",worker_name_str);
                intent.putExtra("receiver_head",worker_head_str);
                startActivity(intent);
            }
        });

        WorkerRecommendListAdapter.ListBtnListener mListener = new WorkerRecommendListAdapter.ListBtnListener() {
            @Override
            public void myOnClick(final int position, View v) {
                if(v.getId()==R.id.bt_list_confirm){
                   // Toast.makeText(RecommendListActivity.this,"点击了确认按钮"+position,Toast.LENGTH_SHORT).show();
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            final PostParameter[] params = new PostParameter[2];
                            params[0] = new PostParameter("id", applicationsVOList.get(position).getApplicationID());
                            params[1] = new PostParameter("confirm", "YES");
                            String reCode = ConnectUtil.httpRequest(ConnectUtil.ConfirmInstallmentRecommend, params, ConnectUtil.POST);
                            Log.e("reCode",""+reCode);
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = reCode;
                            msg.arg1=position;
                            handler.sendMessage(msg);
                        }
                    }.start();

                }else if(v.getId()==R.id.bt_list_refuse){
                   // Toast.makeText(RecommendListActivity.this,"点击了拒绝按钮"+position,Toast.LENGTH_SHORT).show();
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            final PostParameter[] params = new PostParameter[2];
                            params[0] = new PostParameter("id", applicationsVOList.get(position).getApplicationID());
                            params[1] = new PostParameter("confirm", "NO");
                            String reCode = ConnectUtil.httpRequest(ConnectUtil.ConfirmInstallmentRecommend, params, ConnectUtil.POST);
                            Log.e("reCode",""+reCode);
                            Message msg = new Message();
                            msg.what = 2;
                            msg.obj = reCode;
                            msg.arg1=position;
                            handler.sendMessage(msg);
                        }
                    }.start();
                } else if(v.getId()==R.id.act_tel){
                    if (ActivityCompat.checkSelfPermission(RecommendListActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(RecommendListActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                                Constants.ActivityCompatRequestPermissionsCode);
                        return;
                    }
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+applicationsVOList.get(position).getTel()));
                    startActivity(intent);
                }
                /*Intent intent=new Intent();
                String data=listValue.get(position).getTel();
                intent.putExtra("tel",data);
                setResult(152, intent);
                finish();*/
            }
        };

        adaper = new WorkerRecommendListAdapter(applicationsVOList,RecommendListActivity.this,mListener);
        recommend_listView.setAdapter(adaper);

        recommend_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(RecommendListActivity.this,"点击了整个条目"+position,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(RecommendListActivity.this,NewNotificationDetail2Activity.class);
                intent.putExtra("id",applicationsVOList.get(position).getApplicationID());
                intent.putExtra("position",position);
                startActivityForResult(intent,999);
                //startActivity(intent);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (1000==resultCode){
            int position ;
            position = data.getIntExtra("position", 0);
            switch (data.getStringExtra("action")){
                case "confirm":
                    Log.d("www","confirm----"+position);
                    applicationsVOList.get(position).setState("YES");
                    adaper.setList(applicationsVOList);
                    adaper.notifyDataSetChanged();
                    break;
                case "refuse":
                    Log.d("www","refuse----"+position);
                    applicationsVOList.get(position).setState("NO");
                    adaper.setList(applicationsVOList);
                    adaper.notifyDataSetChanged();
                    break;
            }
        }

    }
}
