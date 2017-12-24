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

public class ShopJoin extends AppCompatActivity {

    private RelativeLayout back;
    private ListView list_view;
    private ImageView no_data_img;
    private TextView no_data_txt;

    private List<Map<String, String>> list;
    private SharePreferenceUtil spu;
    private Handler handler;
    private int position;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_join);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.act_shop_join_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list_view = (ListView) findViewById(R.id.act_shop_join_list_view);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(ShopJoin.this, ShopJoinTransparent.class);
                intent.putExtra("position", position);
                startActivityForResult(intent, 157);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });

        spu = new SharePreferenceUtil(ShopJoin.this, "user");

        no_data_img = (ImageView) findViewById(R.id.act_shop_join_no_data_img);
        no_data_txt = (TextView) findViewById(R.id.act_shop_join_no_data_txt);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0 :
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                //Log.e("ShopJoin：reCode", reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(ShopJoin.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONArray jsonArray = json.getJSONArray("shops");
                                    if (jsonArray.length()==0){
                                        list_view.setVisibility(View.GONE);
                                        no_data_img.setVisibility(View.VISIBLE);
                                        no_data_txt.setVisibility(View.VISIBLE);
                                        Toast.makeText(ShopJoin.this, "还没有店铺加盟信息", Toast.LENGTH_SHORT).show();
                                        sleep_and_finish(1);
                                    }
                                    else {
                                        no_data_img.setVisibility(View.GONE);
                                        no_data_txt.setVisibility(View.GONE);
                                        list = new ArrayList<>();
                                        //Log.e("jsonArray",jsonArray.toString());
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject temp = (JSONObject) jsonArray.get(i);
                                            Map<String, String> map = new HashMap<>();
                                            map.put("shopName", temp.getString("shopName"));
                                            map.put("shop_type", "店铺类型："+temp.getString("industry"));
                                            //map.put("ownerName", "店主："+temp.getString("account"));
                                            map.put("ownerName", "");
                                            map.put("teleNumber", "电话："+temp.getString("teleNumber"));
                                            map.put("shop_addr", "地址："+temp.getString("province")+temp.getString("city")
                                                    +temp.getString("county")+temp.getString("street")/*+temp.getString("locationDescribe")*/);
                                            map.put("shopAccount", temp.getString("account"));
                                            list.add(map);
                                        }
                                        SimpleAdapter adapter = new SimpleAdapter(ShopJoin.this, list, R.layout.list_view_shop_join,
                                                new String[]{"shopName", "shop_type",  "teleNumber","ownerName", "shop_addr"},
                                                new int[]{R.id.list_view_shop_join_release_shop_name, R.id.list_view_shop_join_release_shop_type,
                                                        R.id.list_view_shop_join_release_manager_name, R.id.list_view_shop_join_release_tel,
                                                        R.id.list_view_shop_join_release_shop_addr});
                                        list_view.setAdapter(adapter);
                                        list_view.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    Log.e("ShopJoin_Activity", ShopJoin.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(ShopJoin.this, ShopJoin.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("ShopJoin_Activity", "reCode为空");
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
                                Log.e("ShopJoin：reCode", reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(ShopJoin.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    Toast.makeText(ShopJoin.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                    list.remove(position);
                                    if (list.size()>0){
                                        no_data_img.setVisibility(View.GONE);
                                        no_data_txt.setVisibility(View.GONE);
                                        SimpleAdapter adapter = new SimpleAdapter(ShopJoin.this, list, R.layout.list_view_shop_join,
                                                new String[]{"shopName", "shop_type", "teleNumber", "ownerName", "shop_addr"},
                                                new int[]{R.id.list_view_shop_join_release_shop_name, R.id.list_view_shop_join_release_shop_type,
                                                        R.id.list_view_shop_join_release_manager_name, R.id.list_view_shop_join_release_tel,
                                                        R.id.list_view_shop_join_release_shop_addr});
                                        list_view.setAdapter(adapter);
                                        list_view.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        list_view.setVisibility(View.GONE);
                                        no_data_img.setVisibility(View.VISIBLE);
                                        no_data_txt.setVisibility(View.VISIBLE);
                                        Toast.makeText(ShopJoin.this, "还没有店铺加盟信息", Toast.LENGTH_SHORT).show();
                                        sleep_and_finish(2);
                                    }
                                } else {
                                    Log.e("ShopJoin_Activity", ShopJoin.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(ShopJoin.this, ShopJoin.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("ShopJoin_Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                Log.e("ShopJoin：reCode", reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(ShopJoin.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    Toast.makeText(ShopJoin.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                    list.remove(position);
                                    if (list.size()>0){
                                        no_data_img.setVisibility(View.GONE);
                                        no_data_txt.setVisibility(View.GONE);
                                        SimpleAdapter adapter = new SimpleAdapter(ShopJoin.this, list, R.layout.list_view_shop_join,
                                                new String[]{"shopName", "shop_type", "teleNumber","ownerName",  "shop_addr"},
                                                new int[]{R.id.list_view_shop_join_release_shop_name, R.id.list_view_shop_join_release_shop_type,
                                                        R.id.list_view_shop_join_release_manager_name, R.id.list_view_shop_join_release_tel,
                                                        R.id.list_view_shop_join_release_shop_addr});
                                        list_view.setAdapter(adapter);
                                        list_view.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        list_view.setVisibility(View.GONE);
                                        no_data_img.setVisibility(View.VISIBLE);
                                        no_data_txt.setVisibility(View.VISIBLE);
                                        Toast.makeText(ShopJoin.this, "还没有店铺加盟信息", Toast.LENGTH_SHORT).show();
                                        sleep_and_finish(2);
                                    }
                                } else {
                                    Log.e("ShopJoin_Activity", ShopJoin.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(ShopJoin.this, ShopJoin.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("ShopJoin_Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("ShopJoin_Activity", ShopJoin.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

        new Thread(){
            @Override
            public void run() {
                super.run();
                PostParameter[] params = new PostParameter[2];
                params[0] = new PostParameter("account", spu.getAccount());
                params[1] = new PostParameter("cookie", spu.getCookie());
                String reCode = ConnectUtil.httpRequest(ConnectUtil.SHOP_JOIN_LIST, params, ConnectUtil.POST);
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
        if (175==resultCode){
            position = data.getIntExtra("position", 0);
            switch (data.getStringExtra("action")){
                case "contact" :
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                                Constants.ActivityCompatRequestPermissionsCode);
                        return;
                    }
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + list.get(position).get("teleNumber").substring(3))));
                    break;
                case "agree" :
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            PostParameter[] params = new PostParameter[3];
                            params[0] = new PostParameter("account", spu.getAccount());
                            params[1] = new PostParameter("shopAccount", list.get(position).get("shopAccount"));
                            params[2] = new PostParameter("cookie", spu.getCookie());
                            String reCode = ConnectUtil.httpRequest(ConnectUtil.AGREE_JOIN, params, ConnectUtil.POST);
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
                            PostParameter[] params = new PostParameter[3];
                            params[0] = new PostParameter("account", spu.getAccount());
                            params[1] = new PostParameter("shopAccount", list.get(position).get("shopAccount"));
                            params[2] = new PostParameter("cookie", spu.getCookie());
                            String reCode = ConnectUtil.httpRequest(ConnectUtil.REFUSE_JOIN, params, ConnectUtil.POST);
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
