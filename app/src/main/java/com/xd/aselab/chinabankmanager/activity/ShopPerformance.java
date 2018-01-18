package com.xd.aselab.chinabankmanager.activity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopPerformance extends AppCompatActivity {

    private RelativeLayout back;
    private TextView select_time;
    private ListView list_view;
    private ImageView no_data_img;
    private TextView no_data_txt;

    private SharePreferenceUtil spu;
    private Calendar calendar;
    private Handler handler;
    private SimpleDateFormat format;
    private String[] select_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_performance);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        spu = new SharePreferenceUtil(ShopPerformance.this, "user");
        calendar = Calendar.getInstance();
        format = new SimpleDateFormat("yyyy-MM-dd");

        back = (RelativeLayout) findViewById(R.id.act_shop_perf_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list_view = (ListView) findViewById(R.id.act_shop_perf_list_view);
        no_data_img = (ImageView) findViewById(R.id.act_shop_perf_no_data_img);
        no_data_txt = (TextView) findViewById(R.id.act_shop_perf_no_data_txt);

        select_string = new String[]{"近一周业绩", "近一月业绩", "近一季度业绩", "近一年业绩"};
        select_time = (TextView) findViewById(R.id.act_shop_perf_select_time);
        select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShopPerformance.this);
                builder.setTitle("选择时间范围");
                builder.setItems(select_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        select_time.setText(select_string[which]);
                        final PostParameter[] params = new PostParameter[6];
                        params[0] = new PostParameter("account", spu.getAccount());
                        params[1] = new PostParameter("branchLevel4", spu.getBranchLevel4());
                        params[2] = new PostParameter("type", "0");
                        calendar.setTime(new Date());
                        switch (select_string[which]){
                            case "近一周业绩" :
                                //calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
                                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                                params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                                params[4] = new PostParameter("end", format.format(new Date()));
                                break;
                            case "近一月业绩" :
                               // calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                                calendar.set(Calendar.DAY_OF_MONTH,1);
                                params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                                params[4] = new PostParameter("end", format.format(new Date()));
                                break;
                            case "近一季度业绩" :
                                //calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 3);
                                int currentMonth = calendar.get(Calendar.MONTH) + 1;
                                if (currentMonth >= 1 && currentMonth <= 3)
                                    calendar.set(Calendar.MONTH, 0);
                                else if (currentMonth >= 4 && currentMonth <= 6)
                                    calendar.set(Calendar.MONTH, 3);
                                else if (currentMonth >= 7 && currentMonth <= 9)
                                    calendar.set(Calendar.MONTH, 6);
                                else if (currentMonth >= 10 && currentMonth <= 12)
                                    calendar.set(Calendar.MONTH, 9);
                                calendar.set(Calendar.DAY_OF_MONTH,1);
                                params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                                params[4] = new PostParameter("end", format.format(new Date()));
                                break;
                            case "近一年业绩" :
                                //calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
                                calendar.set(Calendar.MONTH, 0);
                                calendar.set(Calendar.DAY_OF_MONTH,1);
                                params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                                params[4] = new PostParameter("end", format.format(new Date()));
                                break;
                        }
                        params[5] = new PostParameter("cookie", spu.getCookie());
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                String reCode = ConnectUtil.httpRequest(ConnectUtil.MY_PERFORMANCE, params, ConnectUtil.POST);
                                Message msg = new Message();
                                msg.what = 0;
                                msg.obj = reCode;
                                handler.sendMessage(msg);
                            }
                        }.start();
                    }
                });
                builder.show();
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
                                //Log.e("ShopPerform：reCode", reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(ShopPerformance.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONArray jsonArray = json.getJSONArray("list");
                                    if (jsonArray.length()==0){
                                        list_view.setVisibility(View.GONE);
                                        no_data_img.setVisibility(View.VISIBLE);
                                        no_data_txt.setVisibility(View.VISIBLE);
                                        Toast.makeText(ShopPerformance.this, "还没有店铺业绩信息", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        //Log.e("jsonArray",jsonArray.toString());
                                        no_data_img.setVisibility(View.GONE);
                                        no_data_txt.setVisibility(View.GONE);
                                        List<Map<String, String>> list = new ArrayList<>();
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject temp = (JSONObject) jsonArray.get(i);
                                            Map<String, String> map = new HashMap<>();
                                            map.put("shopName", temp.getString("shopName"));
                                            //map.put("ownerName", "店铺负责人："+temp.getString("shopAccount"));
                                            map.put("shopRelease", temp.getString("status"));
                                            map.put("ownerName", "");
                                            map.put("sum_card_count", "店铺扫码人数："+temp.getString("sum_card_count"));
                                            map.put("success_sum", "成功办卡人数："+temp.getString("success_sum"));
                                            list.add(map);
                                        }
                                        SimpleAdapter adapter = new SimpleAdapter(ShopPerformance.this, list, R.layout.list_view_shop_perf,
                                                new String[]{"shopName", "shopRelease", "ownerName", "sum_card_count", "success_sum"},
                                                new int[]{R.id.list_view_shop_perf_shop_name, R.id.list_view_shop_perf_release,
                                                        R.id.list_view_shop_perf_manager_name,
                                                        R.id.list_view_shop_perf_saoma, R.id.list_view_shop_perf_banka });
                                        list_view.setAdapter(adapter);
                                        list_view.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    Log.e("ShopPerform_Activity", ShopPerformance.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(ShopPerformance.this, ShopPerformance.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("ShopPerform_Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("ShopPerform_Activity", ShopPerformance.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

        new Thread(){
            @Override
            public void run() {
                super.run();
                PostParameter[] params = new PostParameter[6];
                params[0] = new PostParameter("account", spu.getAccount());
                params[1] = new PostParameter("branchLevel4", spu.getBranchLevel4());
                params[2] = new PostParameter("type", "0");
                calendar.setTime(new Date());
               // calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                params[4] = new PostParameter("end", format.format(new Date()));
                params[5] = new PostParameter("cookie", spu.getCookie());
                String reCode = ConnectUtil.httpRequest(ConnectUtil.MY_PERFORMANCE, params, ConnectUtil.POST);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();
    }
}
