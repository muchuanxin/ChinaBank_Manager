package com.xd.aselab.chinabankmanager.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class BasePerformance extends AppCompatActivity {

    private RelativeLayout back;
    private ImageView chart;
    private TextView select_time;
    //private TextView red_total;
    private ListView list_view;
    private ImageView no_data_img;
    private TextView no_data_txt;

    private List<Map<String, String>> list;
    private ArrayAdapter<String> select_adapter;
    private SimpleAdapter adapter;
    private SharePreferenceUtil spu;
    private Handler handler;
    private SimpleDateFormat format;
    private Calendar calendar;
    private String time;
    private String[] select_string;

    //private String[] names;
    //private float[] numbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_performance);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        spu = new SharePreferenceUtil(BasePerformance.this, "user");
        format = new SimpleDateFormat("yyyy-MM-dd");
        calendar = Calendar.getInstance();

        back = (RelativeLayout) findViewById(R.id.act_base_perf_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*chart = (ImageView) findViewById(R.id.act_base_perf_chart);
        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list_view.getVisibility() == View.VISIBLE) {
                    Intent intent = new Intent();
                    intent.setClass(BasePerformance.this, PerformanceChart.class);
                    intent.putExtra("names", names);
                    intent.putExtra("numbers", numbers);
                    startActivity(intent);
                } else if (no_data_img.getVisibility() == View.VISIBLE) {
                    Toast.makeText(BasePerformance.this, "还没有基层经理业绩信息", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        select_string = new String[]{"近一周业绩", "近一月业绩", "近三月业绩", "近一年业绩"};
        //select_adapter = new ArrayAdapter<>(this, R.layout.textview_for_spinner, select_string);
        //select_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select_time = (TextView) findViewById(R.id.act_base_perf_select_time);
        select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivityForResult(new Intent().setClass(PerformanceRanking.this, PerfRankTransparent.class),102);
                AlertDialog.Builder builder = new AlertDialog.Builder(BasePerformance.this);
                builder.setTitle("选择时间范围");
                builder.setItems(select_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        select_time.setText(select_string[which]);
                        final PostParameter[] params = new PostParameter[6];
                        params[0] = new PostParameter("account", spu.getAccount());
                        params[1] = new PostParameter("branchLevel4", spu.getBranchLevel4());
                        params[2] = new PostParameter("type", "1");
                        calendar.setTime(new Date());
                        switch (select_string[which]) {
                            case "近一周业绩":
                                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
                                params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                                params[4] = new PostParameter("end", format.format(new Date()));
                                break;
                            case "近一月业绩":
                                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                                params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                                params[4] = new PostParameter("end", format.format(new Date()));
                                break;
                            case "近三月业绩":
                                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 3);
                                params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                                params[4] = new PostParameter("end", format.format(new Date()));
                                break;
                            case "近一年业绩":
                                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
                                params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                                params[4] = new PostParameter("end", format.format(new Date()));
                                break;
                        }
                        params[5] = new PostParameter("cookie", spu.getCookie());
                        new Thread() {
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


        list_view = (ListView) findViewById(R.id.act_base_perf_list_view);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ( Integer.valueOf(list.get(position).get("success_sum").substring(7)).intValue() >0 ){
                    Intent intent = new Intent();
                    intent.setClass(BasePerformance.this, PerformanceDetail.class);
                    intent.putExtra("title", select_time.getText());
                    intent.putExtra("account", list.get(position).get("manager_account"));
                    intent.putExtra("real_name", list.get(position).get("manager_realname"));
                    intent.putExtra("job_number", list.get(position).get("manager_jobnumber"));
                    intent.putExtra("saoma", list.get(position).get("sum_card_count").substring(5));
                    intent.putExtra("banka", list.get(position).get("success_sum").substring(7));
                    startActivity(intent);
                }
            }
        });

        //red_total = (TextView) findViewById(R.id.act_base_perf_red_total);
        no_data_img = (ImageView) findViewById(R.id.act_base_perf_no_data_img);
        no_data_txt = (TextView) findViewById(R.id.act_base_perf_no_data_txt);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0 :
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                Log.e("BasePerformance：reCode", ""+reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(BasePerformance.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONArray jsonArray = json.getJSONArray("list");
                                    if (jsonArray.length()==0){
                                        list_view.setVisibility(View.GONE);
                                        //red_total.setVisibility(View.GONE);
                                        no_data_img.setVisibility(View.VISIBLE);
                                        no_data_txt.setVisibility(View.VISIBLE);
                                        Toast.makeText(BasePerformance.this, "还没有基层经理业绩信息", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        no_data_img.setVisibility(View.GONE);
                                        no_data_txt.setVisibility(View.GONE);
                                        //names = new String[jsonArray.length()];
                                        //numbers = new float[jsonArray.length()];
                                        list = new ArrayList<>();
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject temp = (JSONObject) jsonArray.get(i);
                                            Map<String, String> map = new HashMap<>();
                                            map.put("manager_account", temp.getString("manager_account"));
                                            map.put("manager_jobnumber", temp.getString("manager_jobnumber"));
                                            map.put("manager_realname", temp.getString("manager_realname"));
                                            map.put("sum_card_count", "扫码人数："+temp.getString("sum_card_count"));
                                            map.put("success_sum", "成功办卡人数："+temp.getString("success_sum"));
                                            //names[i] = temp.getString("manager_realname");
                                            //numbers[i] = Float.valueOf(temp.getString("sum_card_count")).floatValue();
                                            list.add(map);
                                        }
                                        adapter = new SimpleAdapter(BasePerformance.this, list, R.layout.list_view_base_performance,
                                                new String[]{"manager_realname", "sum_card_count", "success_sum"},
                                                new int[]{R.id.list_view_base_perf_name, R.id.list_view_base_perf_saoma, R.id.list_view_base_perf_banka});
                                        list_view.setAdapter(adapter);
                                        list_view.setVisibility(View.VISIBLE);
                                        //red_total.setText(time + "共有" + total+"人成功办卡");
                                        //red_total.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    Log.e("BasePerform_Activity", BasePerformance.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(BasePerformance.this, BasePerformance.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("BasePerform_Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("BasePerform_Activity", BasePerformance.this.getResources().getString(R.string.handler_what_exception));
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
                params[2] = new PostParameter("type", "1");
                calendar.setTime(new Date());
                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
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

    /*@Override
    protected  void onActivityResult(int requestCode, int resultCode, final Intent intent){
        if(123==resultCode){
            final PostParameter[] params = new PostParameter[4];
            params[0] = new PostParameter("account", spu.getAccount());
            params[1] = new PostParameter("type", "1");
            calendar.setTime(new Date());
            switch (intent.getStringExtra("time")){
                case "week" :
                    time = "近一周内";
                    //select_time.setText("近一周业绩");
                    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
                    params[2] = new PostParameter("begin", format.format(calendar.getTime()));
                    params[3] = new PostParameter("end", format.format(new Date()));
                    break;
                case "one_month" :
                    time = "近一月内";
                    // select_time.setText("近一月业绩");
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                    params[2] = new PostParameter("begin", format.format(calendar.getTime()));
                    params[3] = new PostParameter("end", format.format(new Date()));
                    break;
                case "three_month" :
                    time = "近三月内";
                    // select_time.setText("近三月业绩");
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 3);
                    params[2] = new PostParameter("begin", format.format(calendar.getTime()));
                    params[3] = new PostParameter("end", format.format(new Date()));
                    break;
                case "year" :
                    time = "近一年内";
                    //select_time.setText("近一年业绩");
                    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
                    params[2] = new PostParameter("begin", format.format(calendar.getTime()));
                    params[3] = new PostParameter("end", format.format(new Date()));
                    break;
                case "total" :
                    time = "总";
                    //select_time.setText("累计业绩");
                    params[2] = new PostParameter("begin", "2000-01-01");
                    params[3] = new PostParameter("end", "3000-12-31");
                    break;
            }
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
        super.onActivityResult(requestCode, resultCode, intent);
    }*/
}
