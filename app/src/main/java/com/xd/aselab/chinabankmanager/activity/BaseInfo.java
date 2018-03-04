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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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

public class BaseInfo extends AppCompatActivity {

    private RelativeLayout back;
    private ListView list_view;
    private ImageView no_data_img;
    private TextView no_data_txt;
    private SearchView search;

    private SimpleAdapter adapter;
    private SharePreferenceUtil spu;
    private Handler handler;
    private List<Map<String, String>> list;
    private List<Map<String, String>> backup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.act_base_info_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        no_data_img = (ImageView) findViewById(R.id.act_base_info_no_data_img);
        no_data_txt = (TextView) findViewById(R.id.act_base_info_no_data_txt);

        search = (SearchView) findViewById(R.id.act_base_info_search);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (list != null && list_view.getVisibility()==View.VISIBLE ) {
                    backup = new ArrayList<>();
                    for (Map<String, String> map : list) {
                        if (map.get("real_name").substring(3).contains(query)) {
                            backup.add(map);
                        }
                    }
                    adapter = new SimpleAdapter(BaseInfo.this, backup, R.layout.list_view_base_info,
                            new String[]{"real_name", "job_number", "tele_number"},
                            new int[]{R.id.list_view_base_info_name, R.id.list_view_base_info_job_number, R.id.list_view_base_info_tel});
                    list_view.setAdapter(adapter);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return onQueryTextSubmit(newText);
            }
        });

        list_view = (ListView) findViewById(R.id.act_base_info_list_view);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                startActivityForResult(new Intent().setClass(BaseInfo.this, BaseInfoTransparent.class).putExtra("position",position).putExtra("fromwhere","BaseInfo"), 104);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });

        spu = new SharePreferenceUtil(BaseInfo.this, "user");

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode != null) {
                                Log.e("BaseInfo：reCode", ""+reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(BaseInfo.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONArray jsonArray = json.getJSONArray("list");
                                    if (jsonArray.length() == 0) {
                                        list_view.setVisibility(View.GONE);
                                        no_data_img.setVisibility(View.VISIBLE);
                                        no_data_txt.setVisibility(View.VISIBLE);
                                        Toast.makeText(BaseInfo.this, "还没有银行卡客户经理信息", Toast.LENGTH_SHORT).show();
                                    } else {
                                        no_data_img.setVisibility(View.GONE);
                                        no_data_txt.setVisibility(View.GONE);
                                        list = new ArrayList<>();
                                        backup = new ArrayList<>();
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject temp = (JSONObject) jsonArray.get(i);
                                            Map<String, String> map = new HashMap<>();
                                            map.put("real_name", "经理姓名：" + temp.getString("real_name"));
                                            map.put("job_number", "网点名称：" + temp.getString("branch_sub_name"));
                                            map.put("tele_number", "联系方式：" + temp.getString("tele_number"));
                                            //map.put("sex", "0".equals(temp.getString("sex")) ? "女" : "男");
                                            //map.put("branch_sub_name", "工作地点：" + temp.getString("branch_sub_name"));
                                            list.add(map);
                                            backup.add(map);
                                        }
                                        adapter = new SimpleAdapter(BaseInfo.this, backup, R.layout.list_view_base_info,
                                                new String[]{"real_name", "job_number", "tele_number"},
                                                new int[]{R.id.list_view_base_info_name, R.id.list_view_base_info_job_number, R.id.list_view_base_info_tel});
                                        list_view.setAdapter(adapter);
                                        list_view.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    Log.e("BaseInfo_Activity", BaseInfo.this.getResources().getString(R.string.status_exception));
                                }
                            } else {
                                Toast.makeText(BaseInfo.this, BaseInfo.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("BaseInfo_Activity", "reCode为空");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("BaseInfo_Activity", BaseInfo.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

        new Thread(){
            @Override
            public void run() {
                super.run();
                PostParameter[] params = new PostParameter[2];
                params[0] = new PostParameter("branchLevel4", spu.getBranchLevel4());
                params[1] = new PostParameter("cookie", spu.getCookie());
                String reCode = ConnectUtil.httpRequest(ConnectUtil.BASE_INFO, params, ConnectUtil.POST);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, final Intent intent){
        if(143==resultCode){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                        Constants.ActivityCompatRequestPermissionsCode);
                return;
            }
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                    backup.get(intent.getIntExtra("position", 0)).get("tele_number").substring(5))));
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        search.clearFocus();
    }
}
