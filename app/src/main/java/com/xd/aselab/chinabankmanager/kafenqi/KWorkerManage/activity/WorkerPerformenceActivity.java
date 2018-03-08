package com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.activity;

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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.adapter.WorkerPerformanceListAdapter;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.model.WorkerVO;
import com.xd.aselab.chinabankmanager.util.ChooseSortAdapter;
import com.xd.aselab.chinabankmanager.util.ChooseTimeAdapter;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.Constants;
import com.xd.aselab.chinabankmanager.util.ListUtils;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WorkerPerformenceActivity extends AppCompatActivity {

    private RelativeLayout back;
    private TextView sort;
    private TextView select_time;
    private ListView listView;
    private TextView no_data_text;
    private String[] select_sort_method;
    private String[] select_string;
    private SharePreferenceUtil spu;
    private Handler handler;
    private String choosen_time = "one_week";
    private String choosen_sort = "recommend_num";
    private String  recommend_num="recommend_num";//推荐业务数
    private String  success_num="success_num";//成功业务数
    private String  success_money="success_money";//成功金额数
    private String  success_rate="success_rate";//推荐成功率
    private List<WorkerVO> worker_list_one_week= new ArrayList<>();
    private List<WorkerVO> worker_list_one_month= new ArrayList<>();
    private List<WorkerVO> worker_list_three_month= new ArrayList<>();
    private List<WorkerVO> worker_list_one_year= new ArrayList<>();
    private WorkerPerformanceListAdapter adapter;
    private RelativeLayout rl_choose_sort;
    private RelativeLayout rl_choose_time;
    private ChooseTimeAdapter chooseTimeAdapter;
    private PopupWindow pop;
    private ChooseSortAdapter chooseSortAdapter;
    private RelativeLayout rl_no_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_performence);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initViews();
        initDatas();
        initEvents();
    }

    void initViews(){
        back = (RelativeLayout)findViewById(R.id.act_kafenqi_worker_perf_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sort = (TextView)findViewById(R.id.act_kefenqi_worker_perf_sort);
        select_time = (TextView)findViewById(R.id.act_kefenqi_worker_perf_select_time);

        listView = (ListView)findViewById(R.id.lv_worker_per);
        no_data_text = (TextView)findViewById(R.id.worker_perf_no_data_text);

        rl_choose_sort = (RelativeLayout)findViewById(R.id.rl_kefenqi_worker_select_sort);
        rl_choose_time = (RelativeLayout)findViewById(R.id.rl_kefenqi_worker_perf_select_time);

        rl_no_data = (RelativeLayout)findViewById(R.id.act_kafenqi_perf_ranking_no_data);
    }

    void initDatas(){
        spu = new SharePreferenceUtil(WorkerPerformenceActivity.this, "user");
        select_sort_method = new String[]{"推荐业务数降序", "成功业务数降序", "成功金额数降序", "推荐成功率降序"};
        select_string = new String[]{"近一周业绩", "近一月业绩", "近一季度业绩", "近一年业绩"};
        chooseTimeAdapter = new ChooseTimeAdapter(Constants.chooseTimeList,WorkerPerformenceActivity.this);
        chooseSortAdapter = new ChooseSortAdapter(Constants.chooseSortList,WorkerPerformenceActivity.this);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0 :
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                Log.e("MyPerformance：reCode", reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                //worker_list_one_week = new ArrayList<>();
                                if ("false".equals(status)) {
                                    Toast.makeText(WorkerPerformenceActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
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
                                            WorkerVO workerVO = new WorkerVO();
                                            workerVO.setAccount(temp.getString("account"));
                                            workerVO.setName(temp.getString("name"));
                                            workerVO.setWorker_address(temp.getString("company"));
                                            workerVO.setRecommend_number(temp.getInt("recommend_num"));
                                            workerVO.setSuccess_number(temp.getInt("success_num"));
                                            workerVO.setSuccess_money(temp.getInt("success_money"));
                                            workerVO.setSum_credit(temp.getInt("score"));
                                            workerVO.setExchange_credit(temp.getInt("exchange_score"));
                                            workerVO.setStatus(temp.getString("status"));
                                            double rate = 0;
                                            if (temp.getInt("recommend_num") > 0) {
                                                rate = temp.getInt("success_num") / temp.getInt("recommend_num");
                                            }
                                            workerVO.setSuccess_rate(rate);
                                            worker_list_one_week.add(workerVO);
                                        }
                                    }

                                    //worker_list_one_month = new ArrayList<>();
                                    if(oneMonthJA.length()>0){
                                        for (int i = 0; i < oneMonthJA.length(); i++) {
                                            JSONObject temp = (JSONObject) oneMonthJA.get(i);
                                            WorkerVO workerVO = new WorkerVO();
                                            workerVO.setAccount(temp.getString("account"));
                                            workerVO.setName(temp.getString("name"));
                                            workerVO.setStatus(temp.getString("status"));
                                            workerVO.setWorker_address(temp.getString("company"));
                                            workerVO.setRecommend_number(temp.getInt("recommend_num"));
                                            workerVO.setSuccess_number(temp.getInt("success_num"));
                                            workerVO.setSuccess_money(temp.getInt("success_money"));
                                            workerVO.setSum_credit(temp.getInt("score"));
                                            workerVO.setExchange_credit(temp.getInt("exchange_score"));
                                            double rate = 0;
                                            if (temp.getInt("recommend_num") > 0) {
                                                rate = temp.getInt("success_num") / temp.getInt("recommend_num");
                                            }
                                            workerVO.setSuccess_rate(rate);
                                            worker_list_one_month.add(workerVO);
                                        }
                                    }

                                   // worker_list_three_month = new ArrayList<>();
                                    if(threeMonthJA.length()>0){
                                        for (int i = 0; i < threeMonthJA.length(); i++) {
                                            JSONObject temp = (JSONObject) threeMonthJA.get(i);
                                            WorkerVO workerVO = new WorkerVO();
                                            workerVO.setAccount(temp.getString("account"));
                                            workerVO.setName(temp.getString("name"));
                                            workerVO.setStatus(temp.getString("status"));
                                            workerVO.setWorker_address(temp.getString("company"));
                                            workerVO.setRecommend_number(temp.getInt("recommend_num"));
                                            workerVO.setSuccess_number(temp.getInt("success_num"));
                                            workerVO.setSuccess_money(temp.getInt("success_money"));
                                            workerVO.setSum_credit(temp.getInt("score"));
                                            workerVO.setExchange_credit(temp.getInt("exchange_score"));
                                            double rate = 0;
                                            if (temp.getInt("recommend_num") > 0) {
                                                rate = temp.getInt("success_num") / temp.getInt("recommend_num");
                                            }
                                            workerVO.setSuccess_rate(rate);
                                            worker_list_three_month.add(workerVO);
                                        }
                                    }

                                   // worker_list_one_year = new ArrayList<>();
                                    if(oneYearJA.length()>0){
                                        for (int i = 0; i < oneYearJA.length(); i++) {
                                            JSONObject temp = (JSONObject) oneYearJA.get(i);
                                            WorkerVO workerVO = new WorkerVO();
                                            workerVO.setAccount(temp.getString("account"));
                                            workerVO.setName(temp.getString("name"));
                                            workerVO.setStatus(temp.getString("status"));
                                            workerVO.setWorker_address(temp.getString("company"));
                                            workerVO.setRecommend_number(temp.getInt("recommend_num"));
                                            workerVO.setSuccess_number(temp.getInt("success_num"));
                                            workerVO.setSuccess_money(temp.getInt("success_money"));
                                            workerVO.setSum_credit(temp.getInt("score"));
                                            workerVO.setExchange_credit(temp.getInt("exchange_score"));
                                            double rate = 0;
                                            if (temp.getInt("recommend_num") > 0) {
                                                rate = temp.getInt("success_num") / temp.getInt("recommend_num");
                                            }
                                            workerVO.setSuccess_rate(rate);
                                            worker_list_one_year.add(workerVO);
                                        }
                                    }

                                    adapter = new WorkerPerformanceListAdapter(worker_list_one_week,WorkerPerformenceActivity.this);
                                    listView.setAdapter(adapter);
                                    if(worker_list_one_week.size()>0){
                                        listView.setVisibility(View.VISIBLE);
                                        rl_no_data.setVisibility(View.GONE);

                                    }else{
                                        listView.setVisibility(View.GONE);
                                        rl_no_data.setVisibility(View.VISIBLE);
                                    }

                                } else {
                                    Log.e("KMyPreformenceActivity", WorkerPerformenceActivity.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(WorkerPerformenceActivity.this, WorkerPerformenceActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("KMyPreformenceActivity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("KMyPreformenceActivity", WorkerPerformenceActivity.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

        new Thread(){
            @Override
            public void run() {
                super.run();
                final PostParameter[] params = new PostParameter[1];
                params[0] = new PostParameter("account", spu.getAccount());
                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetInstallmentWorkerPerformance, params, ConnectUtil.POST);
                Log.e("reCode",""+reCode);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();


    }

    void initEvents(){

        rl_choose_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortPopWindow();
            }
        });

        rl_choose_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePopWindow();
            }
        });

       /* select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivityForResult(new Intent().setClass(PerformanceRanking.this, PerfRankTransparent.class),102);
                AlertDialog.Builder builder = new AlertDialog.Builder(WorkerPerformenceActivity.this);
                builder.setTitle("选择时间范围");
                builder.setItems(select_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        select_time.setText(select_string[which]);
                        switch (select_string[which]){
                            case "近一周业绩" :
                                choosen_time="one_week";
                                switch (choosen_sort){
                                    case "recommend_num":
                                        ListUtils.sort(worker_list_one_week,false,"recommend_number");
                                        adapter.setList(worker_list_one_week);
                                        adapter.notifyDataSetChanged();
                                        break;
                                    case "success_num":
                                        setListAndAdaper(worker_list_one_week,"success_number");
                                        break;
                                    case "success_money":
                                        setListAndAdaper(worker_list_one_week,"success_money");
                                        break;
                                    case "success_rate":
                                        setListAndAdaper(worker_list_one_week,"success_rate");
                                        break;
                                }
                                break;
                            case "近一月业绩" :
                                choosen_time="one_month";
                                switch (choosen_sort){
                                    case "recommend_num":
                                        setListAndAdaper(worker_list_one_month,"recommend_number");
                                        break;
                                    case "success_num":
                                        setListAndAdaper(worker_list_one_month,"success_number");
                                        break;
                                    case "success_money":
                                        setListAndAdaper(worker_list_one_month,"success_money");
                                        break;
                                    case "success_rate":
                                        setListAndAdaper(worker_list_one_month,"success_rate");
                                        break;
                                }
                                break;
                            case "近三月业绩" :
                                choosen_time="three_month";
                                switch (choosen_sort){
                                    case "recommend_num":
                                        setListAndAdaper(worker_list_three_month,"recommend_number");
                                        break;
                                    case "success_num":
                                        setListAndAdaper(worker_list_three_month,"success_number");
                                        break;
                                    case "success_money":
                                        setListAndAdaper(worker_list_three_month,"success_money");
                                        break;
                                    case "success_rate":
                                        setListAndAdaper(worker_list_three_month,"success_rate");
                                        break;
                                }
                                break;
                            case "近一年业绩" :
                                choosen_time="one_year";
                                switch (choosen_sort){
                                    case "recommend_num":
                                        setListAndAdaper(worker_list_one_year,"recommend_number");
                                        break;
                                    case "success_num":
                                        setListAndAdaper(worker_list_one_year,"success_number");
                                        break;
                                    case "success_money":
                                        setListAndAdaper(worker_list_one_year,"success_money");
                                        break;
                                    case "success_rate":
                                        setListAndAdaper(worker_list_one_year,"success_rate");
                                        break;
                                }
                                break;
                        }
                    }
                });
                builder.show();
            }
        });*/

        /*sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivityForResult(new Intent().setClass(PerformanceRanking.this, PerfRankTransparent.class),102);
                AlertDialog.Builder builder = new AlertDialog.Builder(WorkerPerformenceActivity.this);
                builder.setTitle("选择排序方式");
                builder.setItems(select_sort_method, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (select_sort_method[which]){
                            case "推荐业务数降序" :
                                choosen_sort=recommend_num;
                                switch (choosen_time){
                                    case "one_week":
                                        setListAndAdaper(worker_list_one_week,"recommend_number");
                                        break;
                                    case "one_month":
                                        setListAndAdaper(worker_list_one_month,"recommend_number");
                                        break;
                                    case "three_month":
                                        setListAndAdaper(worker_list_three_month,"recommend_number");
                                        break;
                                    case "one_year":
                                        setListAndAdaper(worker_list_one_year,"recommend_number");
                                        break;
                                }
                                break;
                            case "成功业务数降序" :
                                choosen_sort=success_num;
                                switch (choosen_time){
                                    case "one_week":
                                        setListAndAdaper(worker_list_one_week,"success_number");
                                        break;
                                    case "one_month":
                                        setListAndAdaper(worker_list_one_month,"success_number");
                                        break;
                                    case "three_month":
                                        setListAndAdaper(worker_list_three_month,"success_number");
                                        break;
                                    case "one_year":
                                        setListAndAdaper(worker_list_one_year,"success_number");
                                        break;
                                }
                                break;
                            case "成功金额数降序" :
                                choosen_sort=success_money;
                                switch (choosen_time){
                                    case "one_week":
                                        setListAndAdaper(worker_list_one_week,"success_money");
                                        break;
                                    case "one_month":
                                        setListAndAdaper(worker_list_one_month,"success_money");
                                        break;
                                    case "three_month":
                                        setListAndAdaper(worker_list_three_month,"success_money");
                                        break;
                                    case "one_year":
                                        setListAndAdaper(worker_list_one_year,"success_money");
                                        break;
                                }
                                break;
                            case "推荐成功率降序" :
                                choosen_sort=success_rate;
                                switch (choosen_time){
                                    case "one_week":
                                        setListAndAdaper(worker_list_one_week,"success_rate");
                                        break;
                                    case "one_month":
                                        setListAndAdaper(worker_list_one_month,"success_rate");
                                        break;
                                    case "three_month":
                                        setListAndAdaper(worker_list_three_month,"success_rate");
                                        break;
                                    case "one_year":
                                        setListAndAdaper(worker_list_one_year,"success_rate");
                                        break;
                                }
                                break;
                        }
                    }
                });
                builder.show();
            }
        });*/



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String worker_account = "";
                String worker_name = "";
                String worker_status = "";
                String time_range = "";
               // worker_account = worker_list.get(position).getAccount();
                switch (select_time.getText().toString()){
                    case "近一周业绩" :
                        worker_account = worker_list_one_week.get(position).getAccount();
                        worker_name = worker_list_one_week.get(position).getName();
                        worker_status = worker_list_one_week.get(position).getStatus();
                        time_range = "近一周业绩";
                        break;
                    case "近一月业绩" :
                        worker_account = worker_list_one_month.get(position).getAccount();
                        worker_name = worker_list_one_month.get(position).getName();
                        worker_status = worker_list_one_month.get(position).getStatus();
                        time_range = "近一月业绩";
                        break;
                    case "近一季度业绩" :
                        worker_account = worker_list_three_month.get(position).getAccount();
                        worker_name = worker_list_three_month.get(position).getName();
                        worker_status = worker_list_three_month.get(position).getStatus();
                        time_range = "近一季度业绩";
                        break;
                    case "近一年业绩" :
                        worker_account = worker_list_one_year.get(position).getAccount();
                        worker_name = worker_list_one_year.get(position).getName();
                        worker_status = worker_list_one_year.get(position).getStatus();
                        time_range = "近一年业绩";
                        break;
                }
                Intent intent = new Intent();
                intent.setClass(WorkerPerformenceActivity.this,WorkerPerformanceDetailActivity.class);
                intent.putExtra("worker_account",worker_account);
                intent.putExtra("worker_name",worker_name);
                intent.putExtra("worker_status",worker_status);
                intent.putExtra("time_range",time_range);
                startActivity(intent);
            }
        });
    }

    void setListAndAdaper(List<WorkerVO> list,String name){

        if(list.size()==0){
            listView.setVisibility(View.GONE);
            rl_no_data.setVisibility(View.VISIBLE);
        }else{
            listView.setVisibility(View.VISIBLE);
            rl_no_data.setVisibility(View.GONE);
            ListUtils.sort(list,false,name);
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }

    }

    void showSortPopWindow(){
        View root = LayoutInflater.from(WorkerPerformenceActivity.this).inflate(R.layout.choose_time_listview,null);
        ListView lv_timeList = (ListView)root.findViewById(R.id.choose_time_listview) ;
        lv_timeList.setAdapter(chooseSortAdapter);
        pop = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        pop.setFocusable(true);
        // 不设置这句话，popWindow无法消失（点击旁边、返回键都没用）
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.showAsDropDown(select_time);
        // 在顶部显示
        pop.showAtLocation(findViewById(R.id.activity_worker_performence), Gravity.TOP, 0, 0);

        lv_timeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                chooseSortAdapter.setPosition(position);
                sort.setText(Constants.chooseSortList.get(position));
                switch (Constants.chooseSortList.get(position)){
                    case "推荐业务数降序" :
                        choosen_sort=recommend_num;
                        switch (choosen_time){
                            case "one_week":
                                setListAndAdaper(worker_list_one_week,"recommend_number");
                                break;
                            case "one_month":
                                setListAndAdaper(worker_list_one_month,"recommend_number");
                                break;
                            case "three_month":
                                setListAndAdaper(worker_list_three_month,"recommend_number");
                                break;
                            case "one_year":
                                setListAndAdaper(worker_list_one_year,"recommend_number");
                                break;
                        }
                        break;
                    case "成功业务数降序" :
                        choosen_sort=success_num;
                        switch (choosen_time){
                            case "one_week":
                                setListAndAdaper(worker_list_one_week,"success_number");
                                break;
                            case "one_month":
                                setListAndAdaper(worker_list_one_month,"success_number");
                                break;
                            case "three_month":
                                setListAndAdaper(worker_list_three_month,"success_number");
                                break;
                            case "one_year":
                                setListAndAdaper(worker_list_one_year,"success_number");
                                break;
                        }
                        break;
                    case "成功金额数降序" :
                        choosen_sort=success_money;
                        switch (choosen_time){
                            case "one_week":
                                setListAndAdaper(worker_list_one_week,"success_money");
                                break;
                            case "one_month":
                                setListAndAdaper(worker_list_one_month,"success_money");
                                break;
                            case "three_month":
                                setListAndAdaper(worker_list_three_month,"success_money");
                                break;
                            case "one_year":
                                setListAndAdaper(worker_list_one_year,"success_money");
                                break;
                        }
                        break;
                    case "推荐成功率降序" :
                        choosen_sort=success_rate;
                        switch (choosen_time){
                            case "one_week":
                                setListAndAdaper(worker_list_one_week,"success_rate");
                                break;
                            case "one_month":
                                setListAndAdaper(worker_list_one_month,"success_rate");
                                break;
                            case "three_month":
                                setListAndAdaper(worker_list_three_month,"success_rate");
                                break;
                            case "one_year":
                                setListAndAdaper(worker_list_one_year,"success_rate");
                                break;
                        }
                        break;
                }
            }
        });
    }

    void showTimePopWindow(){

        View root = LayoutInflater.from(WorkerPerformenceActivity.this).inflate(R.layout.choose_time_listview,null);
        ListView lv_timeList = (ListView)root.findViewById(R.id.choose_time_listview) ;
        lv_timeList.setAdapter(chooseTimeAdapter);
        pop = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        pop.setFocusable(true);
        // 不设置这句话，popWindow无法消失（点击旁边、返回键都没用）
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.showAsDropDown(select_time);
        // 在顶部显示
        pop.showAtLocation(findViewById(R.id.activity_worker_performence), Gravity.TOP, 0, 0);

        lv_timeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                chooseTimeAdapter.setPosition(position);
                select_time.setText(Constants.chooseTimeList.get(position));
                switch (Constants.chooseTimeList.get(position)){
                    case "近一周业绩" :
                        choosen_time="one_week";
                        switch (choosen_sort){
                            case "recommend_num":
                                ListUtils.sort(worker_list_one_week,false,"recommend_number");
                                adapter.setList(worker_list_one_week);
                                adapter.notifyDataSetChanged();
                                break;
                            case "success_num":
                                setListAndAdaper(worker_list_one_week,"success_number");
                                break;
                            case "success_money":
                                setListAndAdaper(worker_list_one_week,"success_money");
                                break;
                            case "success_rate":
                                setListAndAdaper(worker_list_one_week,"success_rate");
                                break;
                        }
                        break;
                    case "近一月业绩" :
                        choosen_time="one_month";
                        switch (choosen_sort){
                            case "recommend_num":
                                setListAndAdaper(worker_list_one_month,"recommend_number");
                                break;
                            case "success_num":
                                setListAndAdaper(worker_list_one_month,"success_number");
                                break;
                            case "success_money":
                                setListAndAdaper(worker_list_one_month,"success_money");
                                break;
                            case "success_rate":
                                setListAndAdaper(worker_list_one_month,"success_rate");
                                break;
                        }
                        break;
                    case "近一季度业绩" :
                        choosen_time="three_month";
                        switch (choosen_sort){
                            case "recommend_num":
                                setListAndAdaper(worker_list_three_month,"recommend_number");
                                break;
                            case "success_num":
                                setListAndAdaper(worker_list_three_month,"success_number");
                                break;
                            case "success_money":
                                setListAndAdaper(worker_list_three_month,"success_money");
                                break;
                            case "success_rate":
                                setListAndAdaper(worker_list_three_month,"success_rate");
                                break;
                        }
                        break;
                    case "近一年业绩" :
                        choosen_time="one_year";
                        switch (choosen_sort){
                            case "recommend_num":
                                setListAndAdaper(worker_list_one_year,"recommend_number");
                                break;
                            case "success_num":
                                setListAndAdaper(worker_list_one_year,"success_number");
                                break;
                            case "success_money":
                                setListAndAdaper(worker_list_one_year,"success_money");
                                break;
                            case "success_rate":
                                setListAndAdaper(worker_list_one_year,"success_rate");
                                break;
                        }
                        break;
                }
            }
        });
    }

}
