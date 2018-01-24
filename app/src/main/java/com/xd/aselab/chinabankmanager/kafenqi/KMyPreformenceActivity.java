package com.xd.aselab.chinabankmanager.kafenqi;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import com.xd.aselab.chinabankmanager.util.ChooseTimeAdapter;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.Constants;
import com.xd.aselab.chinabankmanager.util.DensityUtil;
import com.xd.aselab.chinabankmanager.util.MyMarkerView;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class KMyPreformenceActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private RelativeLayout back;
    private TextView select_time;
    private TextView line1,line2,line3,line4;
    private LinearLayout ll_chart;
    private RelativeLayout gray_bar_top;
    private TextView gray_bar;
    private ImageView no_data_img;
    private TextView no_data_txt;
    private BarChart mChart;
    private Typeface mTfRegular;
    protected Typeface mTfLight;
    private SharePreferenceUtil spu;
    private Handler handler;
    private String[] select_string;
    private float[] numbers = new float[12];
    private float[] money = new float[12];
    private String choosen_time = "one_week";
    private int number1,number2,number3,number4,recommend_number1,recommend_number2,recommend_number3,recommend_number4;
    private int money1,money2,money3,money4,recommend_money1,recommend_money2,recommend_money3,recommend_money4;
    private String type;
    private ChooseTimeAdapter chooseTimeAdapter;
    private PopupWindow pop;
    private int screenWidth;
    private RelativeLayout rl_choose_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kmy_preformence);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;

        spu = new SharePreferenceUtil(KMyPreformenceActivity.this, "user");
        select_string = new String[]{"近一周业绩", "近一月业绩", "近一季度业绩", "近一年业绩"};
        chooseTimeAdapter = new ChooseTimeAdapter(Constants.chooseTimeList,KMyPreformenceActivity.this);
        //chooseTimeAdapter.setList(Constants.chooseTimeList);
        type = getIntent().getStringExtra("type");
        Log.e("www","type------"+type);

        initViews();
        getAllDatas();
        configChart();

    }

    void initViews(){
        back = (RelativeLayout)findViewById(R.id.act_fenqi_my_perf_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rl_choose_time = (RelativeLayout) findViewById(R.id.rl_my_perf_select_time);
        ll_chart = (LinearLayout)findViewById(R.id.ll_chart);
        select_time = (TextView) findViewById(R.id.act_my_perf_select_time);
        line1 = (TextView)findViewById(R.id.act_my_perf_red_recommend_num);
        line2 = (TextView)findViewById(R.id.act_my_perf_red_recommend_money);
        line3 = (TextView)findViewById(R.id.act_my_perf_red_success_num);
        line4 = (TextView)findViewById(R.id.act_my_perf_red_success_money);
        gray_bar_top = (RelativeLayout)findViewById(R.id.act_kafenqi_my_perf_gray_bar_top);
        gray_bar = (TextView)findViewById(R.id.act_kafenqi_my_perf_gray_bar);
        gray_bar.setText(Calendar.getInstance().get(Calendar.YEAR)+"年各月分期业务情况分析");
        no_data_img = (ImageView)findViewById(R.id.act_kafenqi_my_perf_no_data_img);
        no_data_txt = (TextView)findViewById(R.id.act_kafenqi_my_perf_no_data_txt);

        mChart = (BarChart) findViewById(R.id.chart1);
        if("manager".equals(type)){
            line1.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
        }else{
            line1.setVisibility(View.VISIBLE);
            line2.setVisibility(View.VISIBLE);
        }

        /*timeSortLayout = getLayoutInflater().inflate(R.layout.choose_time_listview,null);
        ll_time_top = (LinearLayout)timeSortLayout.findViewById(R.id.ll_time_pop);
        timeListView = (ListView)timeSortLayout.findViewById(R.id.choose_time_listview);
        timeListView.setAdapter(chooseTimeAdapter);*/
    }

    private void getAllDatas(){

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
                                    Toast.makeText(KMyPreformenceActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONObject oneWeekJO = json.getJSONObject("one_week");
                                    JSONObject oneMonthJO = json.getJSONObject("one_month");
                                    JSONObject threeMonthJO = json.getJSONObject("three_month");
                                    JSONObject oneYearJO = json.getJSONObject("one_year");
                                    JSONObject thisYearJO = json.getJSONObject("this_year");
                                    //Log.e("this_year", this_year.toString());
                                    // float[] numbers = new float[12];
                                    recommend_number1 = oneWeekJO.getInt("recommend_num");
                                    recommend_money1 = oneWeekJO.getInt("recommend_money");
                                    number1 = oneWeekJO.getInt("number");
                                    money1 = oneWeekJO.getInt("money");
                                    recommend_number2 = oneMonthJO.getInt("recommend_num");
                                    recommend_money2 = oneMonthJO.getInt("recommend_money");
                                    number2 = oneMonthJO.getInt("number");
                                    money2 = oneMonthJO.getInt("money");
                                    recommend_number3 = threeMonthJO.getInt("recommend_num");
                                    recommend_money3 = threeMonthJO.getInt("recommend_money");
                                    number3 = threeMonthJO.getInt("number");
                                    money3 = threeMonthJO.getInt("money");
                                    recommend_number4 = oneYearJO.getInt("recommend_num");
                                    recommend_money4 = oneYearJO.getInt("recommend_money");
                                    number4 = oneYearJO.getInt("number");
                                    money4 = oneYearJO.getInt("money");

                                    line1.setText("分期业务推荐数量："+("".equals(recommend_number1)? "暂无" : recommend_number1+"笔"));
                                    line2.setText("分期业务推荐金额："+("".equals(recommend_money1)? "暂无" : recommend_money1+"万元"));
                                    line3.setText("分期业务成功数量："+("".equals(number1)? "暂无" : number1+"笔"));
                                    line4.setText("分期业务成功金额："+("".equals(money1)? "暂无" : money1+"万元"));

                                    boolean flag = false;
                                    for (int i=0; i<12; i++){
                                        // numbers[i] = thisYearJO.getInt(""+(i+1));
                                        numbers[i]=thisYearJO.getJSONObject(""+(i+1)).getInt("number");
                                        money[i] = thisYearJO.getJSONObject(""+(i+1)).getInt("money");
                                        if (numbers[i]>0){
                                            flag = true;
                                        }
                                    }


                                    if (! flag){
                                        gray_bar_top.setVisibility(View.GONE);
                                        gray_bar.setVisibility(View.GONE);
                                        ll_chart.setVisibility(View.GONE);
                                        no_data_img.setVisibility(View.VISIBLE);
                                        no_data_txt.setVisibility(View.VISIBLE);
                                        Toast.makeText(KMyPreformenceActivity.this, "还没有业绩信息~", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        initChartDatas();
                                        ll_chart.setVisibility(View.VISIBLE);
                                        no_data_img.setVisibility(View.GONE);
                                        no_data_txt.setVisibility(View.GONE);
                                        gray_bar_top.setVisibility(View.VISIBLE);
                                        gray_bar.setVisibility(View.VISIBLE);

                                    }
                                } else {
                                    Log.e("KMyPreformenceActivity", KMyPreformenceActivity.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(KMyPreformenceActivity.this, KMyPreformenceActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("KMyPreformenceActivity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("KMyPreformenceActivity", KMyPreformenceActivity.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };
        rl_choose_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow();
            }
        });

        new Thread(){
            @Override
            public void run() {
                super.run();
                final PostParameter[] params = new PostParameter[2];
                params[0] = new PostParameter("account", spu.getAccount());
                params[1] = new PostParameter("type", type);
                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetInstallmentMyPerformance, params, ConnectUtil.POST);
                Log.e("reCode",""+reCode);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();

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
            yVals1.add(new BarEntry(i, money[i-1]));
            yVals2.add(new BarEntry(i, numbers[i-1]));
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
            set1 = new BarDataSet(yVals1, "分期总额(万元)");
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
        View root = LayoutInflater.from(KMyPreformenceActivity.this).inflate(R.layout.choose_time_listview,null);
        ListView lv_timeList = (ListView)root.findViewById(R.id.choose_time_listview) ;
        lv_timeList.setAdapter(chooseTimeAdapter);
        pop = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        pop.setFocusable(true);
        // 不设置这句话，popWindow无法消失（点击旁边、返回键都没用）
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.showAsDropDown(select_time);
        // 在顶部显示
        pop.showAtLocation(findViewById(R.id.activity_kmy_preformence), Gravity.TOP, 0, 0);

        lv_timeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                chooseTimeAdapter.setPosition(position);
                select_time.setText(Constants.chooseTimeList.get(position));
                switch (Constants.chooseTimeList.get(position)){
                    case "近一周业绩" :
                        choosen_time="one_week";
                        setText(recommend_number1,recommend_money1,number1,money1);
                        /*line1.setText("分期业务成功数量："+("".equals(number1)? "暂无" : number1+"笔"));
                        line2.setText("分期业务成功金额："+("".equals(money1)? "暂无" : money1+"元"));*/
                        break;
                    case "近一月业绩" :
                        /*line1.setText("分期业务成功数量："+("".equals(number2)? "暂无" : number2+"笔"));
                        line2.setText("分期业务成功金额："+("".equals(money2)? "暂无" : money2+"元"));*/
                        setText(recommend_number2,recommend_money2,number2,money2);
                        choosen_time="one_month";
                        break;
                    case "近一季度业绩" :
                        /*line1.setText("分期业务成功数量："+("".equals(number3)? "暂无" : number3+"笔"));
                        line2.setText("分期业务成功金额："+("".equals(money3)? "暂无" : money3+"元"));*/
                        setText(recommend_number3,recommend_money3,number3,money3);
                        choosen_time="three_month";
                        break;
                    case "近一年业绩" :
                        /*line1.setText("分期业务成功数量："+("".equals(number4)? "暂无" : number4+"笔"));
                        line2.setText("分期业务成功金额："+("".equals(money4)? "暂无" : money4+"元"));*/
                        setText(recommend_number4,recommend_money4,number4,money4);
                        choosen_time="one_year";
                        break;
                }
            }
        });

    }

    void setText(int recommend_number,int recommend_money,int success_number,int success_money){
        line1.setText("分期业务推荐数量："+("".equals(recommend_number)? "暂无" : recommend_number+"笔"));
        line2.setText("分期业务推荐金额："+("".equals(recommend_money)? "暂无" : recommend_money+"万元"));
        line3.setText("分期业务成功数量："+("".equals(success_number)? "暂无" : success_number+"笔"));
        line4.setText("分期业务成功金额："+("".equals(success_money)? "暂无" : success_money+"万元"));
    }
}
