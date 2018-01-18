package com.xd.aselab.chinabankmanager.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

public class PerformanceDetail extends AppCompatActivity {

    private RelativeLayout back;
    private ListView list_view;
    private TextView title;
    private TextView name;
    private TextView job_number;
    private TextView red_total;

    private String time;
    private String account;
    private String saoma;
    private String banka;

    private SharePreferenceUtil spu;
    private Calendar calendar;
    private SimpleDateFormat format;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        final Intent intent = getIntent();
        time = intent.getStringExtra("title");
        account = intent.getStringExtra("account");
        saoma = intent.getStringExtra("saoma");
        banka = intent.getStringExtra("banka");

        name = (TextView) findViewById(R.id.act_perf_detail_name);
        name.setText(intent.getStringExtra("real_name"));

        job_number = (TextView) findViewById(R.id.act_perf_detail_job_number);
        job_number.setText("工号："+intent.getStringExtra("job_number"));

        title = (TextView) findViewById(R.id.act_perf_detail_title);
        title.setText(time);

        spu = new SharePreferenceUtil(PerformanceDetail.this, "user");
        calendar = Calendar.getInstance();
        format = new SimpleDateFormat("yyyy-MM-dd");

        back = (RelativeLayout) findViewById(R.id.act_perf_detail_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        red_total = (TextView) findViewById(R.id.act_perf_detail_red_total);
        list_view = (ListView) findViewById(R.id.act_perf_detail_list_view);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0 :
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                Log.e("PerformDetail：reCode", reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(PerformanceDetail.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONArray jsonArray = json.getJSONArray("list");
                                    if (jsonArray.length()==0){
                                        Log.e("PerformDetail_Activity", PerformanceDetail.this.getResources().getString(R.string.list_length_exception));
                                    }
                                    else {
                                        List<Map<String, String>> list = new ArrayList<>();
                                        //int total = 0;
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject temp = (JSONObject) jsonArray.get(i);
                                            Map<String, String> map = new HashMap<>();
                                            map.put("shopName", temp.getString("shopName"));
                                            map.put("saoma", "店铺扫码人数："+temp.getString("sum_card_count"));
                                            map.put("banka", "店铺办卡成功人数："+temp.getString("success_sum"));
                                            //total += Integer.valueOf(temp.getString("success_sum"));
                                            list.add(map);
                                        }
                                        SimpleAdapter adapter = new SimpleAdapter(PerformanceDetail.this, list, R.layout.list_view_performance_detail,
                                                new String[]{"shopName","saoma","banka"},
                                                new int[]{R.id.list_view_perf_detail_shop_name, R.id.list_view_perf_detail_saoma, R.id.list_view_perf_detail_banka});
                                        list_view.setAdapter(adapter);
                                        red_total.setText("在"+intent.getStringExtra("real_name")+"管理的" + jsonArray.length()+"家店铺中，共有"+saoma+"人扫码，"+banka+"人成功办卡");
                                    }
                                } else {
                                    Log.e("PerformDetail_Activity", PerformanceDetail.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(PerformanceDetail.this, PerformanceDetail.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("PerformDetail_Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("PerformDetail_Activity", PerformanceDetail.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

        new Thread(){
            @Override
            public void run() {
                super.run();
                PostParameter[] params = new PostParameter[6];
                params[0] = new PostParameter("account", account);
                params[1] = new PostParameter("type", "0");
                calendar.setTime(new Date());
                switch (time){
                    case "近一周业绩" :
                        //calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                        params[2] = new PostParameter("begin", format.format(calendar.getTime()));
                        params[3] = new PostParameter("end", format.format(new Date()));
                        break;
                    case "近一月业绩" :
                        //calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                        calendar.set(Calendar.DAY_OF_MONTH,1);
                        params[2] = new PostParameter("begin", format.format(calendar.getTime()));
                        params[3] = new PostParameter("end", format.format(new Date()));
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
                        params[2] = new PostParameter("begin", format.format(calendar.getTime()));
                        params[3] = new PostParameter("end", format.format(new Date()));
                        break;
                    case "近一年业绩" :
                        //calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
                        calendar.set(Calendar.MONTH, 0);
                        calendar.set(Calendar.DAY_OF_MONTH,1);
                        params[2] = new PostParameter("begin", format.format(calendar.getTime()));
                        params[3] = new PostParameter("end", format.format(new Date()));
                        break;
                }
                params[4] = new PostParameter("branchLevel4", spu.getBranchLevel4());
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
