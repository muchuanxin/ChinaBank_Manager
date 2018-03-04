package com.xd.aselab.chinabankmanager.kafenqi.manager;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.Text;
import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.model.WorkerVO;
import com.xd.aselab.chinabankmanager.kafenqi.manager.adapter.BasePerfDetailAdapter;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class KafenqiBasePerfDetailActivity extends AppCompatActivity {

    private RelativeLayout back;
    private TextView title;
    private TextView base_name;
    private TextView base_job_number;
    private TextView base_red_total;
    private ListView listView;
    private LinearLayout no_data;
    private SharePreferenceUtil spu;
    private List<WorkerVO> list;
    private String range = "one_week";
    private String worker_account = "";
    private String worker_name = "";
    private int fenqi_num = 0;
    private double fenqi_money = 0;
    private Handler handler;
    private List<WorkerVO> worker_list_one_week= new ArrayList<>();
    private List<WorkerVO> worker_list_one_month= new ArrayList<>();
    private List<WorkerVO> worker_list_three_month= new ArrayList<>();
    private List<WorkerVO> worker_list_one_year= new ArrayList<>();
    private BasePerfDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kafenqi_base_perf_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initViews();
        initDatas();
        getDataFormServer();

    }

    void initViews(){

        back = (RelativeLayout)findViewById(R.id.act_kafenqi_base_perf_detail_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title = (TextView)findViewById(R.id.act_kafenqi_base_perf_detail_title);
        base_name = (TextView)findViewById(R.id.act_kafenqi_base_perf_detail_name);
        base_job_number = (TextView)findViewById(R.id.act_kafenqi_base_perf_detail_job_number);
        base_red_total = (TextView)findViewById(R.id.act_kafenqi_base_perf_detail_red_total);
        listView = (ListView)findViewById(R.id.act_kafenqi_base_perf_detail_list_view);
        no_data = (LinearLayout)findViewById(R.id.kafenqi_base_perf_detail_no_data);
    }

    void initDatas(){
        spu = new SharePreferenceUtil(KafenqiBasePerfDetailActivity.this,"user");
        range = getIntent().getStringExtra("range");
        worker_account = getIntent().getStringExtra("worker_account");
        worker_name = getIntent().getStringExtra("worker_name");
        fenqi_num = getIntent().getIntExtra("fenqi_num",0);
        fenqi_money = getIntent().getDoubleExtra("fenqi_money",0);
        title.setText(range);
        base_name.setText(worker_name);
        base_job_number.setText("工号："+worker_account);

    }

    void getDataFormServer(){
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
                                    Toast.makeText(KafenqiBasePerfDetailActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONArray oneWeekJA = json.getJSONArray("one_week");
                                    JSONArray oneMonthJA = json.getJSONArray("one_month");
                                    JSONArray threeMonthJA = json.getJSONArray("three_month");
                                    JSONArray oneYearJA = json.getJSONArray("one_year");
                                    //JSONObject oneMonthJO = json.getJSONObject("one_month");
                                    //Log.e("this_year", this_year.toString());
                                    int num_one_week_sum=0;
                                    double money_one_week_sum=0.0;
                                    if(oneWeekJA.length()>0){
                                        for (int i = 0; i < oneWeekJA.length(); i++) {
                                            JSONObject temp = (JSONObject) oneWeekJA.get(i);
                                            WorkerVO workerVO = new WorkerVO();
                                            workerVO.setAccount(temp.getString("account"));
                                            workerVO.setName(temp.getString("name"));
                                            workerVO.setWorker_address(temp.getString("company"));
                                            workerVO.setRecommend_number(temp.getInt("recommend_num"));
                                            workerVO.setSuccess_number(temp.getInt("success_num"));
                                            workerVO.setSuccess_money(temp.getDouble("success_money"));
                                            workerVO.setSum_credit(temp.getInt("score"));
                                            workerVO.setExchange_credit(temp.getInt("exchange_score"));
                                            num_one_week_sum+=temp.getInt("success_num");
                                            money_one_week_sum+=temp.getInt("success_money");
                                            double rate = 0;
                                            if (temp.getInt("recommend_num") > 0) {
                                                rate = temp.getInt("success_num") / temp.getInt("recommend_num");
                                            }
                                            workerVO.setSuccess_rate(rate);
                                            worker_list_one_week.add(workerVO);
                                        }
                                    }

                                    //worker_list_one_month = new ArrayList<>();
                                    int num_one_month_sum=0;
                                    double money_one_month_sum=0.0;
                                    if(oneMonthJA.length()>0){
                                        for (int i = 0; i < oneMonthJA.length(); i++) {
                                            JSONObject temp = (JSONObject) oneMonthJA.get(i);
                                            WorkerVO workerVO = new WorkerVO();
                                            workerVO.setAccount(temp.getString("account"));
                                            workerVO.setName(temp.getString("name"));
                                            workerVO.setWorker_address(temp.getString("company"));
                                            workerVO.setRecommend_number(temp.getInt("recommend_num"));
                                            workerVO.setSuccess_number(temp.getInt("success_num"));
                                            workerVO.setSuccess_money(temp.getDouble("success_money"));
                                            workerVO.setSum_credit(temp.getInt("score"));
                                            workerVO.setExchange_credit(temp.getInt("exchange_score"));
                                            double rate = 0;
                                            if (temp.getInt("recommend_num") > 0) {
                                                rate = temp.getInt("success_num") / temp.getInt("recommend_num");
                                            }
                                            num_one_month_sum+=temp.getInt("success_num");
                                            money_one_month_sum+=temp.getDouble("success_money");
                                            workerVO.setSuccess_rate(rate);
                                            worker_list_one_month.add(workerVO);
                                        }
                                    }

                                    // worker_list_three_month = new ArrayList<>();
                                    int num_three_month_sum=0;
                                    double money_three_month_sum=0.0;
                                    if(threeMonthJA.length()>0){
                                        for (int i = 0; i < threeMonthJA.length(); i++) {
                                            JSONObject temp = (JSONObject) threeMonthJA.get(i);
                                            WorkerVO workerVO = new WorkerVO();
                                            workerVO.setAccount(temp.getString("account"));
                                            workerVO.setName(temp.getString("name"));
                                            workerVO.setWorker_address(temp.getString("company"));
                                            workerVO.setRecommend_number(temp.getInt("recommend_num"));
                                            workerVO.setSuccess_number(temp.getInt("success_num"));
                                            workerVO.setSuccess_money(temp.getDouble("success_money"));
                                            workerVO.setSum_credit(temp.getInt("score"));
                                            workerVO.setExchange_credit(temp.getInt("exchange_score"));
                                            double rate = 0;
                                            if (temp.getInt("recommend_num") > 0) {
                                                rate = temp.getInt("success_num") / temp.getInt("recommend_num");
                                            }
                                            num_three_month_sum+=temp.getInt("success_num");
                                            money_three_month_sum+=temp.getDouble("success_money");
                                            workerVO.setSuccess_rate(rate);
                                            worker_list_three_month.add(workerVO);
                                        }
                                    }

                                    // worker_list_one_year = new ArrayList<>();
                                    int num_one_year_sum=0;
                                    double money_one_year_sum=0.0;
                                    if(oneYearJA.length()>0){
                                        for (int i = 0; i < oneYearJA.length(); i++) {
                                            JSONObject temp = (JSONObject) oneYearJA.get(i);
                                            WorkerVO workerVO = new WorkerVO();
                                            workerVO.setAccount(temp.getString("account"));
                                            workerVO.setName(temp.getString("name"));
                                            workerVO.setWorker_address(temp.getString("company"));
                                            workerVO.setRecommend_number(temp.getInt("recommend_num"));
                                            workerVO.setSuccess_number(temp.getInt("success_num"));
                                            workerVO.setSuccess_money(temp.getDouble("success_money"));
                                            workerVO.setSum_credit(temp.getInt("score"));
                                            workerVO.setExchange_credit(temp.getInt("exchange_score"));
                                            double rate = 0;
                                            if (temp.getInt("recommend_num") > 0) {
                                                rate = temp.getInt("success_num") / temp.getInt("recommend_num");
                                            }
                                            num_one_year_sum +=temp.getInt("success_num");
                                            money_one_year_sum+=temp.getDouble("success_money");
                                            workerVO.setSuccess_rate(rate);
                                            worker_list_one_year.add(workerVO);
                                        }
                                    }
                                    if(worker_list_one_week.size()>0){
                                        listView.setVisibility(View.VISIBLE);
                                        no_data.setVisibility(View.GONE);

                                    }else{
                                        listView.setVisibility(View.GONE);
                                        no_data.setVisibility(View.VISIBLE);
                                    }
                                    adapter = new BasePerfDetailAdapter(worker_list_one_week,KafenqiBasePerfDetailActivity.this);
                                    listView.setAdapter(adapter);
                                    DecimalFormat df = new DecimalFormat("#0.00");
                                    switch (range){
                                        case "近一周业绩":
                                            base_red_total.setText("在"+worker_name+"发展的"+oneWeekJA.length()+"位4S店销售中，共成功办理"+num_one_week_sum+"笔分期业务，总金额为"+df.format(money_one_week_sum)+"万元");
                                            setListview(worker_list_one_week);
                                            break;
                                        case "近一月业绩":
                                            base_red_total.setText("在"+worker_name+"发展的"+oneMonthJA.length()+"位4S店销售中，共成功办理"+num_one_month_sum+"笔分期业务，总金额为"+df.format(money_one_month_sum)+"万元");
                                            setListview(worker_list_one_month);
                                            break;
                                        case "近一季度业绩":
                                            base_red_total.setText("在"+worker_name+"发展的"+threeMonthJA.length()+"位4S店销售中，共成功办理"+num_three_month_sum+"笔分期业务，总金额为"+df.format(money_three_month_sum)+"万元");
                                            setListview(worker_list_three_month);
                                            break;
                                        case "近一年业绩":
                                            base_red_total.setText("在"+worker_name+"发展的"+oneYearJA.length()+"位4S店销售中，共成功办理"+num_one_year_sum+"笔分期业务，总金额为"+df.format(money_one_year_sum)+"万元");
                                            setListview(worker_list_one_year);
                                            break;

                                    }

                                } else {
                                    Log.e("KMyPreformenceActivity", KafenqiBasePerfDetailActivity.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(KafenqiBasePerfDetailActivity.this, KafenqiBasePerfDetailActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("KMyPreformenceActivity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("KMyPreformenceActivity", KafenqiBasePerfDetailActivity.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

        new Thread(){
            @Override
            public void run() {
                super.run();
                final PostParameter[] params = new PostParameter[1];
                params[0] = new PostParameter("account", worker_account);
                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetInstallmentWorkerPerformance, params, ConnectUtil.POST);
                Log.e("reCode",""+reCode);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();

    }

    void setListview(List<WorkerVO> list){
        if(list.size()>0){
            adapter.setList(list);
            adapter.notifyDataSetChanged();
            listView.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.GONE);
        }else{
            listView.setVisibility(View.GONE);
            no_data.setVisibility(View.VISIBLE);
        }
    }

}
