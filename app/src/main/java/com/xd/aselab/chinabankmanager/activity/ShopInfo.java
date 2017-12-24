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

public class ShopInfo extends AppCompatActivity {

    private RelativeLayout back;
    private ListView list_view;
    private ImageView no_data_img;
    private TextView no_data_txt;
    private SearchView search;

    private SimpleAdapter adapter;
    private List<Map<String, String>> list;
    private List<Map<String, String>> backup;
    private SharePreferenceUtil spu;
    private Handler handler;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.act_shop_info_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list_view = (ListView) findViewById(R.id.act_shop_info_list_view);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ("".equals(backup.get(position).get("shopRelease"))){
                    Intent intent = new Intent();
                    intent.setClass(ShopInfo.this, ShopInfoTransparent.class);
                    intent.putExtra("position", position);
                    startActivityForResult(intent, 158);
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                }
                else {
                    Toast.makeText(ShopInfo.this, "店铺"+backup.get(position).get("shopRelease"), Toast.LENGTH_SHORT).show();
                }
            }
        });

        spu = new SharePreferenceUtil(ShopInfo.this, "user");

        no_data_img = (ImageView) findViewById(R.id.act_shop_info_no_data_img);
        no_data_txt = (TextView) findViewById(R.id.act_shop_info_no_data_txt);
        search = (SearchView) findViewById(R.id.act_shop_info_search_bar);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (list != null && list_view.getVisibility()==View.VISIBLE ) {
                    backup = new ArrayList<>();
                    for (Map<String, String> map : list) {
                        if (map.get("shopName").contains(query)) {
                            backup.add(map);
                        }
                    }
                    adapter = new SimpleAdapter(ShopInfo.this, backup, R.layout.list_view_shop_info,
                            new String[]{"shopName", "ownerName", "shop_type", "shop_addr"},
                            new int[]{R.id.list_view_shop_info_shop_name, R.id.list_view_shop_info_manager_name,
                                    R.id.list_view_shop_info_shop_type, R.id.list_view_shop_info_shop_addr });
                    list_view.setAdapter(adapter);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return onQueryTextSubmit(newText);
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
                                //Log.e("ShopInfo：reCode", reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(ShopInfo.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONArray jsonArray = json.getJSONArray("shops");
                                    if (jsonArray.length()==0){
                                        list_view.setVisibility(View.GONE);
                                        no_data_img.setVisibility(View.VISIBLE);
                                        no_data_txt.setVisibility(View.VISIBLE);
                                        Toast.makeText(ShopInfo.this, "还没有店铺信息", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        no_data_img.setVisibility(View.GONE);
                                        no_data_txt.setVisibility(View.GONE);
                                        list = new ArrayList<>();
                                        backup = new ArrayList<>();
                                        //Log.e("jsonArray",jsonArray.toString());
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject temp = (JSONObject) jsonArray.get(i);
                                            Map<String, String> map = new HashMap<>();
                                            map.put("shopName", temp.getString("shopName"));
                                            //map.put("ownerName", "店铺负责人："+temp.getString("shopAccount"));
                                            map.put("shopRelease", temp.getString("status"));
                                            map.put("ownerName", "");
                                            map.put("shop_type", "店铺类型："+temp.getString("industry"));
                                            map.put("shop_addr", "地址："+temp.getString("province")+temp.getString("city")
                                                    +temp.getString("county")+temp.getString("street")+temp.getString("locationDescribe"));
                                            map.put("teleNumber", temp.getString("teleNumber"));
                                            map.put("shopAccount", temp.getString("shopAccount"));
                                            list.add(map);
                                            backup.add(map);
                                        }
                                        adapter = new SimpleAdapter(ShopInfo.this, backup, R.layout.list_view_shop_info,
                                                new String[]{"shopName", "shopRelease", "ownerName", "shop_type", "shop_addr"},
                                                new int[]{R.id.list_view_shop_info_shop_name, R.id.list_view_shop_info_release,
                                                        R.id.list_view_shop_info_manager_name,
                                                        R.id.list_view_shop_info_shop_type, R.id.list_view_shop_info_shop_addr });
                                        list_view.setAdapter(adapter);
                                        list_view.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    Log.e("ShopInfo_Activity", ShopInfo.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(ShopInfo.this, ShopInfo.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("ShopInfo_Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    case 1 :
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                Log.e("ShopInfo：reCode", reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(ShopInfo.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    Toast.makeText(ShopInfo.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                    list.remove(backup.get(position));
                                    backup.remove(position);
                                    if (list.size()>0){
                                        no_data_img.setVisibility(View.GONE);
                                        no_data_txt.setVisibility(View.GONE);
                                        SimpleAdapter adapter = new SimpleAdapter(ShopInfo.this, backup, R.layout.list_view_shop_info,
                                                new String[]{"shopName", "ownerName", "shop_type", "shop_addr"},
                                                new int[]{R.id.list_view_shop_info_shop_name, R.id.list_view_shop_info_manager_name,
                                                        R.id.list_view_shop_info_shop_type, R.id.list_view_shop_info_shop_addr });
                                        list_view.setAdapter(adapter);
                                        list_view.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        list = null;
                                        list_view.setVisibility(View.GONE);
                                        no_data_img.setVisibility(View.VISIBLE);
                                        no_data_txt.setVisibility(View.VISIBLE);
                                        Toast.makeText(ShopInfo.this, "还没有店铺信息", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.e("ShopInfo_Activity", ShopInfo.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(ShopInfo.this, ShopInfo.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("ShopInfo_Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("ShopInfo_Activity", ShopInfo.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

        new Thread(){
            @Override
            public void run() {
                super.run();
                PostParameter[] params = new PostParameter[4];
                params[0] = new PostParameter("account", spu.getAccount());
                params[1] = new PostParameter("branchLevel4", spu.getBranchLevel4());
                params[2] = new PostParameter("type", ""+0);
                params[3] = new PostParameter("cookie", spu.getCookie());
                String reCode = ConnectUtil.httpRequest(ConnectUtil.SHOP_INFO, params, ConnectUtil.POST);
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
        if (185==resultCode){
            if ("contact".equals(data.getStringExtra("action"))){
                position = data.getIntExtra("position", 0);if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                            Constants.ActivityCompatRequestPermissionsCode);
                    return;
                }
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + backup.get(position).get("teleNumber"))));
            }
            else if ("release".equals(data.getStringExtra("action"))){
                position = data.getIntExtra("position", 0);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        PostParameter[] params = new PostParameter[3];
                        params[0] = new PostParameter("account", spu.getAccount());
                        params[1] = new PostParameter("shopAccount", backup.get(position).get("shopAccount"));
                        params[2] = new PostParameter("cookie", spu.getCookie());
                        String reCode = ConnectUtil.httpRequest(ConnectUtil.RELEASE_SHOP, params, ConnectUtil.POST);
                        Message msg= new Message();
                        msg.what = 1;
                        msg.obj = reCode;
                        handler.sendMessage(msg);
                    }
                }.start();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        search.clearFocus();
    }
}
