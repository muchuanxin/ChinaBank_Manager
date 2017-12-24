package com.xd.aselab.chinabankmanager.kafenqi.manager;

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
import com.xd.aselab.chinabankmanager.kafenqi.manager.adapter.BasePerRankAdapter;
import com.xd.aselab.chinabankmanager.kafenqi.manager.model.BaseVO;
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

public class BasePerformanceRandkingActivity extends AppCompatActivity {

    private RelativeLayout back;
    private TextView select_time;
    private TextView sort;
    private ListView listView;
    private RelativeLayout rl_no_data;
    private String[] select_sort_method;
    private String[] select_string;
    private SharePreferenceUtil spu;
    private Handler handler;
    private String choosen_time = "one_week";
    private String choosen_sort = "fenqi_num";
    private List<BaseVO> base_list_one_week= new ArrayList<>();
    private List<BaseVO> base_list_one_month= new ArrayList<>();
    private List<BaseVO> base_list_three_month= new ArrayList<>();
    private List<BaseVO> base_list_one_year= new ArrayList<>();
    private String flag = "fenqi_num";
    private BasePerRankAdapter adapter;
    private RelativeLayout rl_choose_sort;
    private RelativeLayout rl_choose_time;
    private ChooseTimeAdapter chooseTimeAdapter;
    private PopupWindow pop;
    private ChooseSortAdapter chooseSortAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_performance_randking);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initViews();
        initDatas();
        initEvents();

    }

    void initViews(){
        back = (RelativeLayout)findViewById(R.id.act_kafenqi_perf_ranking_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sort = (TextView)findViewById(R.id.act_kafenqi_perf_ranking_select);
        select_time = (TextView)findViewById(R.id.act_kefenqi_basic_perf_select_time);

        listView = (ListView)findViewById(R.id.act_kafenqi_perf_ranking_list_view);
        rl_no_data = (RelativeLayout) findViewById(R.id.act_kafenqi_perf_ranking_no_data);

        rl_choose_sort = (RelativeLayout)findViewById(R.id.rl_kefenqi_worker_select_sort);
        rl_choose_time = (RelativeLayout)findViewById(R.id.rl_kefenqi_worker_perf_select_time);
    }

    void initDatas(){
        spu = new SharePreferenceUtil(BasePerformanceRandkingActivity.this, "user");
        select_sort_method = new String[]{"分期业务数降序", "分期金额数降序"};
        select_string = new String[]{"近一周业绩", "近一月业绩", "近三月业绩", "近一年业绩"};

        chooseTimeAdapter = new ChooseTimeAdapter(Constants.chooseTimeList,BasePerformanceRandkingActivity.this);
        chooseSortAdapter = new ChooseSortAdapter(Constants.chooseRankList,BasePerformanceRandkingActivity.this);

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
                                    Toast.makeText(BasePerformanceRandkingActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
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
                                            BaseVO baseVO = new BaseVO();
                                            baseVO.setBase_name(temp.getString("name"));
                                            baseVO.setBase_fenqi_num(temp.getInt("number"));
                                            baseVO.setBase_fenqi_money(temp.getDouble("money"));
                                            baseVO.setFlag("fenqi_num");
                                            base_list_one_week.add(baseVO);
                                        }
                                    }

                                    //worker_list_one_month = new ArrayList<>();
                                    if(oneMonthJA.length()>0){
                                        for (int i = 0; i < oneMonthJA.length(); i++) {
                                            JSONObject temp = (JSONObject) oneMonthJA.get(i);
                                            BaseVO baseVO = new BaseVO();
                                            baseVO.setBase_name(temp.getString("name"));
                                            baseVO.setBase_fenqi_num(temp.getInt("number"));
                                            baseVO.setBase_fenqi_money(temp.getDouble("money"));
                                            baseVO.setFlag("fenqi_num");
                                            base_list_one_month.add(baseVO);
                                        }
                                    }

                                    // worker_list_three_month = new ArrayList<>();
                                    if(threeMonthJA.length()>0){
                                        for (int i = 0; i < threeMonthJA.length(); i++) {
                                            JSONObject temp = (JSONObject) threeMonthJA.get(i);
                                            BaseVO baseVO = new BaseVO();
                                            baseVO.setBase_name(temp.getString("name"));
                                            baseVO.setBase_fenqi_num(temp.getInt("number"));
                                            baseVO.setBase_fenqi_money(temp.getDouble("money"));
                                            baseVO.setFlag("fenqi_num");
                                            base_list_three_month.add(baseVO);
                                        }
                                    }

                                    // worker_list_one_year = new ArrayList<>();
                                    if(oneYearJA.length()>0){
                                        for (int i = 0; i < oneYearJA.length(); i++) {
                                            JSONObject temp = (JSONObject) oneYearJA.get(i);
                                            BaseVO baseVO = new BaseVO();
                                            baseVO.setBase_name(temp.getString("name"));
                                            baseVO.setBase_fenqi_num(temp.getInt("number"));
                                            baseVO.setBase_fenqi_money(temp.getDouble("money"));
                                            baseVO.setFlag("fenqi_num");
                                            base_list_one_year.add(baseVO);
                                        }
                                    }

                                    adapter = new BasePerRankAdapter(base_list_one_week,BasePerformanceRandkingActivity.this);
                                    listView.setAdapter(adapter);
                                    if(base_list_one_week.size()>0){
                                        listView.setVisibility(View.VISIBLE);
                                        rl_no_data.setVisibility(View.GONE);

                                    }else{
                                        listView.setVisibility(View.GONE);
                                        rl_no_data.setVisibility(View.VISIBLE);
                                    }

                                } else {
                                    Log.e("KMyPreformenceActivity", BasePerformanceRandkingActivity.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(BasePerformanceRandkingActivity.this, BasePerformanceRandkingActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("KMyPreformenceActivity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("KMyPreformenceActivity", BasePerformanceRandkingActivity.this.getResources().getString(R.string.handler_what_exception));
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
                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetInstallmentBasicPerformance, params, ConnectUtil.POST);
                Log.e("reCode",""+reCode);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();


    }

    void initEvents(){

        rl_choose_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePopWindow();

            }
        });

        rl_choose_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortPopWindow();

            }
        });
    }

    void setListAndAdaper(List<BaseVO> list,String name){

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

    void setFlag(List<BaseVO> list,String flag){
        if (list.size()>0){
            for (int i= 0;i<list.size();i++){
                Log.e("www","setFlag---"+flag);
                list.get(i).setFlag(flag);
            }
            Log.e("www","setFlag---"+list.get(list.size()-1).getFlag());
        }
    }

    void showSortPopWindow(){
        View root = LayoutInflater.from(BasePerformanceRandkingActivity.this).inflate(R.layout.choose_time_listview,null);
        ListView lv_timeList = (ListView)root.findViewById(R.id.choose_time_listview) ;
        lv_timeList.setAdapter(chooseSortAdapter);
        pop = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        pop.setFocusable(true);
        // 不设置这句话，popWindow无法消失（点击旁边、返回键都没用）
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.showAsDropDown(select_time);
        // 在顶部显示
        pop.showAtLocation(findViewById(R.id.activity_base_performance_ranking), Gravity.TOP, 0, 0);

        lv_timeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                chooseSortAdapter.setPosition(position);
                sort.setText(Constants.chooseRankList.get(position));
                switch (Constants.chooseRankList.get(position)){
                    case "分期业务数降序" :
                        choosen_sort="fenqi_num";
                        switch (choosen_time){
                            case "one_week":
                                setFlag(base_list_one_week,"fenqi_num");
                                setListAndAdaper(base_list_one_week,"base_fenqi_num");
                                break;
                            case "one_month":
                                setFlag(base_list_one_month,"fenqi_num");
                                setListAndAdaper(base_list_one_month,"base_fenqi_num");
                                break;
                            case "three_month":
                                setFlag(base_list_three_month,"fenqi_num");
                                setListAndAdaper(base_list_three_month,"base_fenqi_num");
                                break;
                            case "one_year":
                                setFlag(base_list_one_year,"fenqi_num");
                                setListAndAdaper(base_list_one_year,"base_fenqi_num");
                                break;
                        }
                        break;

                    case "分期金额数降序" :
                        choosen_sort="fenqi_money";
                        switch (choosen_time){
                            case "one_week":
                                setFlag(base_list_one_week,"fenqi_money");
                                setListAndAdaper(base_list_one_week,"base_fenqi_money");
                                break;
                            case "one_month":
                                setFlag(base_list_one_month,"fenqi_money");
                                setListAndAdaper(base_list_one_month,"base_fenqi_money");
                                break;
                            case "three_month":
                                setFlag(base_list_three_month,"fenqi_money");
                                setListAndAdaper(base_list_three_month,"base_fenqi_money");
                                break;
                            case "one_year":
                                setFlag(base_list_one_year,"fenqi_money");
                                setListAndAdaper(base_list_one_year,"base_fenqi_money");
                                break;
                        }
                        break;
                }
            }
        });
    }

    void showTimePopWindow(){

        View root = LayoutInflater.from(BasePerformanceRandkingActivity.this).inflate(R.layout.choose_time_listview,null);
        ListView lv_timeList = (ListView)root.findViewById(R.id.choose_time_listview) ;
        lv_timeList.setAdapter(chooseTimeAdapter);
        pop = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        pop.setFocusable(true);
        // 不设置这句话，popWindow无法消失（点击旁边、返回键都没用）
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.showAsDropDown(select_time);
        // 在顶部显示
        pop.showAtLocation(findViewById(R.id.activity_base_performance_ranking), Gravity.TOP, 0, 0);

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
                            case "fenqi_num":
                                ListUtils.sort(base_list_one_week,false,"base_fenqi_num");
                                setFlag(base_list_one_week,"fenqi_num");
                                adapter.setList(base_list_one_week);
                                adapter.notifyDataSetChanged();
                                break;
                            case "fenqi_money":
                                setFlag(base_list_one_week,"fenqi_money");
                                setListAndAdaper(base_list_one_week,"base_fenqi_money");
                                break;
                        }
                        break;
                    case "近一月业绩" :
                        choosen_time="one_month";
                        switch (choosen_sort){
                            case "fenqi_num":
                                setFlag(base_list_one_week,"fenqi_num");
                                setListAndAdaper(base_list_one_month,"base_fenqi_num");
                                break;
                            case "fenqi_money":
                                setFlag(base_list_one_week,"fenqi_money");
                                setListAndAdaper(base_list_one_month,"base_fenqi_money");
                                break;
                        }
                        break;
                    case "近三月业绩" :
                        choosen_time="three_month";
                        switch (choosen_sort){
                            case "fenqi_num":
                                setFlag(base_list_one_week,"fenqi_num");
                                setListAndAdaper(base_list_three_month,"base_fenqi_num");
                                break;
                            case "fenqi_money":
                                setFlag(base_list_one_week,"fenqi_money");
                                setListAndAdaper(base_list_three_month,"base_fenqi_money");
                                break;
                        }
                        break;
                    case "近一年业绩" :
                        choosen_time="one_year";
                        switch (choosen_sort){
                            case "fenqi_num":
                                setFlag(base_list_one_week,"fenqi_num");
                                setListAndAdaper(base_list_one_year,"base_fenqi_num");
                                break;
                            case "fenqi_money":
                                setFlag(base_list_one_week,"fenqi_money");
                                setListAndAdaper(base_list_one_year,"base_fenqi_money");
                                break;
                        }
                        break;
                }
            }
        });
    }


}
