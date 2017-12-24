package com.xd.aselab.chinabankmanager.activity;

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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.Constants;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyContact extends AppCompatActivity {

    private RelativeLayout back;
    private ListView list_view;
    /*private TextView manager;
    private TextView job_number;
    private TextView tel;
    private TextView landline;*/

    private SharePreferenceUtil spu;
    private Handler handler;
    private List<Map<String, String>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.act_my_cont_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list_view = (ListView) findViewById(R.id.act_my_cont_list_view);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(MyContact.this, MyContactTrans.class);
                intent.putExtra("tel", list.get(position).get("teleNumber").substring(3));
                intent.putExtra("landline", list.get(position).get("landlineNumber").substring(3));
                startActivityForResult(intent, 152);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0 :
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                Log.e("MyContact：reCode", reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(MyContact.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    /*manager.setText(json.getString("realName"));
                                    job_number.setText(json.getString("jobNumber"));
                                    tel.setText(json.getString("teleNumber"));
                                    landline.setText(json.getString("landlineNumber"));*/
                                    JSONArray jsonArray = json.getJSONArray("list");
                                    list = new ArrayList<>();
                                    for (int i=0; i<jsonArray.length(); i++){
                                        JSONObject temp = (JSONObject) jsonArray.get(i);
                                        Map<String, String> map = new HashMap<>();
                                        map.put("realName", temp.getString("realName"));
                                        map.put("jobNumber", "工号："+temp.getString("jobNumber"));
                                        map.put("teleNumber", "手机："+temp.getString("teleNumber"));
                                        map.put("landlineNumber", "座机："+temp.getString("landlineNumber"));
                                        list.add(map);
                                    }
                                    SimpleAdapter adapter = new SimpleAdapter(MyContact.this, list, R.layout.list_view_my_contact,
                                            new String[]{"realName", "jobNumber", "teleNumber", "landlineNumber"},
                                            new int[]{R.id.list_view_my_contact_name, R.id.list_view_my_contact_job_number,
                                            R.id.list_view_my_contact_tel, R.id.list_view_my_contact_landline });
                                    list_view.setAdapter(adapter);
                                } else {
                                    Log.e("MyContact_Activity", MyContact.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(MyContact.this, MyContact.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("MyContact_Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("MyContact_Activity", MyContact.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

        /*manager = (TextView) findViewById(R.id.act_my_cont_manager_txt);
        job_number = (TextView) findViewById(R.id.act_my_cont_job_num_txt);
        tel = (TextView) findViewById(R.id.act_my_cont_tel_txt);
        landline = (TextView) findViewById(R.id.act_my_cont_landline_txt);*/

        new Thread(){
            @Override
            public void run() {
                super.run();
                spu = new SharePreferenceUtil(MyContact.this, "user");
                PostParameter[] params = new PostParameter[2];
                params[0] = new PostParameter("branchLevel4",spu.getBranchLevel4());
                params[1] = new PostParameter("cookie", spu.getCookie());
                String reCode = ConnectUtil.httpRequest(ConnectUtil.MY_CONTACT, params, ConnectUtil.POST);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, final Intent intent){
        if(125==resultCode && "contact".equals(intent.getStringExtra("action")) ){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                        Constants.ActivityCompatRequestPermissionsCode);
                return;
            }
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + intent.getStringExtra("contact") ) ) );
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }
}
