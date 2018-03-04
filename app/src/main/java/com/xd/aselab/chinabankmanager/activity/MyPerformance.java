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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.fragment.HistogramView;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyPerformance extends AppCompatActivity {

    private RelativeLayout back;
    //private ImageView chart;
    private TextView select_time;
    private TextView line1;
    private TextView line2;
    private TextView line3;
    private TextView gray_bar;
    private RelativeLayout chart;
    private ListView list_view;
    private ImageView no_data_img;
    private TextView no_data_txt;

    private ArrayAdapter<String> select_adapter;
    private SimpleAdapter adapter;
    private SharePreferenceUtil spu;
    private Handler handler;
    private SimpleDateFormat format;
    private Calendar calendar;
    private String time;
    private String[] names;
    private float[] numbers;
    private String[] select_string;
    private int type;

    /*private TextView start_time_txt;
    private TextView end_time_txt;

    private Calendar start_calendar;
    private Calendar end_calendar;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_performance);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        Intent intent = getIntent();
        type = intent.getIntExtra("type",0);

        spu = new SharePreferenceUtil(MyPerformance.this, "user");
        format = new SimpleDateFormat("yyyy-MM-dd");
        calendar = Calendar.getInstance();

        back = (RelativeLayout) findViewById(R.id.act_my_perf_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*chart = (ImageView) findViewById(R.id.act_my_perf_chart);
        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list_view.getVisibility()==View.VISIBLE){
                    Intent intent = new Intent();
                    intent.setClass(MyPerformance.this, PerformanceChart.class);
                    intent.putExtra("names", names);
                    intent.putExtra("numbers",numbers);
                    startActivity(intent);
                }
                else if (no_data_img.getVisibility()==View.VISIBLE){
                    Toast.makeText(MyPerformance.this, "还没有银行卡客户经理业绩信息", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        select_string = new String[]{"近一周业绩", "近一月业绩", "近一季度业绩", "近一年业绩"};
        //select_adapter = new ArrayAdapter<>(this, R.layout.textview_for_spinner, select_string);
        //select_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select_time = (TextView) findViewById(R.id.act_my_perf_select_time);
        select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivityForResult(new Intent().setClass(PerformanceRanking.this, PerfRankTransparent.class),102);
                AlertDialog.Builder builder = new AlertDialog.Builder(MyPerformance.this);
                builder.setTitle("选择时间范围");
                builder.setItems(select_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        select_time.setText(select_string[which]);
                        final PostParameter[] params = new PostParameter[6];
                        params[0] = new PostParameter("account", spu.getAccount());
                        params[1] = new PostParameter("branchLevel4", spu.getBranchLevel4());
                        params[2] = new PostParameter("type", ""+type);
                        calendar.setTime(new Date());
                        switch (select_string[which]){
                            case "近一周业绩" :
                                //calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
                                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                                params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                                params[4] = new PostParameter("end", format.format(new Date()));
                                break;
                            case "近一月业绩" :
                                //calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
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

        /*select_time.setAdapter(select_adapter);
        select_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final PostParameter[] params = new PostParameter[5];
                params[0] = new PostParameter("account", spu.getAccount());
                params[1] = new PostParameter("branchLevel4", spu.getBranchLevel4());
                params[2] = new PostParameter("type", "1");
                calendar.setTime(new Date());
                switch (parent.getItemAtPosition(position).toString()){
                    case "近一周业绩" :
                        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
                        params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                        params[4] = new PostParameter("end", format.format(new Date()));
                        break;
                    case "近一月业绩" :
                        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                        params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                        params[4] = new PostParameter("end", format.format(new Date()));
                        break;
                    case "近三月业绩" :
                        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 3);
                        params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                        params[4] = new PostParameter("end", format.format(new Date()));
                        break;
                    case "近一年业绩" :
                        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
                        params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                        params[4] = new PostParameter("end", format.format(new Date()));
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

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        /*start_calendar = Calendar.getInstance();
        end_calendar = Calendar.getInstance();

        start_time_txt = (TextView) findViewById(R.id.act_my_perf_start_time);
        start_time_txt.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        start_time_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MyPerformance.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                start_calendar.set(year, monthOfYear, dayOfMonth);
                                DecimalFormat mFormat = new DecimalFormat("00");
                                start_time_txt.setText(year + "-" + mFormat.format(monthOfYear + 1) + "-" + mFormat.format(dayOfMonth));
                            }
                        },
                        start_calendar.get(Calendar.YEAR),
                        start_calendar.get(Calendar.MONTH),
                        start_calendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        end_time_txt = (TextView) findViewById(R.id.act_my_perf_end_time);
        end_time_txt.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        end_time_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MyPerformance.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                end_calendar.set(year, monthOfYear, dayOfMonth);
                                DecimalFormat mFormat = new DecimalFormat("00");
                                end_time_txt.setText(year + "-" + mFormat.format(monthOfYear + 1) + "-" + mFormat.format(dayOfMonth));
                            }
                        },
                        end_calendar.get(Calendar.YEAR),
                        end_calendar.get(Calendar.MONTH),
                        end_calendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });*/

        /*list_view = (ListView) findViewById(R.id.act_my_perf_list_view);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(MyPerformance.this, PerformanceDetail.class);
                //intent.putExtra("title", select_time.getText().toString());
                intent.putExtra("account", ((Map<String, String>) adapter.getItem(position)).get("manager_account"));
                intent.putExtra("real_name", ( (Map<String, String>) adapter.getItem(position) ).get("manager_realname").substring(3));
                intent.putExtra("job_number", ( (Map<String, String>) adapter.getItem(position) ).get("manager_jobnumber"));
                startActivity(intent);
            }
        });*/

        line1 = (TextView) findViewById(R.id.act_my_perf_red_total_line1);
        line2 = (TextView) findViewById(R.id.act_my_perf_red_total_line2);
        line3 = (TextView) findViewById(R.id.act_my_perf_red_total_line3);
        gray_bar = (TextView) findViewById(R.id.act_my_perf_gray_bar);
        gray_bar.setText(Calendar.getInstance().get(Calendar.YEAR)+"年各月办卡成功情况分析");
        chart = (RelativeLayout) findViewById(R.id.act_my_perf_chart);
        no_data_img = (ImageView) findViewById(R.id.act_my_perf_no_data_img);
        no_data_txt = (TextView) findViewById(R.id.act_my_perf_no_data_txt);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0 :
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                Log.e("MyPerformance：reCode", ""+reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(MyPerformance.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONObject this_year = json.getJSONObject("this_year");
                                    //Log.e("this_year", this_year.toString());
                                    String[] names = new String[12];
                                    float[] numbers = new float[12];
                                    boolean flag = false;
                                    for (int i=0; i<12; i++){
                                        names[i] = new String(""+(i+1));
                                        numbers[i] = this_year.getInt(""+(i+1)+"月");
                                        if (numbers[i]>0){
                                            flag = true;
                                        }
                                    }
                                    /*JSONArray jsonArray = json.getJSONArray("list");
                                    int saoma_count = 0;
                                    int banka_count = 0;
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject temp = (JSONObject) jsonArray.get(i);
                                        saoma_count += Integer.valueOf(temp.getString("sum_card_count"));
                                        banka_count += Integer.valueOf(temp.getString("success_sum"));
                                    }*/
                                    line1.setText("共有"+json.getString("sumCount")+"人扫码，共有"+json.getString("success_sum")+"人成功办卡");
                                    if ("BASIC".equals(spu.getType())){
                                        line2.setText("发展商户排名："+("".equals(json.getString("shop_rank"))? "暂无" : "第"+json.getString("shop_rank")+"名"));
                                        line3.setText("成功办卡业绩排名："+("".equals(json.getString("cards_rank"))? "暂无" : "第"+json.getString("cards_rank")+"名"));
                                        line2.setVisibility(View.VISIBLE);
                                        line3.setVisibility(View.VISIBLE);
                                    }
                                    if (! flag){
                                        //list_view.setVisibility(View.GONE);
                                        gray_bar.setVisibility(View.GONE);
                                        chart.setVisibility(View.GONE);
                                        no_data_img.setVisibility(View.VISIBLE);
                                        no_data_txt.setVisibility(View.VISIBLE);
                                        Toast.makeText(MyPerformance.this, "还没有银行卡客户经理业绩信息", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        no_data_img.setVisibility(View.GONE);
                                        no_data_txt.setVisibility(View.GONE);
                                        HistogramView histogramView = new HistogramView(MyPerformance.this, names, numbers);
                                        chart.addView(histogramView);
                                        gray_bar.setVisibility(View.VISIBLE);
                                        chart.setVisibility(View.VISIBLE);
                                        /*names = new String[jsonArray.length()];
                                        numbers = new float[jsonArray.length()];
                                        List<Map<String, String>> list = new ArrayList<>();
                                        int total = 0;
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject temp = (JSONObject) jsonArray.get(i);
                                            Map<String, String> map = new HashMap<>();
                                            map.put("manager_account", temp.getString("manager_account"));
                                            map.put("manager_jobnumber", temp.getString("manager_jobnumber"));
                                            map.put("manager_realname", "经理："+temp.getString("manager_realname"));
                                            map.put("sum_card_count", "业绩："+temp.getString("sum_card_count"));
                                            total += Integer.valueOf(temp.getString("sum_card_count"));
                                            names[i] = temp.getString("manager_realname");
                                            numbers[i] = Float.valueOf(temp.getString("sum_card_count")).floatValue();
                                            list.add(map);
                                        }
                                        adapter = new SimpleAdapter(MyPerformance.this, list, R.layout.list_view_my_performance,
                                                new String[]{"manager_realname","sum_card_count"},
                                                new int[]{R.id.list_view_my_perf_name, R.id.list_view_my_perf_performance});
                                        list_view.setAdapter(adapter);
                                        list_view.setVisibility(View.VISIBLE);*/
                                    }
                                } else {
                                    Log.e("MyPerformance_Activity", MyPerformance.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(MyPerformance.this, MyPerformance.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("MyPerformance_Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("MyPerformance_Activity", MyPerformance.this.getResources().getString(R.string.handler_what_exception));
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
                params[2] = new PostParameter("type", ""+type);
                calendar.setTime(new Date());
                //calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                params[4] = new PostParameter("end", format.format(new Date()));
                params[5] = new PostParameter("cookie", spu.getCookie());
                String reCode = ConnectUtil.httpRequest(ConnectUtil.MY_PERFORMANCE, params, ConnectUtil.POST);
                Log.e("reCode",""+reCode);
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