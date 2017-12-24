package com.xd.aselab.chinabankmanager.my;

import android.content.DialogInterface;
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
import com.xd.aselab.chinabankmanager.activity.MyPerformance;
import com.xd.aselab.chinabankmanager.fragment.HistogramView;
import com.xd.aselab.chinabankmanager.util.ChooseTimeAdapter;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.Constants;
import com.xd.aselab.chinabankmanager.util.MyMarkerView;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BaseMyAllPerformanceActivity extends AppCompatActivity  implements OnChartValueSelectedListener {

    private RelativeLayout back;
    private TextView select_time;
    private LinearLayout ll_card_chart,ll_fenqi_chart,ll_xiaodai_chart;
    private LinearLayout no_data_card_chart,no_data_fenqi_chart,no_data_xiaodai_chart;
    private TextView tv_card_saomiao,tv_card_success;
    private TextView tv_fenqi_1,tv_fenqi_2;
    private TextView tv_xiaodai_1,tv_xiaodai_2;
    private TextView gray_bar1,gray_bar2,gray_bar3;
    private RelativeLayout chart1;
    private BarChart chart2,chart3;
    protected Typeface mTfLight;
    private String[] select_string;
    private float[] numbers_fenqi = new float[12];
    private float[] money_fenqi = new float[12];
    private float[] numbers_xiaodai = new float[12];
    private float[] money_xiaodai = new float[12];
    private SharePreferenceUtil spu;
    private Handler handler;
    private SimpleDateFormat format;
    private Calendar calendar;
    private String type;
    private String choosen_time = "one_week";
    private int number1_fenqi,number2_fenqi,number3_fenqi,number4_fenqi;
    private int money1_fenqi,money2_fenqi,money3_fenqi,money4_fenqi;
    private int number1_xiaodai,number2_xiaodai,number3_xiaodai,number4_xiaodai;
    private int money1_xiaodai,money2_xiaodai,money3_xiaodai,money4_xiaodai;
    private String account="";
    private String type2="";
    private ChooseTimeAdapter chooseTimeAdapter;
    private PopupWindow pop;
    private int screenWidth;
    private RelativeLayout rl_choose_time;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_my_all_performance);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initViews();
        initDatas();
        getAllDatas();
        parseDatas();
        configChart();

    }

    void initViews(){
        back = (RelativeLayout)findViewById(R.id.act_my_all_perf_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title = (TextView)findViewById(R.id.title_score);
        select_time = (TextView)findViewById(R.id.act_my_all_perf_select_time);

        tv_card_saomiao = (TextView) findViewById(R.id.act_my_all_perf_saoma_num);
        tv_card_success = (TextView) findViewById(R.id.act_my_all_perf_success_sum);
        ll_card_chart = (LinearLayout)findViewById(R.id.act_my_all_perf_card_chart_ll);
        gray_bar1 = (TextView)findViewById(R.id.act_my_all_perf_bar_card);
        chart1 = (RelativeLayout) findViewById(R.id.rl_chart_card);
        no_data_card_chart = (LinearLayout)findViewById(R.id.act_my_all_perf_no_data_card);

        tv_fenqi_1 = (TextView)findViewById(R.id.act_my_all_perf_fenqi_success_num);
        tv_fenqi_2 = (TextView)findViewById(R.id.act_my_all_perf_fenqi_success_money);
        ll_fenqi_chart = (LinearLayout)findViewById(R.id.act_my_all_perf_fenqi_chart_ll);
        gray_bar2 = (TextView)findViewById(R.id.act_my_all_perf_bar_fenqi);
        chart2 = (BarChart) findViewById(R.id.bar_chart_fenqi);
        no_data_fenqi_chart= (LinearLayout)findViewById(R.id.act_my_all_perf_no_data_fenqi);

        tv_xiaodai_1 = (TextView)findViewById(R.id.act_my_all_perf_gerenxiaodai_success_num);
        tv_xiaodai_2 = (TextView)findViewById(R.id.act_my_all_perf_gerenxiaodai_success_money);
        ll_xiaodai_chart = (LinearLayout)findViewById(R.id.act_my_all_perf_gerenxiaodai_rl);
        gray_bar3 = (TextView)findViewById(R.id.act_my_all_perf_bar_gerenxiaodai);
        chart3 = (BarChart)findViewById(R.id.bar_chart_gerenxiaodai);
        no_data_xiaodai_chart = (LinearLayout)findViewById(R.id.act_my_all_perf_no_data_xiaodai);
    }

    void initDatas(){
        spu= new SharePreferenceUtil(BaseMyAllPerformanceActivity.this,"user");
        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");
        if("0".equals(type)){
            type2 = "basic";
        }else if("1".equals(type)){
            type2 = "manager";
        }
        if (getIntent().getStringExtra("name")!=null){
            title.setText(getIntent().getStringExtra("name")+"的所有业绩");
        }
        select_string = new String[]{"近一周业绩", "近一月业绩", "近三月业绩", "近一年业绩"};
        format = new SimpleDateFormat("yyyy-MM-dd");
        calendar = Calendar.getInstance();
        gray_bar1.setText(Calendar.getInstance().get(Calendar.YEAR)+"年各月办卡成功情况分析");
        gray_bar2.setText(Calendar.getInstance().get(Calendar.YEAR)+"年各月分期业绩情况分析");
        gray_bar3.setText(Calendar.getInstance().get(Calendar.YEAR)+"年各月个人消贷业绩情况分析");
        chooseTimeAdapter = new ChooseTimeAdapter(Constants.chooseTimeList,BaseMyAllPerformanceActivity.this);
    }

    void getAllDatas(){

        select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow();
                //startActivityForResult(new Intent().setClass(PerformanceRanking.this, PerfRankTransparent.class),102);
                /*AlertDialog.Builder builder = new AlertDialog.Builder(BaseMyAllPerformanceActivity.this);
                builder.setTitle("选择时间范围");
                builder.setItems(select_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        select_time.setText(select_string[which]);
                        final PostParameter[] params = new PostParameter[6];
                        params[0] = new PostParameter("account", account);
                        params[1] = new PostParameter("branchLevel4", spu.getBranchLevel4());
                        params[2] = new PostParameter("type", ""+type);
                        calendar.setTime(new Date());
                        switch (select_string[which]){
                            case "近一周业绩" :
                                choosen_time="one_week";
                                setFenqiData(number1_fenqi,money1_fenqi);
                                *//*line1.setText("分期业务成功数量："+("".equals(number1)? "暂无" : number1+"笔"));
                                line2.setText("分期业务成功金额："+("".equals(money1)? "暂无" : money1+"元"));*//*
                                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
                                params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                                params[4] = new PostParameter("end", format.format(new Date()));
                                break;
                            case "近一月业绩" :
                                choosen_time="one_month";
                                setFenqiData(number2_fenqi,money2_fenqi);
                                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                                params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                                params[4] = new PostParameter("end", format.format(new Date()));
                                break;
                            case "近三月业绩" :
                                choosen_time="three_month";
                                setFenqiData(number3_fenqi,money3_fenqi);
                                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 3);
                                params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                                params[4] = new PostParameter("end", format.format(new Date()));
                                break;
                            case "近一年业绩" :
                                choosen_time="one_year";
                                setFenqiData(number4_fenqi,money4_fenqi);
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
                                Log.e("reCode",""+reCode);
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
                params[0] = new PostParameter("account", account);
                params[1] = new PostParameter("branchLevel4", spu.getBranchLevel4());
                params[2] = new PostParameter("type", ""+type);
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
                final PostParameter[] params = new PostParameter[2];
                params[0] = new PostParameter("account", account);
                params[1] = new PostParameter("type", type2);
                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetInstallmentMyPerformance, params, ConnectUtil.POST);
                Log.e("reCode",""+reCode);
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
                final PostParameter[] params = new PostParameter[2];
                params[0] = new PostParameter("account", account);
                params[1] = new PostParameter("type", type2);
                //个人消贷的接口
                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetInstallmentMyPerformance, params, ConnectUtil.POST);
                Log.e("reCode",""+reCode);
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
                    case 0:
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                Log.e("MyPerformance：reCode", reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(BaseMyAllPerformanceActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONObject this_year = json.getJSONObject("this_year");
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
                                    tv_card_saomiao.setText("共有"+json.getString("sumCount")+"人扫码");
                                    tv_card_success.setText("共有"+json.getString("success_sum")+"人成功办卡");
                                   /* if ("BASIC".equals(spu.getType())){
                                        line2.setText("发展店铺排名："+("".equals(json.getString("shop_rank"))? "暂无" : "第"+json.getString("shop_rank")+"名"));
                                        line3.setText("成功办卡业绩排名："+("".equals(json.getString("cards_rank"))? "暂无" : "第"+json.getString("cards_rank")+"名"));
                                    }*/

                                    if (! flag){
                                        ll_card_chart.setVisibility(View.GONE);
                                        no_data_card_chart.setVisibility(View.VISIBLE);
                                       // Toast.makeText(BaseMyAllPerformanceActivity.this, "还没有基层经理业绩信息", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        ll_card_chart.setVisibility(View.VISIBLE);
                                        no_data_card_chart.setVisibility(View.GONE);
                                        HistogramView histogramView = new HistogramView(BaseMyAllPerformanceActivity.this, names, numbers);
                                        chart1.addView(histogramView);
                                    }
                                } else {
                                    Log.e("MyPerformance_Activity", BaseMyAllPerformanceActivity.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(BaseMyAllPerformanceActivity.this, BaseMyAllPerformanceActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("MyPerformance_Activity", "reCode为空");
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
                                if ("false".equals(status)) {
                                    Toast.makeText(BaseMyAllPerformanceActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONObject oneWeekJO = json.getJSONObject("one_week");
                                    JSONObject oneMonthJO = json.getJSONObject("one_month");
                                    JSONObject threeMonthJO = json.getJSONObject("three_month");
                                    JSONObject oneYearJO = json.getJSONObject("one_year");
                                    JSONObject thisYearJO = json.getJSONObject("this_year");
                                    //Log.e("this_year", this_year.toString());
                                    // float[] numbers = new float[12];
                                    number1_fenqi = oneWeekJO.getInt("number");
                                    money1_fenqi = oneWeekJO.getInt("money");
                                    number2_fenqi = oneMonthJO.getInt("number");
                                    money2_fenqi = oneMonthJO.getInt("money");
                                    number3_fenqi = threeMonthJO.getInt("number");
                                    money3_fenqi = threeMonthJO.getInt("money");
                                    number4_fenqi = oneYearJO.getInt("number");
                                    money4_fenqi = oneYearJO.getInt("money");

                                    tv_fenqi_1.setText("分期业务成功数量："+("".equals(number1_fenqi)? "暂无" : number1_fenqi+"笔"));
                                    tv_fenqi_2.setText("分期业务成功金额："+("".equals(money1_fenqi)? "暂无" : money1_fenqi+"元"));

                                    boolean flag = false;
                                    for (int i=0; i<12; i++){
                                        // numbers[i] = thisYearJO.getInt(""+(i+1));
                                        numbers_fenqi[i]=thisYearJO.getJSONObject(""+(i+1)).getInt("number");
                                        money_fenqi[i] = thisYearJO.getJSONObject(""+(i+1)).getInt("money");
                                        if (numbers_fenqi[i]>0){
                                            flag = true;
                                        }
                                    }
                                    if (! flag){
                                        ll_fenqi_chart.setVisibility(View.GONE);
                                        //chart.setVisibility(View.GONE);
                                        no_data_fenqi_chart.setVisibility(View.VISIBLE);
                                       // Toast.makeText(BaseMyAllPerformanceActivity.this, "还没有业绩信息~", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        initChartDatas();
                                        no_data_fenqi_chart.setVisibility(View.GONE);
                                        ll_fenqi_chart.setVisibility(View.VISIBLE);

                                    }
                                } else {
                                    Log.e("BaseAllPerfActivity", BaseMyAllPerformanceActivity.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(BaseMyAllPerformanceActivity.this, BaseMyAllPerformanceActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("KMyPreformenceActivity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        break;
                }
            }
        };
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
            yVals1.add(new BarEntry(i, money_fenqi[i-1]));
            yVals2.add(new BarEntry(i, numbers_fenqi[i-1]));
//            yVals1.add(new BarEntry(i, 10));
//            yVals2.add(new BarEntry(i, 2));
//            yVals3.add(new BarEntry(i, (float) (Math.random() * 1)));
//            yVals4.add(new BarEntry(i, (float) (Math.random() * 1)));
        }

        BarDataSet set1, set2;
        if (chart2.getData() != null && chart2.getData().getDataSetCount() > 0) {

            set1 = (BarDataSet) chart2.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) chart2.getData().getDataSetByIndex(1);

            set1.setValues(yVals1);
            set2.setValues(yVals2);

            chart2.getData().notifyDataChanged();
            chart2.notifyDataSetChanged();

        } else {
            // create 2 DataSets
            set1 = new BarDataSet(yVals1, "分期总额(元)");
            set1.setColor(Color.rgb(255, 133, 133));
            set2 = new BarDataSet(yVals2, "业务量(笔)");
            set2.setColor(Color.rgb(98, 188, 255));

            BarData data = new BarData(set1, set2);
            data.setValueFormatter(new LargeValueFormatter());
            data.setValueTypeface(mTfLight);

            chart2.setData(data);
        }

        // specify the width each bar should have
        chart2.getBarData().setBarWidth(barWidth);

        // restrict the x-axis range
        chart2.getXAxis().setAxisMinimum(startYear);

        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
        chart2.getXAxis().setAxisMaximum(startYear + chart2.getBarData().getGroupWidth(groupSpace, barSpace) * 12);
        chart2.groupBars(startYear, groupSpace, barSpace);
        chart2.invalidate();

    }

    private void configChart(){
       // MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
       // mv.setChartView(chart2); // For bounds control
       // chart2.setMarker(mv); // Set the marker to the chart

//        mTfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");

        Legend l = chart2.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
        l.setTypeface(mTfLight);
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        XAxis xAxis = chart2.getXAxis();
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

        YAxis leftAxis = chart2.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        chart2.getAxisRight().setEnabled(false);

       // chart2.setOnChartValueSelectedListener(this);
        chart2.getDescription().setEnabled(false);

        chart2.setPinchZoom(false);
        chart2.setDrawBarShadow(false);
        chart2.setDrawGridBackground(false);
    }

    void setFenqiData(int number,int money){
        tv_fenqi_1.setText("分期业务成功数量："+("".equals(number)? "暂无" : number+"笔"));
        tv_fenqi_2.setText("分期业务成功金额："+("".equals(money)? "暂无" : money+"元"));
    }
    void setGerenxiaodaiData(int number,int money){
        tv_xiaodai_1.setText("分期业务成功数量："+("".equals(number)? "暂无" : number+"笔"));
        tv_xiaodai_2.setText("分期业务成功金额："+("".equals(money)? "暂无" : money+"元"));
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
        View root = LayoutInflater.from(BaseMyAllPerformanceActivity.this).inflate(R.layout.choose_time_listview,null);
        ListView lv_timeList = (ListView)root.findViewById(R.id.choose_time_listview) ;
        lv_timeList.setAdapter(chooseTimeAdapter);
        pop = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        pop.setFocusable(true);
        // 不设置这句话，popWindow无法消失（点击旁边、返回键都没用）
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.showAsDropDown(select_time);
        // 在顶部显示
        pop.showAtLocation(findViewById(R.id.activity_base_my_all_performance), Gravity.TOP, 0, 0);

        lv_timeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                chooseTimeAdapter.setPosition(position);
                select_time.setText(Constants.chooseTimeList.get(position));
                final PostParameter[] params = new PostParameter[6];
                params[0] = new PostParameter("account", account);
                params[1] = new PostParameter("branchLevel4", spu.getBranchLevel4());
                params[2] = new PostParameter("type", ""+type);
                calendar.setTime(new Date());
                switch (Constants.chooseTimeList.get(position)){
                    case "近一周业绩" :
                        choosen_time="one_week";
                        setFenqiData(number1_fenqi,money1_fenqi);
                                /*line1.setText("分期业务成功数量："+("".equals(number1)? "暂无" : number1+"笔"));
                                line2.setText("分期业务成功金额："+("".equals(money1)? "暂无" : money1+"元"));*/
                        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
                        params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                        params[4] = new PostParameter("end", format.format(new Date()));
                        break;
                    case "近一月业绩" :
                        choosen_time="one_month";
                        setFenqiData(number2_fenqi,money2_fenqi);
                        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                        params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                        params[4] = new PostParameter("end", format.format(new Date()));
                        break;
                    case "近三月业绩" :
                        choosen_time="three_month";
                        setFenqiData(number3_fenqi,money3_fenqi);
                        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 3);
                        params[3] = new PostParameter("begin", format.format(calendar.getTime()));
                        params[4] = new PostParameter("end", format.format(new Date()));
                        break;
                    case "近一年业绩" :
                        choosen_time="one_year";
                        setFenqiData(number4_fenqi,money4_fenqi);
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
                        Log.e("reCode",""+reCode);
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
