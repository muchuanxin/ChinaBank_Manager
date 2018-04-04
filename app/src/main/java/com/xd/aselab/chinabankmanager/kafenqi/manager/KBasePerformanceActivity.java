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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.kafenqi.manager.adapter.BasePerfToAdapter;
import com.xd.aselab.chinabankmanager.kafenqi.manager.model.BaseVO;
import com.xd.aselab.chinabankmanager.util.ChooseTimeAdapter;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.Constants;
import com.xd.aselab.chinabankmanager.util.ListUtils;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class KBasePerformanceActivity extends AppCompatActivity {

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
    private BasePerfToAdapter adapter;
    private ChooseTimeAdapter chooseTimeAdapter;
    private PopupWindow pop;
    private RelativeLayout rl_choose_time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kbase_performance);
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
    }

    void initDatas(){
        spu = new SharePreferenceUtil(KBasePerformanceActivity.this, "user");
        select_string = new String[]{"近一周业绩", "近一月业绩", "近一季度业绩", "近一年业绩"};
        chooseTimeAdapter = new ChooseTimeAdapter(Constants.chooseTimeList,KBasePerformanceActivity.this);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0 :
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                Log.e("KBasePerformance：reCode", reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                //worker_list_one_week = new ArrayList<>();
                                if ("false".equals(status)) {
                                    Toast.makeText(KBasePerformanceActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
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
                                    if(oneWeekJA.length()>0){
                                        for (int i = 0; i < oneWeekJA.length(); i++) {
                                            JSONObject temp = (JSONObject) oneWeekJA.get(i);
                                            BaseVO baseVO = new BaseVO();
                                            baseVO.setBase_account(temp.getString("account"));
                                            baseVO.setBase_name(temp.getString("name"));
                                            baseVO.setbase_fenqi_confirm_num(temp.getInt("confirmNumber"));
                                            baseVO.setbase_fenqi_refuse_num(temp.getInt("refuseNumber"));
                                            baseVO.setbase_fenqi_remark_num(temp.getInt("remarkNumber"));
                                            baseVO.setBase_fenqi_num(temp.getInt("successNumber"));
                                            baseVO.setBase_fenqi_money(temp.getDouble("money"));
                                            base_list_one_week.add(baseVO);
                                        }
                                    }

                                    //worker_list_one_month = new ArrayList<>();
                                    if(oneMonthJA.length()>0){
                                        for (int i = 0; i < oneMonthJA.length(); i++) {
                                            JSONObject temp = (JSONObject) oneMonthJA.get(i);
                                            BaseVO baseVO = new BaseVO();
                                            baseVO.setBase_account(temp.getString("account"));
                                            baseVO.setBase_name(temp.getString("name"));
                                            baseVO.setbase_fenqi_confirm_num(temp.getInt("confirmNumber"));
                                            baseVO.setbase_fenqi_refuse_num(temp.getInt("refuseNumber"));
                                            baseVO.setbase_fenqi_remark_num(temp.getInt("remarkNumber"));
                                            baseVO.setBase_fenqi_num(temp.getInt("successNumber"));
                                            baseVO.setBase_fenqi_money(temp.getDouble("money"));
                                            base_list_one_month.add(baseVO);
                                        }
                                    }

                                    // worker_list_three_month = new ArrayList<>();
                                    if(threeMonthJA.length()>0){
                                        for (int i = 0; i < threeMonthJA.length(); i++) {
                                            JSONObject temp = (JSONObject) threeMonthJA.get(i);
                                            BaseVO baseVO = new BaseVO();
                                            baseVO.setBase_account(temp.getString("account"));
                                            baseVO.setBase_name(temp.getString("name"));
                                            baseVO.setbase_fenqi_confirm_num(temp.getInt("confirmNumber"));
                                            baseVO.setbase_fenqi_refuse_num(temp.getInt("refuseNumber"));
                                            baseVO.setbase_fenqi_remark_num(temp.getInt("remarkNumber"));
                                            baseVO.setBase_fenqi_num(temp.getInt("successNumber"));
                                            baseVO.setBase_fenqi_money(temp.getDouble("money"));
                                            base_list_three_month.add(baseVO);
                                        }
                                    }

                                    // worker_list_one_year = new ArrayList<>();
                                    if(oneYearJA.length()>0){
                                        for (int i = 0; i < oneYearJA.length(); i++) {
                                            JSONObject temp = (JSONObject) oneYearJA.get(i);
                                            BaseVO baseVO = new BaseVO();
                                            baseVO.setBase_account(temp.getString("account"));
                                            baseVO.setBase_name(temp.getString("name"));
                                            baseVO.setbase_fenqi_confirm_num(temp.getInt("confirmNumber"));
                                            baseVO.setbase_fenqi_refuse_num(temp.getInt("refuseNumber"));
                                            baseVO.setbase_fenqi_remark_num(temp.getInt("remarkNumber"));
                                            baseVO.setBase_fenqi_num(temp.getInt("successNumber"));
                                            baseVO.setBase_fenqi_money(temp.getDouble("money"));
                                            base_list_one_year.add(baseVO);
                                        }
                                    }

                                    adapter = new BasePerfToAdapter(base_list_one_week,KBasePerformanceActivity.this);
                                    listView.setAdapter(adapter);

                                    if(base_list_one_week.size()>0){
                                        listView.setVisibility(View.VISIBLE);
                                        no_data.setVisibility(View.GONE);

                                    }else{
                                        listView.setVisibility(View.GONE);
                                        no_data.setVisibility(View.VISIBLE);
                                    }

                                } else {
                                    Log.e("KMyPreformenceActivity", KBasePerformanceActivity.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(KBasePerformanceActivity.this, KBasePerformanceActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("KMyPreformenceActivity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("KMyPreformenceActivity", KBasePerformanceActivity.this.getResources().getString(R.string.handler_what_exception));
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
                showPopWindow();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String worker_account = "";
                String worker_name = "";
                String range = "";

                int fenqi_num = 0;
                double fenqi_money = 0;
                // worker_account = worker_list.get(position).getAccount();
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
                intent.setClass(KBasePerformanceActivity.this,KafenqiBasePerfDetailActivity.class);
                intent.putExtra("worker_account",worker_account);
                intent.putExtra("worker_name",worker_name);
                intent.putExtra("range",range);
                intent.putExtra("fenqi_num",fenqi_num);
                intent.putExtra("fenqi_money",fenqi_money);

                startActivity(intent);
            }
        });
    }

    void setListAndChangeData(List<BaseVO> list){
        if(list.size()>0){
            listView.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.GONE);
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }else{
            listView.setVisibility(View.GONE);
            no_data.setVisibility(View.VISIBLE);
        }
    }

    void showPopWindow(){
        View root = LayoutInflater.from(KBasePerformanceActivity.this).inflate(R.layout.choose_time_listview,null);
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

    }


}
