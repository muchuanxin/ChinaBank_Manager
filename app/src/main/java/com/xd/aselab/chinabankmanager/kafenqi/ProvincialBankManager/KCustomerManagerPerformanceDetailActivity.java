package com.xd.aselab.chinabankmanager.kafenqi.ProvincialBankManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.model.WorkerVO;
import com.xd.aselab.chinabankmanager.kafenqi.manager.adapter.BasePerfDetailAdapter;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.Constants;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;
import com.xd.aselab.chinabankmanager.activity.BaseInfoTransparent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class KCustomerManagerPerformanceDetailActivity extends AppCompatActivity {

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
    private String current_worker_account="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kcustomer_manager_performance_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initViews();
        initDatas();
        getDataFormServer();
        initEvent();

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
        spu = new SharePreferenceUtil(KCustomerManagerPerformanceDetailActivity.this,"user");
        range = "4S店销售"+getIntent().getStringExtra("range");
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
                                    Toast.makeText(KCustomerManagerPerformanceDetailActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
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
                                            workerVO.setStatus(temp.getString("status"));
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
                                            workerVO.setStatus(temp.getString("status"));
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
                                            workerVO.setStatus(temp.getString("status"));
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
                                            workerVO.setStatus(temp.getString("status"));
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
                                    adapter = new BasePerfDetailAdapter(worker_list_one_week,KCustomerManagerPerformanceDetailActivity.this);
                                    listView.setAdapter(adapter);
                                    DecimalFormat df = new DecimalFormat("#0.0000");
                                    switch (range){
                                        case "4S店销售近一周业绩":
                                            base_red_total.setText("在"+worker_name+"发展的"+oneWeekJA.length()+"位4S店销售中，共成功办理"+num_one_week_sum+"笔分期业务，总金额为"+df.format(money_one_week_sum)+"万元");
                                            setListview(worker_list_one_week);
                                            break;
                                        case "4S店销售近一月业绩":
                                            base_red_total.setText("在"+worker_name+"发展的"+oneMonthJA.length()+"位4S店销售中，共成功办理"+num_one_month_sum+"笔分期业务，总金额为"+df.format(money_one_month_sum)+"万元");
                                            setListview(worker_list_one_month);
                                            break;
                                        case "4S店销售近一季度业绩":
                                            base_red_total.setText("在"+worker_name+"发展的"+threeMonthJA.length()+"位4S店销售中，共成功办理"+num_three_month_sum+"笔分期业务，总金额为"+df.format(money_three_month_sum)+"万元");
                                            setListview(worker_list_three_month);
                                            break;
                                        case "4S店销售近一年业绩":
                                            base_red_total.setText("在"+worker_name+"发展的"+oneYearJA.length()+"位4S店销售中，共成功办理"+num_one_year_sum+"笔分期业务，总金额为"+df.format(money_one_year_sum)+"万元");
                                            setListview(worker_list_one_year);
                                            break;

                                    }

                                } else {
                                    Log.e("KMyPreformenceActivity", KCustomerManagerPerformanceDetailActivity.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(KCustomerManagerPerformanceDetailActivity.this, KCustomerManagerPerformanceDetailActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("KMyPreformenceActivity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                Log.e("MyPerformance：reCode", reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                //worker_list_one_week = new ArrayList<>();
                                if ("false".equals(status)) {
                                    Toast.makeText(KCustomerManagerPerformanceDetailActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    Toast.makeText(KCustomerManagerPerformanceDetailActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                     worker_list_one_week= new ArrayList<>();
                                     worker_list_one_month= new ArrayList<>();
                                     worker_list_three_month= new ArrayList<>();
                                    worker_list_one_year= new ArrayList<>();
                                    getDataFormServer();
                                } else {
                                    Log.e("KMyPreformenceActivity", KCustomerManagerPerformanceDetailActivity.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(KCustomerManagerPerformanceDetailActivity.this, KCustomerManagerPerformanceDetailActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("KMyPreformenceActivity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;

                        default:
                        Log.e("KMyPreformenceActivity", KCustomerManagerPerformanceDetailActivity.this.getResources().getString(R.string.handler_what_exception));
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
    void initEvent()
    {

       /*
       //  将二级行与4S店解约（通过4S店销售账号） 已删除
       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                startActivityForResult(new Intent().setClass(KCustomerManagerPerformanceDetailActivity.this, BaseInfoTransparent.class).putExtra("position",position).putExtra("fromwhere","KCustomerManagerPerformanceDetailActivity").putExtra("title",title.getText()), 104);

                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });*/

    };


    /*
    //  将二级行与4S店解约（通过4S店销售账号） 已删除
    @Override
    protected  void onActivityResult(int requestCode, int resultCode, final Intent intent){
        if(140==resultCode){

            int i=intent.getIntExtra("position", 0);

            switch (range){
                case "4S店销售近一周业绩":
                    current_worker_account=worker_list_one_week.get(i).getAccount();
                    break;
                case "4S店销售近一月业绩":
                    current_worker_account=worker_list_one_month.get(i).getAccount();
                    break;
                case "4S店销售近一季度业绩":
                    current_worker_account=worker_list_three_month.get(i).getAccount();
                    break;
                case "4S店销售近一年业绩":
                    current_worker_account=worker_list_one_year.get(i).getAccount();
                    break;

            }


            new Thread(){
                @Override
                public void run() {
                    super.run();
                    final PostParameter[] params = new PostParameter[1];
                    params[0] = new PostParameter("worker_account", current_worker_account);
                    String reCode = ConnectUtil.httpRequest(ConnectUtil.ForceReleaseBetweenBankAnd4SShop, params, ConnectUtil.POST);
                    Log.e("reCode",""+reCode);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = reCode;
                    handler.sendMessage(msg);
                }
            }.start();


        }
        super.onActivityResult(requestCode, resultCode, intent);
    }*/
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
