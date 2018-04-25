package com.xd.aselab.chinabankmanager.kafenqi.ProvincialBankManager;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
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
import com.xd.aselab.chinabankmanager.kafenqi.manager.KafenqiBasePerfDetailActivity;
import com.xd.aselab.chinabankmanager.kafenqi.manager.adapter.BasePerfAdapter;
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

public class KSubManagerPerformanceDetailActivity extends AppCompatActivity {

    private RelativeLayout back;
    private TextView select_time;
    private ListView listView;
    private LinearLayout no_data;
    private SharePreferenceUtil spu;
    private Handler handler;
    private String[] select_string;
    private String choosen_time = "one_week";
    private List<BaseVO> base_list_one_week= new ArrayList<>();
    private List<BaseVO> base_list_one_month= new ArrayList<>();
    private List<BaseVO> base_list_three_month= new ArrayList<>();
    private List<BaseVO> base_list_one_year= new ArrayList<>();
    private BasePerfAdapter adapter;
    private ChooseTimeAdapter chooseTimeAdapter;
    private PopupWindow pop;
    private RelativeLayout rl_choose_time;
    private Intent intent;
    private String secondary_num;

    private TextView sort;
    private RelativeLayout rl_choose_sort;
    private LinearLayout rl_no_data;
    private ChooseSortAdapter chooseSortAdapter;
    private String choosen_sort = "base_fenqi_num";
    private String  base_fenqi_num="base_fenqi_num";//分期业务数
    private String  base_fenqi_money="base_fenqi_money";//分期金额数
    private String shijian;
    private Integer position1=0;
    private String worker_account = "";
    private String worker_name = "";
    private String range = "";
    private int fenqi_num = 0;
    private double fenqi_money = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ksubmanager_performance_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initView();
        initDatas();
        initEvents();

    }

    void initView(){
        back = (RelativeLayout)findViewById(R.id.act_kafenqi_base_perf_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        select_time = (TextView)findViewById(R.id.act_kefenqi_base_perf_select_time);
        listView = (ListView)findViewById(R.id.act_kafenqi_base_perf_lv);
        no_data = (LinearLayout)findViewById(R.id.act_kafenqi_base_perf_no_data);
        rl_choose_time = (RelativeLayout) findViewById(R.id.rl_my_perf_select_time);

        sort = (TextView)findViewById(R.id.act_kefenqi_worker_perf_sort);
        rl_choose_sort = (RelativeLayout)findViewById(R.id.rl_kefenqi_worker_select_sort);
        rl_no_data = (LinearLayout)findViewById(R.id.act_kafenqi_base_perf_no_data);
    }

    void initDatas() {
        spu = new SharePreferenceUtil(KSubManagerPerformanceDetailActivity.this, "user");
        select_string = new String[]{"近一周业绩", "近一月业绩", "近一季度业绩", "近一年业绩"};
        chooseTimeAdapter = new ChooseTimeAdapter(Constants.chooseTimeList, KSubManagerPerformanceDetailActivity.this);
        chooseSortAdapter = new ChooseSortAdapter(Constants.shengHangChooseSortList, KSubManagerPerformanceDetailActivity.this);
        intent = getIntent();
        secondary_num = intent.getStringExtra("worker_account");
        shijian = intent.getStringExtra("shijian");
        position1 = intent.getIntExtra("position", 0);
       /* if (shijian != null) {
            switch (shijian) {
                case "近一周业绩":
                    worker_account = base_list_one_week.get(position1).getBase_account();
                    worker_name = base_list_one_week.get(position1).getBase_name();
                    range = "近一周业绩";
                    fenqi_num = base_list_one_week.get(position1).getBase_fenqi_num();
                    fenqi_money = base_list_one_week.get(position1).getBase_fenqi_money();
                    break;
                case "近一月业绩":
                    worker_account = base_list_one_month.get(position1).getBase_account();
                    worker_name = base_list_one_month.get(position1).getBase_name();
                    range = "近一月业绩";
                    fenqi_num = base_list_one_month.get(position1).getBase_fenqi_num();
                    fenqi_money = base_list_one_month.get(position1).getBase_fenqi_money();
                    break;
                case "近一季度业绩":
                    worker_account = base_list_three_month.get(position1).getBase_account();
                    worker_name = base_list_three_month.get(position1).getBase_name();
                    range = "近一季度业绩";
                    fenqi_num = base_list_three_month.get(position1).getBase_fenqi_num();
                    fenqi_money = base_list_three_month.get(position1).getBase_fenqi_money();
                    break;
                case "近一年业绩":
                    worker_account = base_list_one_year.get(position1).getBase_account();
                    worker_name = base_list_one_year.get(position1).getBase_name();
                    range = "近一年业绩";
                    fenqi_num = base_list_one_year.get(position1).getBase_fenqi_num();
                    fenqi_money = base_list_one_year.get(position1).getBase_fenqi_money();
                    break;
            }*/
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case 0:
                            try {
                                String reCode = (String) msg.obj;
                                if (reCode != null) {
                                    Log.e("KBasePerformance：reCode", reCode);
                                    JSONObject json = new JSONObject(reCode);
                                    String status = json.getString("status");
                                    //worker_list_one_week = new ArrayList<>();
                                    if ("false".equals(status)) {
                                        Toast.makeText(KSubManagerPerformanceDetailActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                    } else if ("true".equals(status)) {
                                        JSONArray oneWeekJA = json.getJSONArray("one_week");
                                        JSONArray oneMonthJA = json.getJSONArray("one_month");
                                        JSONArray threeMonthJA = json.getJSONArray("three_month");
                                        JSONArray oneYearJA = json.getJSONArray("one_year");
                                        //JSONObject oneMonthJO = json.getJSONObject("one_month");
                                        //Log.e("this_year", this_year.toString());

                                        base_list_one_week.clear();
                                        base_list_one_month.clear();
                                        base_list_three_month.clear();
                                        base_list_one_year.clear();
                                        if (oneWeekJA.length() > 0) {
                                            for (int i = 0; i < oneWeekJA.length(); i++) {
                                                JSONObject temp = (JSONObject) oneWeekJA.get(i);
                                                BaseVO baseVO = new BaseVO();
                                                baseVO.setBase_account(temp.getString("account"));
                                                baseVO.setBase_name(temp.getString("name"));
                                                baseVO.setBase_fenqi_num(temp.getInt("number"));
                                                baseVO.setBase_fenqi_money(temp.getDouble("money"));
                                                base_list_one_week.add(baseVO);
                                            }
                                        }

                                        //worker_list_one_month = new ArrayList<>();
                                        if (oneMonthJA.length() > 0) {
                                            for (int i = 0; i < oneMonthJA.length(); i++) {
                                                JSONObject temp = (JSONObject) oneMonthJA.get(i);
                                                BaseVO baseVO = new BaseVO();
                                                baseVO.setBase_account(temp.getString("account"));
                                                baseVO.setBase_name(temp.getString("name"));
                                                baseVO.setBase_fenqi_num(temp.getInt("number"));
                                                baseVO.setBase_fenqi_money(temp.getDouble("money"));
                                                base_list_one_month.add(baseVO);
                                            }
                                        }

                                        // worker_list_three_month = new ArrayList<>();
                                        if (threeMonthJA.length() > 0) {
                                            for (int i = 0; i < threeMonthJA.length(); i++) {
                                                JSONObject temp = (JSONObject) threeMonthJA.get(i);
                                                BaseVO baseVO = new BaseVO();
                                                baseVO.setBase_account(temp.getString("account"));
                                                baseVO.setBase_name(temp.getString("name"));
                                                baseVO.setBase_fenqi_num(temp.getInt("number"));
                                                baseVO.setBase_fenqi_money(temp.getDouble("money"));
                                                base_list_three_month.add(baseVO);
                                            }
                                        }

                                        // worker_list_one_year = new ArrayList<>();
                                        if (oneYearJA.length() > 0) {
                                            for (int i = 0; i < oneYearJA.length(); i++) {
                                                JSONObject temp = (JSONObject) oneYearJA.get(i);
                                                BaseVO baseVO = new BaseVO();
                                                baseVO.setBase_account(temp.getString("account"));
                                                baseVO.setBase_name(temp.getString("name"));
                                                baseVO.setBase_fenqi_num(temp.getInt("number"));
                                                baseVO.setBase_fenqi_money(temp.getDouble("money"));
                                                base_list_one_year.add(baseVO);
                                            }
                                        }

                                        List temp = null;
                                        if (shijian!=null && !"".equals(shijian)) {
                                            switch (shijian) {
                                                case "近一周业绩":
                                                    chooseTimeAdapter.setPosition(0);
                                                    choosen_time="one_week";
                                                    temp = base_list_one_week;
                                                    break;
                                                case "近一月业绩":
                                                    chooseTimeAdapter.setPosition(1);
                                                    choosen_time="one_month";
                                                    temp = base_list_one_month;
                                                    break;
                                                case "近一季度业绩":
                                                    chooseTimeAdapter.setPosition(2);
                                                    choosen_time="three_month";
                                                    temp = base_list_three_month;
                                                    break;
                                                case "近一年业绩":
                                                    chooseTimeAdapter.setPosition(3);
                                                    choosen_time="one_year";
                                                    temp = base_list_one_year;
                                                    break;
                                            }
                                            select_time.setText(shijian);
                                            adapter = new BasePerfAdapter(temp, KSubManagerPerformanceDetailActivity.this);
                                        }
                                        else {
                                            select_time.setText("近一周业绩");
                                            adapter = new BasePerfAdapter(base_list_one_week, KSubManagerPerformanceDetailActivity.this);
                                        }
                                        listView.setAdapter(adapter);

                                        if (temp.size() > 0) {
                                            listView.setVisibility(View.VISIBLE);
                                            no_data.setVisibility(View.GONE);

                                        } else {
                                            listView.setVisibility(View.GONE);
                                            no_data.setVisibility(View.VISIBLE);
                                        }

                                    } else {
                                        Log.e("KMyPreformenceActivity", KSubManagerPerformanceDetailActivity.this.getResources().getString(R.string.status_exception));
                                    }
                                } else {
                                    Toast.makeText(KSubManagerPerformanceDetailActivity.this, KSubManagerPerformanceDetailActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                    Log.e("KMyPreformenceActivity", "reCode为空");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            Log.e("KMyPreformenceActivity", KSubManagerPerformanceDetailActivity.this.getResources().getString(R.string.handler_what_exception));
                            break;
                    }
                }
            };

            new Thread() {
                @Override
                public void run() {
                    super.run();
                    final PostParameter[] params = new PostParameter[1];
                    params[0] = new PostParameter("secondary_num", secondary_num);
                    String reCode = ConnectUtil.httpRequest(ConnectUtil.GetInstallmentBossManagerPerformace, params, ConnectUtil.POST);
                    Log.e("reCode", "" + reCode);
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                // worker_account = worker_list.get(position).getAccount();
               // shijian=select_time.getText().toString();
                switch (select_time.getText().toString()){
                    case "近一周业绩" :
                        worker_account = base_list_one_week.get(position).getBase_account();
                        worker_name = base_list_one_week.get(position).getBase_name();
                        range = "近一周业绩";
                        fenqi_num = base_list_one_week.get(position).getBase_fenqi_num();
                        fenqi_money = base_list_one_week.get(position).getBase_fenqi_money();
                        break;
                    case "近一月业绩" :
                        worker_account = base_list_one_month.get(position).getBase_account();
                        worker_name = base_list_one_month.get(position).getBase_name();
                        range = "近一月业绩";
                        fenqi_num = base_list_one_month.get(position).getBase_fenqi_num();
                        fenqi_money = base_list_one_month.get(position).getBase_fenqi_money();
                        break;
                    case "近一季度业绩" :
                        worker_account = base_list_three_month.get(position).getBase_account();
                        worker_name = base_list_three_month.get(position).getBase_name();
                        range = "近一季度业绩";
                        fenqi_num = base_list_three_month.get(position).getBase_fenqi_num();
                        fenqi_money = base_list_three_month.get(position).getBase_fenqi_money();
                        break;
                    case "近一年业绩" :
                        worker_account = base_list_one_year.get(position).getBase_account();
                        worker_name = base_list_one_year.get(position).getBase_name();
                        range = "近一年业绩";
                        fenqi_num = base_list_one_year.get(position).getBase_fenqi_num();
                        fenqi_money = base_list_one_year.get(position).getBase_fenqi_money();
                        break;
                }
                Intent intent = new Intent();
                intent.setClass(KSubManagerPerformanceDetailActivity.this,KCustomerManagerPerformanceActivity.class);
                intent.putExtra("worker_account",worker_account);
                intent.putExtra("worker_name",worker_name);
                intent.putExtra("range",range);
                intent.putExtra("fenqi_num",fenqi_num);
                intent.putExtra("fenqi_money",fenqi_money);

               intent.putExtra("shijian",select_time.getText().toString());
//                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
//        if()
    }
    void showSortPopWindow(){
        View root = LayoutInflater.from(KSubManagerPerformanceDetailActivity.this).inflate(R.layout.choose_time_listview,null);
        ListView lv_timeList = (ListView)root.findViewById(R.id.choose_time_listview) ;
        lv_timeList.setAdapter(chooseSortAdapter);
        pop = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        pop.setFocusable(true);
        // 不设置这句话，popWindow无法消失（点击旁边、返回键都没用）
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.showAsDropDown(select_time);
        // 在顶部显示
        pop.showAtLocation(findViewById(R.id.cativity_kbase_performance), Gravity.TOP, 0, 0);

        lv_timeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                chooseSortAdapter.setPosition(position);
                sort.setText(Constants.shengHangChooseSortList.get(position));
                switch (Constants.shengHangChooseSortList.get(position)){
                    case "成功业务数量降序" :
                        choosen_sort=base_fenqi_num;
                        switch (choosen_time){
                            case "one_week":
                                setListAndAdaper(base_list_one_week,"base_fenqi_num");
                                break;
                            case "one_month":
                                setListAndAdaper(base_list_one_month,"base_fenqi_num");
                                break;
                            case "three_month":
                                setListAndAdaper(base_list_three_month,"base_fenqi_num");
                                break;
                            case "one_year":
                                setListAndAdaper(base_list_one_year,"base_fenqi_num");
                                break;
                        }
                        break;
                    case "成功业务金额降序" :
                        choosen_sort=base_fenqi_money;
                        switch (choosen_time){
                            case "one_week":
                                setListAndAdaper(base_list_one_week,"base_fenqi_money");
                                break;
                            case "one_month":
                                setListAndAdaper(base_list_one_month,"base_fenqi_money");
                                break;
                            case "three_month":
                                setListAndAdaper(base_list_three_month,"base_fenqi_money");
                                break;
                            case "one_year":
                                setListAndAdaper(base_list_one_year,"base_fenqi_money");
                                break;
                        }
                        break;
                }
            }
        });
    }
    void setListAndAdaper(List<BaseVO> list, String name){

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

    void showTimePopWindow(){

        View root = LayoutInflater.from(KSubManagerPerformanceDetailActivity.this).inflate(R.layout.choose_time_listview,null);
        ListView lv_timeList = (ListView)root.findViewById(R.id.choose_time_listview) ;
        lv_timeList.setAdapter(chooseTimeAdapter);
        pop = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        pop.setFocusable(true);
        // 不设置这句话，popWindow无法消失（点击旁边、返回键都没用）
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.showAsDropDown(select_time);
        // 在顶部显示
        pop.showAtLocation(findViewById(R.id.cativity_kbase_performance), Gravity.TOP, 0, 0);

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
                            case "base_fenqi_num":
                                ListUtils.sort(base_list_one_week,false,"base_fenqi_num");
                                adapter.setList(base_list_one_week);
                                adapter.notifyDataSetChanged();
                                break;
                            case "base_fenqi_money":
                                setListAndAdaper(base_list_one_week,"base_fenqi_money");
                                break;

                        }
                        break;
                    case "近一月业绩" :
                        choosen_time="one_month";
                        switch (choosen_sort){
                            case "base_fenqi_num":
                                setListAndAdaper(base_list_one_month,"base_fenqi_num");
                                break;
                            case "base_fenqi_money":
                                setListAndAdaper(base_list_one_month,"base_fenqi_money");
                                break;
                        }
                        break;
                    case "近一季度业绩" :
                        choosen_time="three_month";
                        switch (choosen_sort){
                            case "base_fenqi_num":
                                setListAndAdaper(base_list_three_month,"base_fenqi_num");
                                break;
                            case "base_fenqi_money":
                                setListAndAdaper(base_list_three_month,"base_fenqi_money");
                                break;
                        }
                        break;
                    case "近一年业绩" :
                        choosen_time="one_year";
                        switch (choosen_sort){
                            case "base_fenqi_num":
                                setListAndAdaper(base_list_one_year,"base_fenqi_num");
                                break;
                            case "base_fenqi_money":
                                setListAndAdaper(base_list_one_year,"base_fenqi_money");
                                break;

                        }
                        break;
                }
            }
        });
    }
 /*   void setListAndChangeData(List<BaseVO> list){
        if(list.size()>0){
            listView.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.GONE);
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }else{
            listView.setVisibility(View.GONE);
            no_data.setVisibility(View.VISIBLE);
        }
    }*/

 /*   void showPopWindow(){
        View root = LayoutInflater.from(KSubManagerPerformanceDetailActivity.this).inflate(R.layout.choose_time_listview,null);
        ListView lv_timeList = (ListView)root.findViewById(R.id.choose_time_listview) ;
        lv_timeList.setAdapter(chooseTimeAdapter);
        pop = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        pop.setFocusable(true);
        // 不设置这句话，popWindow无法消失（点击旁边、返回键都没用）
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.showAsDropDown(select_time);
        // 在顶部显示
        pop.showAtLocation(findViewById(R.id.cativity_kbase_performance), Gravity.TOP, 0, 0);

        lv_timeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                chooseTimeAdapter.setPosition(position);
                select_time.setText(Constants.chooseTimeList.get(position));
                switch (Constants.chooseTimeList.get(position)){
                    case "近一周业绩" :
                        choosen_time="one_week";
                        setListAndChangeData(base_list_one_week);
                        break;
                    case "近一月业绩" :
                        choosen_time="one_month";
                        setListAndChangeData(base_list_one_month);
                        break;
                    case "近一季度业绩" :
                        choosen_time="three_month";
                        setListAndChangeData(base_list_three_month);
                        break;
                    case "近一年业绩" :
                        choosen_time="one_year";
                        setListAndChangeData(base_list_one_year);
                        break;
                }
            }
        });

    }*/


}
