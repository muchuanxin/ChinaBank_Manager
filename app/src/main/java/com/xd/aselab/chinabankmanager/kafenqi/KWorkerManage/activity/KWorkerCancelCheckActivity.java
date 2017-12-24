package com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
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
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.adapter.WorkerCancelCheckAdapter;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.model.WorkerVO;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.Constants;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class KWorkerCancelCheckActivity extends AppCompatActivity {
    private RelativeLayout back;
    private ListView cancelListview;
    private ImageView no_data_img;
    private TextView no_data_txt;
    private List<WorkerVO> list;
    private SharePreferenceUtil spu;
    private Handler handler;
    private int position;
    private WorkerCancelCheckAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kworker_cancel_check);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initViews();
        initEvents();

    }

    void initViews() {
        back = (RelativeLayout) findViewById(R.id.act_kafenqi_worker_cancel_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cancelListview = (ListView) findViewById(R.id.act_kafenqi_worker_cancel_list_view);
        no_data_img = (ImageView) findViewById(R.id.act_kafenqi_worker_cancel_no_data_img);
        no_data_txt = (TextView) findViewById(R.id.act_kafenqi_worker_cancel_no_data_txt);

    }

    void initEvents() {
        cancelListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(KWorkerCancelCheckActivity.this, KWorkerCancelTransparentActivity.class);
                intent.putExtra("position", position);
                startActivityForResult(intent, 901);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        spu = new SharePreferenceUtil(KWorkerCancelCheckActivity.this, "user");

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode != null) {
                                //Log.e("ShopJoin：reCode", reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(KWorkerCancelCheckActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONArray jsonArray = json.getJSONArray("worker_list");
                                    if (jsonArray.length() == 0) {
                                        cancelListview.setVisibility(View.GONE);
                                        no_data_img.setVisibility(View.VISIBLE);
                                        no_data_txt.setVisibility(View.VISIBLE);
                                        Toast.makeText(KWorkerCancelCheckActivity.this, "还没有推广员解约信息", Toast.LENGTH_SHORT).show();
                                        sleep_and_finish(1);
                                    } else {
                                        no_data_img.setVisibility(View.GONE);
                                        no_data_txt.setVisibility(View.GONE);
                                        list = new ArrayList<>();
                                        //Log.e("jsonArray",jsonArray.toString());
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject temp = (JSONObject) jsonArray.get(i);
                                            WorkerVO workerVO = new WorkerVO();
                                            workerVO.setAccount(temp.getString("account"));
                                            workerVO.setName(temp.getString("name"));
                                            workerVO.setWorker_tel(temp.getString("telephone"));
                                            workerVO.setWorker_address(temp.getString("company"));
                                            workerVO.setCancel_reason(temp.getString("reason"));
                                            list.add(workerVO);
                                        }
                                        adapter = new WorkerCancelCheckAdapter(list, KWorkerCancelCheckActivity.this);
                                        cancelListview.setAdapter(adapter);
                                        cancelListview.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    Log.e("KWorkerCancelCheck", KWorkerCancelCheckActivity.this.getResources().getString(R.string.status_exception));
                                }
                            } else {
                                Toast.makeText(KWorkerCancelCheckActivity.this, KWorkerCancelCheckActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("KWorkerCancelCheck", "reCode为空");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode != null) {
                                Log.e("KWokerRelease：reCode", reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(KWorkerCancelCheckActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    Toast.makeText(KWorkerCancelCheckActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                    list.remove(position);
                                    if (list.size() > 0) {
                                        no_data_img.setVisibility(View.GONE);
                                        no_data_txt.setVisibility(View.GONE);
                                        adapter = new WorkerCancelCheckAdapter(list, KWorkerCancelCheckActivity.this);
                                        cancelListview.setAdapter(adapter);
                                        cancelListview.setVisibility(View.VISIBLE);
                                    } else {
                                        cancelListview.setVisibility(View.GONE);
                                        no_data_img.setVisibility(View.VISIBLE);
                                        no_data_txt.setVisibility(View.VISIBLE);
                                        Toast.makeText(KWorkerCancelCheckActivity.this, "还没有推广员解约信息", Toast.LENGTH_SHORT).show();
                                        sleep_and_finish(2);
                                    }
                                } else {
                                    Log.e("KWorkerCancelCheck", KWorkerCancelCheckActivity.this.getResources().getString(R.string.status_exception));
                                }
                            } else {
                                Toast.makeText(KWorkerCancelCheckActivity.this, KWorkerCancelCheckActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("KWorkerCancelCheck", "reCode为空");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode != null) {
                                Log.e("KWorkerRelease：reCode", reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(KWorkerCancelCheckActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    Toast.makeText(KWorkerCancelCheckActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                    list.remove(position);
                                    if (list.size() > 0) {
                                        no_data_img.setVisibility(View.GONE);
                                        no_data_txt.setVisibility(View.GONE);
                                        adapter = new WorkerCancelCheckAdapter(list, KWorkerCancelCheckActivity.this);
                                        cancelListview.setAdapter(adapter);
                                        cancelListview.setVisibility(View.VISIBLE);
                                    } else {
                                        cancelListview.setVisibility(View.GONE);
                                        no_data_img.setVisibility(View.VISIBLE);
                                        no_data_txt.setVisibility(View.VISIBLE);
                                        Toast.makeText(KWorkerCancelCheckActivity.this, "还没有推广员加盟信息", Toast.LENGTH_SHORT).show();
                                        sleep_and_finish(2);
                                    }
                                } else {
                                    Log.e("KWorkerCancelCheck", KWorkerCancelCheckActivity.this.getResources().getString(R.string.status_exception));
                                }
                            } else {
                                Toast.makeText(KWorkerCancelCheckActivity.this, KWorkerCancelCheckActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("KWorkerCancelCheck", "reCode为空");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("KWorkerCancelCheck", KWorkerCancelCheckActivity.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

        new Thread() {
            @Override
            public void run() {
                super.run();
                PostParameter[] params = new PostParameter[1];
                params[0] = new PostParameter("account", spu.getAccount());
                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetInstallmentReleaseWorker, params, ConnectUtil.POST);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (903 == resultCode) {
            position = data.getIntExtra("position", 0);
            switch (data.getStringExtra("action")) {
                case "contact":
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                                Constants.ActivityCompatRequestPermissionsCode);
                        return;
                    }
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + list.get(position).getWorker_tel())));
                    break;
                case "agree" :
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            PostParameter[] params = new PostParameter[2];
                            params[0] = new PostParameter("manager_account", spu.getAccount());
                            params[1] = new PostParameter("worker_account", list.get(position).getAccount());
                            String reCode = ConnectUtil.httpRequest(ConnectUtil.AgreeInstallmentReleaseWorker, params, ConnectUtil.POST);
                            Message msg= new Message();
                            msg.what = 1;
                            msg.obj = reCode;
                            handler.sendMessage(msg);
                        }
                    }.start();
                    break;
                case "refuse" :
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            PostParameter[] params = new PostParameter[2];
                            params[0] = new PostParameter("manager_account", spu.getAccount());
                            params[1] = new PostParameter("worker_account", list.get(position).getAccount());
                            String reCode = ConnectUtil.httpRequest(ConnectUtil.RefuseInstallmentReleaseWorker, params, ConnectUtil.POST);
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
