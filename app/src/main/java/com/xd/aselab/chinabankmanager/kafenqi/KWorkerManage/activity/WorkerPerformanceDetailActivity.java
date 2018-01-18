package com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.util.ChooseTimeAdapter;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.Constants;
import com.xd.aselab.chinabankmanager.util.MyMarkerView;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class WorkerPerformanceDetailActivity extends AppCompatActivity  implements OnChartValueSelectedListener {

    private TextView select_time;
    private TextView tv_name;
    private LinearLayout ll_chart;
    private LinearLayout no_data;
    private TextView tv_worker_status;
    private RelativeLayout rl_detail_recommend;
    private ImageView right_arrow;
    private TextView tv_recommend_num;
    private TextView tv_success_num;
    private TextView tv_success_money;
    private TextView tv_credit_sum;
    private TextView tv_credit_exchange;

    private RelativeLayout back;
    private TextView gray_bar;
    private BarChart mChart;
    private Typeface mTfRegular;
    protected Typeface mTfLight;
    private SharePreferenceUtil spu;
    private Handler handler;
    private String[] select_string;
    private float[] numbers = new float[12];
    private float[] money = new float[12];
    private String choosen_time = "one_week";
    private String worker_account,worker_name,worker_status;
    private int recommend_num_one_week,success_num_one_week,success_money_one_week,score_one_week,exchange_score_one_week;
    private int recommend_num_one_month,success_num_one_month,success_money_one_month,score_one_month,exchange_score_one_month;
    private int recommend_num_three_month,success_num_three_month,success_money_three_month,score_three_month,exchange_score_three_month;
    private int recommend_num_one_year,success_num_one_year,success_money_one_year,score_one_year,exchange_score_one_year;
    private ChooseTimeAdapter chooseTimeAdapter;
    private PopupWindow pop;
    private RelativeLayout rl_choose_time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_performance_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initViews();
        initDatas();
        initEvents();
        configChart();
    }

    void initViews(){
        back = (RelativeLayout)findViewById(R.id.act_worker_perf_detail_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ll_chart = (LinearLayout)findViewById(R.id.act_worker_perf_detail_chat_ll);
        no_data = (LinearLayout) findViewById(R.id.act_worker_perf_no_data);
        select_time = (TextView)findViewById(R.id.act_worker_perf_detail_select_time);
        tv_name = (TextView)findViewById(R.id.detail_name);
        tv_worker_status = (TextView)findViewById(R.id.detail_worker_status);
        rl_detail_recommend = (RelativeLayout)findViewById(R.id.detail_go_recommend_detail);
        right_arrow = (ImageView)findViewById(R.id.iv_worker_performance_detail_right_arrow);
        tv_recommend_num = (TextView)findViewById(R.id.detail_recommend_num);
        tv_success_num = (TextView)findViewById(R.id.detail_success_num);
        tv_success_money = (TextView)findViewById(R.id.detail_success_money);
        tv_credit_sum = (TextView)findViewById(R.id.detail_sum_credit);
        tv_credit_exchange = (TextView)findViewById(R.id.detail_exchange_credit);
        gray_bar = (TextView)findViewById(R.id.act_worker_perf_detail_gray_bar);
        mChart = (BarChart) findViewById(R.id.chart_detail);
    }

    void initDatas(){
        worker_name = getIntent().getStringExtra("worker_name");
        worker_account = getIntent().getStringExtra("worker_account");
        worker_status = getIntent().getStringExtra("worker_status");
        chooseTimeAdapter = new ChooseTimeAdapter(Constants.chooseTimeList,WorkerPerformanceDetailActivity.this);

        switch (worker_status){
            case "已加盟":
                tv_worker_status.setVisibility(View.GONE);
                break;
            case "正在申请解约":
                tv_worker_status.setVisibility(View.VISIBLE);
                tv_worker_status.setText(worker_status);
                break;
            case "已解约":
                tv_worker_status.setVisibility(View.VISIBLE);
                tv_worker_status.setText(worker_status);
                break;
        }

        gray_bar.setText(Calendar.getInstance().get(Calendar.YEAR)+"年各月分期业务情况分析");

        spu = new SharePreferenceUtil(WorkerPerformanceDetailActivity.this, "user");
        select_string = new String[]{"近一周业绩", "近一月业绩", "近一季度业绩", "近一年业绩"};
        final PostParameter[] params = new PostParameter[1];
        params[0] = new PostParameter("worker_account", worker_account);
        new Thread(){
            @Override
            public void run() {
                super.run();
                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetInstallmentWorkerPerformanceDetail, params, ConnectUtil.POST);
                Log.e("reCode",""+reCode);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();
    }

    void initEvents(){
        tv_name.setText(""+worker_name);

        rl_detail_recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(WorkerPerformanceDetailActivity.this,RecommendListActivity.class);
                intent.putExtra("worker_account",worker_account);
                intent.putExtra("range",choosen_time);
                startActivity(intent);
            }
        });

        select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow();
                //startActivityForResult(new Intent().setClass(PerformanceRanking.this, PerfRankTransparent.class),102);
                /*AlertDialog.Builder builder = new AlertDialog.Builder(WorkerPerformanceDetailActivity.this);
                builder.setTitle("选择时间范围");
                builder.setItems(select_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        select_time.setText(select_string[which]);

                        switch (select_string[which]){
                            case "近一周业绩" :
                                choosen_time="one_week";
                                setText(recommend_num_one_week,success_num_one_week,success_money_one_week,score_one_week,exchange_score_one_week);
                                if(recommend_num_one_week==0){
                                    rl_detail_recommend.setClickable(false);
                                    right_arrow.setVisibility(View.GONE);
                                }else{
                                    rl_detail_recommend.setClickable(true);
                                    right_arrow.setVisibility(View.VISIBLE);
                                }
                                break;
                            case "近一月业绩" :
                                choosen_time="one_month";
                                setText(recommend_num_one_month,success_num_one_month,success_money_one_month,score_one_month,exchange_score_one_month);
                                if(recommend_num_one_month==0){
                                    rl_detail_recommend.setClickable(false);
                                    right_arrow.setVisibility(View.GONE);
                                }else{
                                    rl_detail_recommend.setClickable(true);
                                    right_arrow.setVisibility(View.VISIBLE);
                                }
                                break;
                            case "近三月业绩" :
                                choosen_time="three_month";
                                setText(recommend_num_three_month,success_num_three_month,success_money_three_month,score_three_month,exchange_score_three_month);
                                if(recommend_num_three_month==0){
                                    rl_detail_recommend.setClickable(false);
                                    right_arrow.setVisibility(View.GONE);
                                }else{
                                    rl_detail_recommend.setClickable(true);
                                    right_arrow.setVisibility(View.VISIBLE);
                                }
                                break;
                            case "近一年业绩" :
                                choosen_time="one_year";
                                setText(recommend_num_one_year,success_num_one_year,success_money_one_year,score_one_year,exchange_score_one_year);
                                if(recommend_num_one_year==0){
                                    rl_detail_recommend.setClickable(false);
                                    right_arrow.setVisibility(View.GONE);
                                }else{
                                    rl_detail_recommend.setClickable(true);
                                    right_arrow.setVisibility(View.VISIBLE);
                                }
                                break;
                        }
                    }
                });
                builder.show();*/
            }
        });

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
                                if ("false".equals(status)) {
                                    Toast.makeText(WorkerPerformanceDetailActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONObject oneWeekJO = json.getJSONObject("one_week");
                                    JSONObject oneMonthJO = json.getJSONObject("one_month");
                                    JSONObject threeMonthJO = json.getJSONObject("three_month");
                                    JSONObject oneYearJO = json.getJSONObject("one_year");
                                    JSONObject thisYearJO = json.getJSONObject("this_year");
                                    //Log.e("this_year", this_year.toString());
                                    // float[] numbers = new float[12];
                                    recommend_num_one_week = oneWeekJO.getInt("recommend_num");
                                    success_num_one_week = oneWeekJO.getInt("success_num");
                                    success_money_one_week= oneWeekJO.getInt("success_money");
                                    score_one_week= oneWeekJO.getInt("score");
                                    exchange_score_one_week= oneWeekJO.getInt("exchange_score");

                                    recommend_num_one_month = oneMonthJO.getInt("recommend_num");
                                    success_num_one_month = oneMonthJO.getInt("success_num");
                                    success_money_one_month= oneMonthJO.getInt("success_money");
                                    score_one_month= oneMonthJO.getInt("score");
                                    exchange_score_one_month= oneMonthJO.getInt("exchange_score");

                                    recommend_num_three_month = threeMonthJO.getInt("recommend_num");
                                    success_num_three_month = threeMonthJO.getInt("success_num");
                                    success_money_three_month= threeMonthJO.getInt("success_money");
                                    score_three_month= threeMonthJO.getInt("score");
                                    exchange_score_three_month= threeMonthJO.getInt("exchange_score");

                                    recommend_num_one_year = oneYearJO.getInt("recommend_num");
                                    success_num_one_year = oneYearJO.getInt("success_num");
                                    success_money_one_year= oneYearJO.getInt("success_money");
                                    score_one_year= oneYearJO.getInt("score");
                                    exchange_score_one_year= oneYearJO.getInt("exchange_score");

                                    setText(recommend_num_one_week,success_num_one_week,success_money_one_week,score_one_week,exchange_score_one_week);
                                    if(recommend_num_one_week==0){
                                        rl_detail_recommend.setClickable(false);
                                        right_arrow.setVisibility(View.GONE);
                                    }else{
                                        rl_detail_recommend.setClickable(true);
                                        right_arrow.setVisibility(View.VISIBLE);
                                    }

                                    boolean flag = false;
                                    for (int i=0; i<12; i++){
                                        //numbers[i] = thisYearJO.getInt(""+(i+1));
                                        numbers[i]=thisYearJO.getJSONObject(""+(i+1)).getInt("number");
                                        money[i] = thisYearJO.getJSONObject(""+(i+1)).getInt("money");
                                        if (numbers[i]>0){
                                            flag = true;
                                        }
                                    }

                                    if (! flag){
                                        ll_chart.setVisibility(View.GONE);
                                        no_data.setVisibility(View.VISIBLE);
                                        //gray_bar.setVisibility(View.GONE);
                                        //chart.setVisibility(View.GONE);
                                       // Toast.makeText(WorkerPerformanceDetailActivity.this, "还没有业绩信息~", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        initChartDatas();
                                        no_data.setVisibility(View.GONE);
                                        ll_chart.setVisibility(View.VISIBLE);
                                       // gray_bar.setVisibility(View.VISIBLE);

                                    }
                                } else {
                                    Log.e("KMyPreformenceActivity", WorkerPerformanceDetailActivity.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(WorkerPerformanceDetailActivity.this, WorkerPerformanceDetailActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("KMyPreformenceActivity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("KMyPreformenceActivity", WorkerPerformanceDetailActivity.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };


    }

    private void setText(int recommend_num,int success_num,int success_money,int score,int exchange_score){
        tv_recommend_num.setText("推荐分期业务数量："+("".equals(recommend_num)? "暂无" : recommend_num+"笔"));
        tv_success_num.setText("分期业务成功数量："+("".equals(success_num)? "暂无" : success_num+"笔"));
        tv_success_money.setText("分期业务成功金额："+("".equals(success_money)? "暂无" : success_money+"元"));
        tv_credit_sum.setText("总积分数："+("".equals(score)? "暂无" : score));
        tv_credit_exchange.setText("已兑换积分："+("".equals(exchange_score)? "暂无" : exchange_score));
    }

    private void initChartDatas(){
        float groupSpace = 0.14f;
        float barSpace = 0.03f; // x4 DataSet
        float barWidth = 0.4f; // x4 DataSet
        // (0.2 + 0.03) * 4 + 0.08 = 1.00 -> interval per "group"

        int startYear = 1;
        int endYear = startYear + 12;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals3 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals4 = new ArrayList<BarEntry>();

        for (int i = startYear; i < endYear; i++) {
            switch (i){
                case 1:
                    yVals1.add(new BarEntry(i, money[i-1]));
                    yVals2.add(new BarEntry(i, numbers[i-1]));
                    break;
                case 2:
                    yVals1.add(new BarEntry(i, money[i-1]));
                    yVals2.add(new BarEntry(i, numbers[i-1]));
                    break;
                case 3:
                    yVals1.add(new BarEntry(i, money[i-1]));
                    yVals2.add(new BarEntry(i, numbers[i-1]));
                    break;
                case 4:
                    yVals1.add(new BarEntry(i, money[i-1]));
                    yVals2.add(new BarEntry(i, numbers[i-1]));
                    break;
                case 5:
                    yVals1.add(new BarEntry(i, money[i-1]));
                    yVals2.add(new BarEntry(i, numbers[i-1]));
                    break;
                case 6:
                    yVals1.add(new BarEntry(i, money[i-1]));
                    yVals2.add(new BarEntry(i, numbers[i-1]));
                    break;
                case 7:
                    yVals1.add(new BarEntry(i, money[i-1]));
                    yVals2.add(new BarEntry(i, numbers[i-1]));
                    break;
                case 8:
                    yVals1.add(new BarEntry(i, money[i-1]));
                    yVals2.add(new BarEntry(i, numbers[i-1]));
                    break;
                case 9:
                    yVals1.add(new BarEntry(i, money[i-1]));
                    yVals2.add(new BarEntry(i, numbers[i-1]));
                    break;
                case 10:
                    yVals1.add(new BarEntry(i, money[i-1]));
                    yVals2.add(new BarEntry(i, numbers[i-1]));
                    break;
                case 11:
                    yVals1.add(new BarEntry(i, money[i-1]));
                    yVals2.add(new BarEntry(i, numbers[i-1]));
                    break;
                default:
                    yVals1.add(new BarEntry(i, (float)0.00));
                    yVals2.add(new BarEntry(i, 2));
                    break;
            }
//            yVals1.add(new BarEntry(i, 10));
//            yVals2.add(new BarEntry(i, 2));
//            yVals3.add(new BarEntry(i, (float) (Math.random() * 1)));
//            yVals4.add(new BarEntry(i, (float) (Math.random() * 1)));
        }

        BarDataSet set1, set2;
        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {

            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) mChart.getData().getDataSetByIndex(1);

            set1.setValues(yVals1);
            set2.setValues(yVals2);

            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();

        } else {
            // create 2 DataSets
            set1 = new BarDataSet(yVals1, "分期总额(元)");
            set1.setColor(Color.rgb(255, 133, 133));
            set2 = new BarDataSet(yVals2, "业务量(笔)");
            set2.setColor(Color.rgb(98, 188, 255));

            BarData data = new BarData(set1, set2);
            data.setValueFormatter(new LargeValueFormatter());
            data.setValueTypeface(mTfLight);

            mChart.setData(data);
        }

        // specify the width each bar should have
        mChart.getBarData().setBarWidth(barWidth);

        // restrict the x-axis range
        mChart.getXAxis().setAxisMinimum(startYear);

        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
        mChart.getXAxis().setAxisMaximum(startYear + mChart.getBarData().getGroupWidth(groupSpace, barSpace) * 12);
        mChart.groupBars(startYear, groupSpace, barSpace);
        mChart.invalidate();

    }

    private void configChart(){
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

//        mTfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
        l.setTypeface(mTfLight);
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(mTfLight);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                //return String.valueOf((int) value);
                if(value==0){
                    return String.valueOf((int) value);
                }else
                    return (int)value+"月";
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        mChart.getAxisRight().setEnabled(false);

        mChart.setOnChartValueSelectedListener(this);
        mChart.getDescription().setEnabled(false);

        mChart.setPinchZoom(false);
        mChart.setDrawBarShadow(false);
        mChart.setDrawGridBackground(false);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Activity", "Selected: " + e.toString() + ", dataSet: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Activity", "Nothing selected.");
    }

    void showPopWindow(){
        View root = LayoutInflater.from(WorkerPerformanceDetailActivity.this).inflate(R.layout.choose_time_listview,null);
        ListView lv_timeList = (ListView)root.findViewById(R.id.choose_time_listview) ;
        lv_timeList.setAdapter(chooseTimeAdapter);
        pop = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        pop.setFocusable(true);
        // 不设置这句话，popWindow无法消失（点击旁边、返回键都没用）
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.showAsDropDown(select_time);
        // 在顶部显示
        pop.showAtLocation(findViewById(R.id.activity_worker_performance_detail), Gravity.TOP, 0, 0);

        lv_timeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                chooseTimeAdapter.setPosition(position);
                select_time.setText(Constants.chooseTimeList.get(position));
                switch (Constants.chooseTimeList.get(position)){
                    case "近一周业绩" :
                        choosen_time="one_week";
                        setText(recommend_num_one_week,success_num_one_week,success_money_one_week,score_one_week,exchange_score_one_week);
                        if(recommend_num_one_week==0){
                            rl_detail_recommend.setClickable(false);
                            right_arrow.setVisibility(View.GONE);
                        }else{
                            rl_detail_recommend.setClickable(true);
                            right_arrow.setVisibility(View.VISIBLE);
                        }
                        break;
                    case "近一月业绩" :
                        choosen_time="one_month";
                        setText(recommend_num_one_month,success_num_one_month,success_money_one_month,score_one_month,exchange_score_one_month);
                        if(recommend_num_one_month==0){
                            rl_detail_recommend.setClickable(false);
                            right_arrow.setVisibility(View.GONE);
                        }else{
                            rl_detail_recommend.setClickable(true);
                            right_arrow.setVisibility(View.VISIBLE);
                        }
                        break;
                    case "近一季度业绩" :
                        choosen_time="three_month";
                        setText(recommend_num_three_month,success_num_three_month,success_money_three_month,score_three_month,exchange_score_three_month);
                        if(recommend_num_three_month==0){
                            rl_detail_recommend.setClickable(false);
                            right_arrow.setVisibility(View.GONE);
                        }else{
                            rl_detail_recommend.setClickable(true);
                            right_arrow.setVisibility(View.VISIBLE);
                        }
                        break;
                    case "近一年业绩" :
                        choosen_time="one_year";
                        setText(recommend_num_one_year,success_num_one_year,success_money_one_year,score_one_year,exchange_score_one_year);
                        if(recommend_num_one_year==0){
                            rl_detail_recommend.setClickable(false);
                            right_arrow.setVisibility(View.GONE);
                        }else{
                            rl_detail_recommend.setClickable(true);
                            right_arrow.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }
        });

    }

}
