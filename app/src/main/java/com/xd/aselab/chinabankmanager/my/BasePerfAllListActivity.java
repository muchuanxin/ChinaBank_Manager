package com.xd.aselab.chinabankmanager.my;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.kafenqi.manager.adapter.BasePerfAllAdapter;
import com.xd.aselab.chinabankmanager.util.ChooseTimeAdapter;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.Constants;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BasePerfAllListActivity extends AppCompatActivity {

    private RelativeLayout back;
    private TextView select_time;
    private ListView listView;
    private LinearLayout no_data;
    private SharePreferenceUtil spu;
    private  String[] select_string;
    private Handler handler;
    private SimpleDateFormat format;
    private Calendar calendar;
    private String choosen_time="one_week";
    private List<Map<String, String>> list_card = new ArrayList<>();
    private List<Map<String, String>> list_fenqi_one_week = new ArrayList<>();
    private List<Map<String, String>> list_fenqi_one_month = new ArrayList<>();
    private List<Map<String, String>> list_fenqi_three_month = new ArrayList<>();
    private List<Map<String, String>> list_fenqi_one_year = new ArrayList<>();
    private List<Map<String, String>> list_xiaodai_one_week = new ArrayList<>();
    private List<Map<String, String>> list_xiaodai_one_month = new ArrayList<>();
    private List<Map<String, String>> list_xiaodai_three_month = new ArrayList<>();
    private List<Map<String, String>> list_xiaodai_one_year = new ArrayList<>();
    private List<Map<String, String>> list_all = new ArrayList<>();
    private boolean card_status=false;
    private boolean fenqi_stutas=false;
    private boolean xiaodai_stutas=false;
    private BasePerfAllAdapter adapter;
    private ChooseTimeAdapter chooseTimeAdapter;
    private PopupWindow pop;
    private RelativeLayout rl_choose_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_perf_all_list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initViews();
        initDatas();
        getAllDatas();
        parseDatas();
        initEvents();

    }

    void initViews(){
        back = (RelativeLayout)findViewById(R.id.act_base_perf_all_list_back_btn);
        select_time = (TextView)findViewById(R.id.act_base_perf_all_list_select_time);
        listView = (ListView)findViewById(R.id.base_perf_all_list_listView);
        no_data = (LinearLayout)findViewById(R.id.act_base_perf_all_list_no_data);
        rl_choose_time = (RelativeLayout) findViewById(R.id.rl_my_perf_select_time);
    }

    void initDatas(){
        spu = new SharePreferenceUtil(BasePerfAllListActivity.this,"user");
        select_string = new String[]{"近一周业绩", "近一月业绩", "近三月业绩", "近一年业绩"};
        format = new SimpleDateFormat("yyyy-MM-dd");
        calendar = Calendar.getInstance();
        chooseTimeAdapter = new ChooseTimeAdapter(Constants.chooseTimeList,BasePerfAllListActivity.this);
    }

    void getAllDatas(){

        rl_choose_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow();
                //startActivityForResult(new Intent().setClass(PerformanceRanking.this, PerfRankTransparent.class),102);
                /*AlertDialog.Builder builder = new AlertDialog.Builder(BasePerfAllListActivity.this);
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
                        switch (select_string[which]){
                            case "近一周业绩" :
                                choosen_time="one_week";
                                *//*line1.setText("分期业务成功数量："+("".equals(number1)? "暂无" : number1+"笔"));
                                line2.setText("分期业务成功金额："+("".equals(money1)? "暂无" : money1+"元"));*//*
                                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
                                params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                                params[4] = new PostParameter("end", format.format(new Date()));
                                break;
                            case "近一月业绩" :
                                choosen_time="one_month";
                            //    setFenqiData(number2_fenqi,money2_fenqi);
                                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                                params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                                params[4] = new PostParameter("end", format.format(new Date()));
                                break;
                            case "近三月业绩" :
                                choosen_time="three_month";
                             //   setFenqiData(number3_fenqi,money3_fenqi);
                                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 3);
                                params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                                params[4] = new PostParameter("end", format.format(new Date()));
                                break;
                            case "近一年业绩" :
                                choosen_time="one_year";
                            //    setFenqiData(number4_fenqi,money4_fenqi);
                                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
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
                builder.show();*/
            }
        });

        new Thread(){
            @Override
            public void run() {
                super.run();
                final PostParameter[] params = new PostParameter[6];
                params[0] = new PostParameter("account", spu.getAccount());
                params[1] = new PostParameter("branchLevel4", spu.getBranchLevel4());
                params[2] = new PostParameter("type", "1");
                calendar.setTime(new Date());
                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
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

        new Thread(){
            @Override
            public void run() {
                super.run();
                final PostParameter[] params = new PostParameter[1];
                params[0] = new PostParameter("account", spu.getAccount());
                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetInstallmentBasicPerformance, params, ConnectUtil.POST);
                Log.e("reCode-fenqi",""+reCode);
                Message msg = new Message();
                msg.what = 1;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                super.run();
                final PostParameter[] params = new PostParameter[1];
                params[0] = new PostParameter("account", spu.getAccount());
                //个人消贷的接口
                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetLoanBasicPerformance, params, ConnectUtil.POST);
                Log.e("reCode-loan",""+reCode);
                Message msg = new Message();
                msg.what = 2;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();

    }

    void parseDatas(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0 :
                        card_status=true;
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(BasePerfAllListActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    list_card.clear();
                                    JSONArray jsonArray = json.getJSONArray("list");
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject temp = (JSONObject) jsonArray.get(i);
                                            Map<String, String> map = new HashMap<>();
                                            map.put("manager_account", temp.getString("manager_account"));
                                            map.put("manager_jobnumber", temp.getString("manager_jobnumber"));
                                            map.put("manager_realname", temp.getString("manager_realname"));
                                            map.put("sum_card_count", temp.getString("sum_card_count"));
                                            map.put("success_sum", temp.getString("success_sum"));
                                            list_card.add(map);
                                        }

                                        if(card_status&&fenqi_stutas&&xiaodai_stutas){
                                            switch (choosen_time){
                                                case "one_week":
                                                    list_card.addAll(list_fenqi_one_week);
                                                    list_card.addAll(list_xiaodai_one_week);
                                                    break;
                                                case "one_month":
                                                    list_card.addAll(list_fenqi_one_month);
                                                    list_card.addAll(list_xiaodai_one_month);
                                                    break;
                                                case "three_month":
                                                    list_card.addAll(list_fenqi_three_month);
                                                    list_card.addAll(list_xiaodai_three_month);
                                                    break;
                                                case "one_year":
                                                    list_card.addAll(list_fenqi_one_year);
                                                    list_card.addAll(list_xiaodai_one_year);
                                                    break;
                                            }
                                            if(list_card.size()==0){
                                                listView.setVisibility(View.GONE);
                                                no_data.setVisibility(View.VISIBLE);
                                            }else {
                                                listView.setVisibility(View.VISIBLE);
                                                no_data.setVisibility(View.GONE);
                                                list_all = transform(list_card);
                                                adapter = new BasePerfAllAdapter(list_all,BasePerfAllListActivity.this);
                                                listView.setAdapter(adapter);
                                            }
                                            card_status=false;
                                        }


                                } else {
                                    Log.e("BasePerform_Activity", BasePerfAllListActivity.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                //Toast.makeText(BasePerfAllListActivity.this, BasePerfAllListActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("BasePerform_Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;

                    case 1 :
                        fenqi_stutas=true;
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                //worker_list_one_week = new ArrayList<>();
                                if ("false".equals(status)) {
                                    Toast.makeText(BasePerfAllListActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONArray oneWeekJA = json.getJSONArray("one_week");
                                    JSONArray oneMonthJA = json.getJSONArray("one_month");
                                    JSONArray threeMonthJA = json.getJSONArray("three_month");
                                    JSONArray oneYearJA = json.getJSONArray("one_year");
                                    //JSONObject oneMonthJO = json.getJSONObject("one_month");
                                    //Log.e("this_year", this_year.toString());

                                    if(oneWeekJA.length()>0){
                                        for (int i = 0; i < oneWeekJA.length(); i++) {
                                            JSONObject temp = (JSONObject) oneWeekJA.get(i);
                                            Map<String, String> map = new HashMap<>();
                                            map.put("manager_account", temp.getString("account"));
                                            map.put("manager_realname", temp.getString("name"));
                                            map.put("sum_fenqi_count", temp.getString("number"));
                                            map.put("sum_fenqi_money", temp.getString("money"));
                                            list_fenqi_one_week.add(map);
                                        }
                                    }

                                    if(oneMonthJA.length()>0){
                                        for (int i = 0; i < oneMonthJA.length(); i++) {
                                            JSONObject temp = (JSONObject) oneMonthJA.get(i);
                                            Map<String, String> map = new HashMap<>();
                                            map.put("manager_account", temp.getString("account"));
                                            map.put("manager_realname", temp.getString("name"));
                                            map.put("sum_fenqi_count", temp.getString("number"));
                                            map.put("sum_fenqi_money", temp.getString("money"));
                                            list_fenqi_one_month.add(map);
                                        }
                                    }

                                    if(threeMonthJA.length()>0){
                                        for (int i = 0; i < threeMonthJA.length(); i++) {
                                            JSONObject temp = (JSONObject) threeMonthJA.get(i);
                                            Map<String, String> map = new HashMap<>();
                                            map.put("manager_account", temp.getString("account"));
                                            map.put("manager_realname", temp.getString("name"));
                                            map.put("sum_fenqi_count", temp.getString("number"));
                                            map.put("sum_fenqi_money", temp.getString("money"));
                                            list_fenqi_three_month.add(map);
                                        }
                                    }

                                    if(oneYearJA.length()>0){
                                        for (int i = 0; i < oneYearJA.length(); i++) {
                                            JSONObject temp = (JSONObject) oneYearJA.get(i);
                                            Map<String, String> map = new HashMap<>();
                                            map.put("manager_account", temp.getString("account"));
                                            map.put("manager_realname", temp.getString("name"));
                                            map.put("sum_fenqi_count", temp.getString("number"));
                                            map.put("sum_fenqi_money", temp.getString("money"));
                                            list_fenqi_one_year.add(map);
                                        }
                                    }

                                    if(card_status&&fenqi_stutas&&xiaodai_stutas){
                                        switch (choosen_time){
                                            case "one_week":
                                                list_card.addAll(list_fenqi_one_week);
                                                list_card.addAll(list_xiaodai_one_week);
                                                break;
                                            case "one_month":
                                                list_card.addAll(list_fenqi_one_month);
                                                list_card.addAll(list_xiaodai_one_month);
                                                break;
                                            case "three_month":
                                                list_card.addAll(list_fenqi_three_month);
                                                list_card.addAll(list_xiaodai_three_month);
                                                break;
                                            case "one_year":
                                                list_card.addAll(list_fenqi_one_year);
                                                list_card.addAll(list_xiaodai_one_year);
                                                break;
                                        }
                                        if(list_card.size()==0){
                                            listView.setVisibility(View.GONE);
                                            no_data.setVisibility(View.VISIBLE);
                                        }else {
                                            listView.setVisibility(View.VISIBLE);
                                            no_data.setVisibility(View.GONE);
                                            list_all = transform(list_card);
                                            adapter = new BasePerfAllAdapter(list_all,BasePerfAllListActivity.this);
                                            listView.setAdapter(adapter);
                                        }
                                    }
                                    /*adapter = new BasePerfAdapter(base_list_one_week,KBasePerformanceActivity.this);
                                    listView.setAdapter(adapter);

                                    if(base_list_one_week.size()>0){
                                        listView.setVisibility(View.VISIBLE);
                                        no_data.setVisibility(View.GONE);
                                    }else{
                                        listView.setVisibility(View.GONE);
                                        no_data.setVisibility(View.VISIBLE);
                                    }*/

                                } else {
                                    Log.e("KMyPreformenceActivity", BasePerfAllListActivity.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                               // Toast.makeText(BasePerfAllListActivity.this, BasePerfAllListActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("KMyPreformenceActivity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;

                    case 2 :
                        xiaodai_stutas=true;
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                //worker_list_one_week = new ArrayList<>();
                                if ("false".equals(status)) {
                                    Toast.makeText(BasePerfAllListActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONArray oneWeekJA = json.getJSONArray("one_week");
                                    JSONArray oneMonthJA = json.getJSONArray("one_month");
                                    JSONArray threeMonthJA = json.getJSONArray("three_month");
                                    JSONArray oneYearJA = json.getJSONArray("one_year");
                                    //JSONObject oneMonthJO = json.getJSONObject("one_month");
                                    //Log.e("this_year", this_year.toString());

                                    if(oneWeekJA.length()>0){
                                        for (int i = 0; i < oneWeekJA.length(); i++) {
                                            JSONObject temp = (JSONObject) oneWeekJA.get(i);
                                            Map<String, String> map = new HashMap<>();
                                            map.put("manager_account", temp.getString("account"));
                                            map.put("manager_realname", temp.getString("name"));
                                            map.put("sum_xiaodai_count", temp.getString("number"));
                                            map.put("sum_xiaodai_money", temp.getString("money"));
                                            list_xiaodai_one_week.add(map);
                                        }
                                    }

                                    if(oneMonthJA.length()>0){
                                        for (int i = 0; i < oneMonthJA.length(); i++) {
                                            JSONObject temp = (JSONObject) oneMonthJA.get(i);
                                            Map<String, String> map = new HashMap<>();
                                            map.put("manager_account", temp.getString("account"));
                                            map.put("manager_realname", temp.getString("name"));
                                            map.put("sum_xiaodai_count", temp.getString("number"));
                                            map.put("sum_xiaodai_money", temp.getString("money"));
                                            list_xiaodai_one_month.add(map);
                                        }
                                    }

                                    if(threeMonthJA.length()>0){
                                        for (int i = 0; i < threeMonthJA.length(); i++) {
                                            JSONObject temp = (JSONObject) threeMonthJA.get(i);
                                            Map<String, String> map = new HashMap<>();
                                            map.put("manager_account", temp.getString("account"));
                                            map.put("manager_realname", temp.getString("name"));
                                            map.put("sum_xiaodai_count", temp.getString("number"));
                                            map.put("sum_xiaodai_money", temp.getString("money"));
                                            list_xiaodai_three_month.add(map);
                                        }
                                    }

                                    if(oneYearJA.length()>0){
                                        for (int i = 0; i < oneYearJA.length(); i++) {
                                            JSONObject temp = (JSONObject) oneYearJA.get(i);
                                            Map<String, String> map = new HashMap<>();
                                            map.put("manager_account", temp.getString("account"));
                                            map.put("manager_realname", temp.getString("name"));
                                            map.put("sum_xiaodai_count", temp.getString("number"));
                                            map.put("sum_xiaodai_money", temp.getString("money"));
                                            list_xiaodai_one_year.add(map);
                                        }
                                    }
                                    if(card_status&&fenqi_stutas&&xiaodai_stutas){
                                        switch (choosen_time){
                                            case "one_week":
                                                list_card.addAll(list_fenqi_one_week);
                                                list_card.addAll(list_xiaodai_one_week);
                                                break;
                                            case "one_month":
                                                list_card.addAll(list_fenqi_one_month);
                                                list_card.addAll(list_xiaodai_one_month);
                                                break;
                                            case "three_month":
                                                list_card.addAll(list_fenqi_three_month);
                                                list_card.addAll(list_xiaodai_three_month);
                                                break;
                                            case "one_year":
                                                list_card.addAll(list_fenqi_one_year);
                                                list_card.addAll(list_xiaodai_one_year);
                                                break;
                                        }
                                        if(list_card.size()==0){
                                            listView.setVisibility(View.GONE);
                                            no_data.setVisibility(View.VISIBLE);
                                        }else {
                                            listView.setVisibility(View.VISIBLE);
                                            no_data.setVisibility(View.GONE);
                                            list_all = transform(list_card);
                                            adapter = new BasePerfAllAdapter(list_all,BasePerfAllListActivity.this);
                                            listView.setAdapter(adapter);
                                        }

                                    }
                                    /*adapter = new BasePerfAdapter(base_list_one_week,KBasePerformanceActivity.this);
                                    listView.setAdapter(adapter);

                                    if(base_list_one_week.size()>0){
                                        listView.setVisibility(View.VISIBLE);
                                        no_data.setVisibility(View.GONE);
                                    }else{
                                        listView.setVisibility(View.GONE);
                                        no_data.setVisibility(View.VISIBLE);
                                    }*/

                                } else {
                                    Log.e("KMyPreformenceActivity", BasePerfAllListActivity.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                // Toast.makeText(BasePerfAllListActivity.this, BasePerfAllListActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("KMyPreformenceActivity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;

                    default:
                        Log.e("BasePerform_Activity", BasePerfAllListActivity.this.getResources().getString(R.string.handler_what_exception));
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
                String account = list_all.get(position).get("manager_account");
                String name = list_all.get(position).get("manager_realname");
                Intent intent = new Intent();
                intent.setClass(BasePerfAllListActivity.this,BaseMyAllPerformanceActivity.class);
                intent.putExtra("type","0");
                intent.putExtra("account",account);
                intent.putExtra("name",name);
                startActivity(intent);
            }
        });
    }

    List<Map<String, String>> transform (List<Map<String, String>> list) {

        List<Map<String, String>> result = new ArrayList<Map<String, String>>();

        // 将所有Map中account的value值存入Set集合（去重）
        Set<String> accValues = new HashSet<String>();
        for(Map<String, String> map : list){
            String accValue = map.get("manager_account");
            accValues.add(accValue);
        }

        // 根据Set集合中account的value值对所有Map进行分类并合并属性值
        for (String accValue : accValues) {
            // 将同一账号的多个Map临时保存在temp中
            List<Map<String, String>> temp = new ArrayList<Map<String, String>>();
            for (Map<String, String> map : list) {
                if (accValue.equals(map.get("manager_account"))) {
                    temp.add(map);
                }
            }
            // 将合并处理后的Map添加到result结果集中
            Map<String, String> map = putAllByAccount(temp);
            result.add(map);
        }
        return result;
    }

    /**
     * 根据account的value值对多个Map进行属性合并，即putAll操作
     * @param temp
     * @return
     */
    Map<String, String> putAllByAccount(List<Map<String, String>> temp) {

        Map<String, String> result = new HashMap<String, String>();
        for(Map<String, String> map : temp)
        {
            result.putAll(map);
        }
        return result;
    }

    void showPopWindow(){
        View root = LayoutInflater.from(BasePerfAllListActivity.this).inflate(R.layout.choose_time_listview,null);
        ListView lv_timeList = (ListView)root.findViewById(R.id.choose_time_listview) ;
        lv_timeList.setAdapter(chooseTimeAdapter);
        pop = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        pop.setFocusable(true);
        // 不设置这句话，popWindow无法消失（点击旁边、返回键都没用）
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.showAsDropDown(select_time);
        // 在顶部显示
        pop.showAtLocation(findViewById(R.id.activity_base_perf_all_list), Gravity.TOP, 0, 0);

        lv_timeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                chooseTimeAdapter.setPosition(position);
                select_time.setText(Constants.chooseTimeList.get(position));
                final PostParameter[] params = new PostParameter[6];
                params[0] = new PostParameter("account", spu.getAccount());
                params[1] = new PostParameter("branchLevel4", spu.getBranchLevel4());
                params[2] = new PostParameter("type", "1");
                calendar.setTime(new Date());
                switch (Constants.chooseTimeList.get(position)){
                    case "近一周业绩" :
                        choosen_time="one_week";
                                /*line1.setText("分期业务成功数量："+("".equals(number1)? "暂无" : number1+"笔"));
                                line2.setText("分期业务成功金额："+("".equals(money1)? "暂无" : money1+"元"));*/
                        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
                        params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                        params[4] = new PostParameter("end", format.format(new Date()));
                        break;
                    case "近一月业绩" :
                        choosen_time="one_month";
                        //    setFenqiData(number2_fenqi,money2_fenqi);
                        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                        params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                        params[4] = new PostParameter("end", format.format(new Date()));
                        break;
                    case "近三月业绩" :
                        choosen_time="three_month";
                        //   setFenqiData(number3_fenqi,money3_fenqi);
                        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 3);
                        params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                        params[4] = new PostParameter("end", format.format(new Date()));
                        break;
                    case "近一年业绩" :
                        choosen_time="one_year";
                        //    setFenqiData(number4_fenqi,money4_fenqi);
                        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
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

    }

}
