package com.xd.aselab.chinabankmanager.activity.Manager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.xd.aselab.chinabankmanager.kafenqi.KafenqiCustomerPerformance;
import com.xd.aselab.chinabankmanager.kafenqi.KafenqiCustomerPerformanceDetial;
import com.xd.aselab.chinabankmanager.util.ChooseTimeAdapter;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.Constants;
import com.xd.aselab.chinabankmanager.util.MyMarkerView;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ManagerKafenqiNonCarMyPerformance extends AppCompatActivity implements OnChartValueSelectedListener {

    private RelativeLayout back;
    private TextView non_car_success_num;
    private TextView non_car_success_money;
    private TextView select_time;
    private TextView gray_bar;
    private LinearLayout ll_chart;
    private LinearLayout ll_no_data;
//    private LinearLayout ll_click;
    private RelativeLayout rl_select_time;
    private RelativeLayout gray_bar_top;
//    private ImageView click;
    private BarChart mChart;

    private String choosen_time;

    private float[] numbers = new float[12];
    private float[] money = new float[12];

    private int success_num_one_week, success_num_one_month, success_num_three_month, success_num_one_year;
    private double success_money_one_week, success_money_one_month, success_money_three_month, success_money_one_year;

    private ChooseTimeAdapter chooseTimeAdapter;
    private PopupWindow pop;

    private Typeface mTfLight;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_kafenqi_non_car_my_performance);

        back = (RelativeLayout) findViewById(R.id.act_manager_kafenqi_non_car_my_perf_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        non_car_success_num = (TextView) findViewById(R.id.act_manager_kafenqi_non_car_my_perf_success_num_text);
        non_car_success_money = (TextView) findViewById(R.id.act_manager_kafenqi_non_car_my_perf_success_money_text);
        select_time = (TextView) findViewById(R.id.act_manager_kafenqi_non_car_my_perf_select_time);
        gray_bar = (TextView) findViewById(R.id.act_manager_kafenqi_non_car_my_perf_gray_bar);
        gray_bar_top = (RelativeLayout) findViewById(R.id.act_manager_kafenqi_non_car_my_perf_gray_bar_top);
        rl_select_time = (RelativeLayout) findViewById(R.id.rl_manager_kafenqi_non_car_my_perf_select_time);
        ll_chart = (LinearLayout) findViewById(R.id.ll_chart);
        ll_no_data = (LinearLayout) findViewById(R.id.ll_no_data);
        mChart = (BarChart) findViewById(R.id.chart1);

    }

    void getData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                SharePreferenceUtil spu = new SharePreferenceUtil(ManagerKafenqiNonCarMyPerformance.this, "user");
                PostParameter[] parameter = new PostParameter[1];
                parameter[0] = new PostParameter("account", spu.getAccount());
                String recode = ConnectUtil.httpRequest(ConnectUtil.GetNotCarBossMyPerformance, parameter, ConnectUtil.POST);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = recode;
                handler.sendMessage(msg);
            }
        }.start();
    }

    void parseData() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        try {
                            String recode = (String) msg.obj;
                            if(recode != null) {
                                JSONObject object = new JSONObject(recode);
                                String status = object.getString("status");
                                if("false".equals(status)) {
                                    Toast.makeText(ManagerKafenqiNonCarMyPerformance.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if("true".equals(status)) {
                                    JSONObject oneWeekJO = object.getJSONObject("one_week");
                                    JSONObject oneMonthJO = object.getJSONObject("one_month");
                                    JSONObject threeMonthJO = object.getJSONObject("three_month");
                                    JSONObject oneYearJO = object.getJSONObject("one_year");
                                    JSONObject thisYearJO = object.getJSONObject("this_year");

                                    success_num_one_week = oneWeekJO.getInt("number");
                                    success_money_one_week = oneWeekJO.getDouble("money");

                                    success_num_one_month = oneMonthJO.getInt("number");
                                    success_money_one_month = oneMonthJO.getDouble("money");

                                    success_num_three_month = threeMonthJO.getInt("number");
                                    success_money_three_month = threeMonthJO.getDouble("money");

                                    success_num_one_year = oneYearJO.getInt("number");
                                    success_money_one_year = oneYearJO.getDouble("money");

                                    choosen_time = "one_week";
                                    chooseTimeAdapter.setPosition(0);
                                    setText(success_num_one_week,success_money_one_week);

                                    boolean flag = false;
                                    for(int i = 0; i < 12; i++) {
                                        numbers[i] = thisYearJO.getJSONObject("" + (i + 1)).getInt("number");
                                        money[i] = thisYearJO.getJSONObject("" + (i + 1)).getInt("money");
                                        if(numbers[i] > 0) {
                                            flag = true;
                                        }
                                    }

                                    if(! flag) {
                                        ll_chart.setVisibility(View.GONE);
                                        ll_no_data.setVisibility(View.VISIBLE);
                                    } else {
                                        initChartDatas();
                                        ll_chart.setVisibility(View.VISIBLE);
                                        ll_no_data.setVisibility(View.GONE);
                                    }
                                } else {
                                    Log.e("ManaKafenqiNonCarMyPerf", ManagerKafenqiNonCarMyPerformance.this.getResources().getString(R.string.status_exception));
                                }
                            } else {
                                Toast.makeText(ManagerKafenqiNonCarMyPerformance.this, ManagerKafenqiNonCarMyPerformance.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("ManaKafenqiNonCarMyPerf", "reCode为空");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    default:
                        Log.e("ManaKafenqiNonCarMyPerf", ManagerKafenqiNonCarMyPerformance.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };
    }

    void initDatas() {
        chooseTimeAdapter = new ChooseTimeAdapter(Constants.chooseTimeList,ManagerKafenqiNonCarMyPerformance.this);
    }

    void initEvents() {
        rl_select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow();
            }
        });
    }

    void showPopWindow() {
        View root = LayoutInflater.from(ManagerKafenqiNonCarMyPerformance.this).inflate(R.layout.choose_time_listview,null);
        ListView lv_timeList = (ListView)root.findViewById(R.id.choose_time_listview) ;
        lv_timeList.setAdapter(chooseTimeAdapter);
        pop = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        pop.setFocusable(true);
        // 不设置这句话，popWindow无法消失（点击旁边、返回键都没用）
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.showAsDropDown(select_time);
        // 在顶部显示
        pop.showAtLocation(findViewById(R.id.activity_manager_kafenqi_non_car_my_performance), Gravity.TOP, 0, 0);

        lv_timeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                chooseTimeAdapter.setPosition(position);
                select_time.setText(Constants.chooseTimeList.get(position));
                switch (Constants.chooseTimeList.get(position)){
                    case "近一周业绩" :
                        choosen_time="one_week";
                        setText(success_num_one_week,success_money_one_week);
                        break;
                    case "近一月业绩" :
                        choosen_time="one_month";
                        setText(success_num_one_month,success_money_one_month);
                        break;
                    case "近一季度业绩" :
                        choosen_time="three_month";
                        setText(success_num_three_month,success_money_three_month);
                        break;
                    case "近一年业绩" :
                        choosen_time="one_year";
                        setText(success_num_one_year,success_money_one_year);
                        break;
                }
            }
        });
    }

    private void setText(int success_num,double success_money) {
        DecimalFormat df = new DecimalFormat("#0.0000");
        non_car_success_num.setText("".equals(success_num)? "暂无" : success_num+"笔");
        non_car_success_money.setText(("".equals(success_money)? "暂无" : df.format(success_money)) + "万元");
    }

    private void initChartDatas() {

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
                    yVals1.add(new BarEntry(i, (float)0.0000));
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

}
