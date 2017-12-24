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
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.adapter.WorkerJoinCheckAdapter;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.model.WorkerVO;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class KMyContactsActivity extends AppCompatActivity {
    private RelativeLayout back;
    private ListView listView;
    private WorkerJoinCheckAdapter adapter;
    private Handler handler;
    private SharePreferenceUtil spu;
    private ImageView no_data_img;
    private TextView no_data_txt;
    private List<WorkerVO> list = new ArrayList<>();
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kmy_contacts);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        spu = new SharePreferenceUtil(KMyContactsActivity.this,"user");

        initViews();
        getContactsList();
        parseData();
        initEvents();

    }

    void initViews(){
        back = (RelativeLayout)findViewById(R.id.act_k_my_contacts_back_btn);
        listView = (ListView)findViewById(R.id.act_k_my_contacts_list);
        no_data_img = (ImageView)findViewById(R.id.act_kafenqi_my_contacts_no_data_img);
        no_data_txt = (TextView)findViewById(R.id.act_kafenqi_my_contacts_no_data_txt);
    }

    void getContactsList(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                PostParameter[] params = new PostParameter[1];
                params[0] = new PostParameter("account",spu.getAccount());
                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetInstallmentWorker,params,ConnectUtil.POST);
                Message msg = new Message();
                msg.what=0;
                msg.obj=reCode;
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

                        try{
                            String reCode =(String) msg.obj;
                            if(reCode!=null){
                                JSONObject json= new JSONObject(reCode);
                                String status = json.getString("status");
                                if("true".equals(status)){
                                    JSONArray listArray = json.getJSONArray("worker_list");
                                    if(listArray.length()==0){
                                        listView.setVisibility(View.GONE);
                                        no_data_img.setVisibility(View.VISIBLE);
                                        no_data_txt.setVisibility(View.VISIBLE);
                                        Toast.makeText(KMyContactsActivity.this, "还没有推广员加盟信息", Toast.LENGTH_SHORT).show();
                                        sleep_and_finish(2);
                                    }else{
                                        listView.setVisibility(View.VISIBLE);
                                        no_data_img.setVisibility(View.GONE);
                                        no_data_txt.setVisibility(View.GONE);
                                        for(int i=0;i<listArray.length();i++){
                                            JSONObject temp = (JSONObject) listArray.get(i);
                                            WorkerVO workerVO = new WorkerVO();
                                            workerVO.setName(temp.getString("name"));
                                            workerVO.setAccount(temp.getString("account"));
                                            workerVO.setWorker_tel(temp.getString("telephone"));
                                            workerVO.setWorker_address(temp.getString("company"));
                                            workerVO.setHead_image(temp.getString("head_image"));

                                            list.add(workerVO);
                                        }
                                        adapter = new WorkerJoinCheckAdapter(list,KMyContactsActivity.this);
                                        listView.setAdapter(adapter);
                                    }
                                }else {
                                    Toast.makeText(KMyContactsActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                }

                            }else{
                                Toast.makeText(KMyContactsActivity.this, KMyContactsActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("ShopJoin_Activity", "reCode为空");
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }
            }
        };
    }

    void initEvents(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(KMyContactsActivity.this, KMyContactsTransparentActivity.class);
                intent.putExtra("position", position);
                startActivityForResult(intent, 910);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (912==resultCode){
            position = data.getIntExtra("position", 0);
            switch (data.getStringExtra("action")){
                case "call" :
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+list.get(position).getWorker_tel()));
                    if (Build.VERSION.SDK_INT >= 23) {
                        //判断有没有拨打电话权限
                        if (PermissionChecker.checkSelfPermission(KMyContactsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            //请求拨打电话权限
                            ActivityCompat.requestPermissions(KMyContactsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 10015);
                        } else {
                            startActivity(intent);
                        }

                    } else {
                        startActivity(intent);
                    }
                    //startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + list.get(position).getWorker_tel())));
                    break;
                case "online" :
                    Intent intent1 = new Intent(KMyContactsActivity.this, ChatActivity.class);
                    intent1.putExtra("receiver",list.get(position).getAccount());
                    intent1.putExtra("receiver_name",list.get(position).getName());
                    intent1.putExtra("receiver_head",list.get(position).getHead_image());
                    startActivity(intent1);
                    break;
                case "release" :
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            PostParameter[] params = new PostParameter[2];
                            params[0] = new PostParameter("manager_account", spu.getAccount());
                            params[1] = new PostParameter("worker_account", list.get(position).getAccount());
                            String reCode = ConnectUtil.httpRequest(ConnectUtil.InstallmentActivelyRelieveWorker, params, ConnectUtil.POST);
                            Message msg= new Message();
                            msg.what = 2;
                            msg.obj = reCode;
                            handler.sendMessage(msg);
                        }
                    }.start();
                    break;
            }
        }
    }


    private void sleep_and_finish(final int s){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(s * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    finish();
                }
            }
        }.start();
    }

}
