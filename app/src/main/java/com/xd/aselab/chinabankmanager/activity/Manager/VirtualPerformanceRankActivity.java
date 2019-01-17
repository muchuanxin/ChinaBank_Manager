package com.xd.aselab.chinabankmanager.activity.Manager;

import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
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
import com.xd.aselab.chinabankmanager.activity.Manager.model.SimpleRankAbilityVO;
import com.xd.aselab.chinabankmanager.util.ChooseSortAdapter;
import com.xd.aselab.chinabankmanager.util.ChooseTimeAdapter;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.Constants;
import com.xd.aselab.chinabankmanager.util.ListUtils;
import com.xd.aselab.chinabankmanager.util.PostParameter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VirtualPerformanceRankActivity extends AppCompatActivity {

    private ListView listView;
    private RelativeLayout back;
    private LinearLayout choose_sort;
    private LinearLayout choose_time;
    private TextView sort;
    private TextView select_time;
    private RelativeLayout no_data;

    private String choosen_time = "one_week";
    private String choosen_sort = "number";
    private String number = "number";
    private String money = "money";

    private ChooseSortAdapter chooseSortAdapter;
    private ChooseTimeAdapter chooseTimeAdapter;

    private List<SimpleRankAbilityVO> numberListOfWeek = new ArrayList<>();
    private List<SimpleRankAbilityVO> numberListOfMonth = new ArrayList<>();
    private List<SimpleRankAbilityVO> numberListOfQuarter = new ArrayList<>();
    private List<SimpleRankAbilityVO> numberListOfYear = new ArrayList<>();
    private List<SimpleRankAbilityVO> moneyListOfWeek = new ArrayList<>();
    private List<SimpleRankAbilityVO> moneyListOfMonth = new ArrayList<>();
    private List<SimpleRankAbilityVO> moneyListOfQuarter = new ArrayList<>();
    private List<SimpleRankAbilityVO> moneyListOfYear = new ArrayList<>();


    private GrabRankAdapter adapter;

    private PopupWindow pop;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    try {
                        String recode = (String) msg.obj;
                        if (recode != null) {
                            Log.e("VirPerformRank：recode", recode);
                            JSONObject jsonObject = new JSONObject(recode);
                            String status = jsonObject.getString("status");

                            if ("false".equals(status)) {
                                Toast.makeText(VirtualPerformanceRankActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else if ("true".equals(status)) {
                                JSONArray oneWeekJA = jsonObject.getJSONArray("one_week");
                                JSONArray oneMonthJA = jsonObject.getJSONArray("one_month");
                                JSONArray oneQuarterJA = jsonObject.getJSONArray("three_month");
                                JSONArray oneYearJA = jsonObject.getJSONArray("one_year");
                                if (oneWeekJA.length() > 0) {
                                    for (int i = 0; i < oneWeekJA.length(); i++) {
                                        JSONObject temp = (JSONObject) oneWeekJA.get(i);
                                        SimpleRankAbilityVO sraForNumber = new SimpleRankAbilityVO();
                                        sraForNumber.setName(temp.getString("name"));
                                        sraForNumber.setValue(Double.parseDouble(temp.getString("number")));
                                        numberListOfWeek.add(sraForNumber);
                                        SimpleRankAbilityVO sraForMoney = new SimpleRankAbilityVO();
                                        sraForMoney.setName(temp.getString("name"));
                                        sraForMoney.setValue(Double.parseDouble(temp.getString("money")));
                                        moneyListOfWeek.add(sraForMoney);
                                    }
                                }

                                if (oneMonthJA.length() > 0) {
                                    for (int i = 0; i < oneMonthJA.length(); i++) {
                                        JSONObject temp = (JSONObject) oneMonthJA.get(i);
                                        SimpleRankAbilityVO sraForNumber = new SimpleRankAbilityVO();
                                        sraForNumber.setName(temp.getString("name"));
                                        sraForNumber.setValue(Double.parseDouble(temp.getString("number")));
                                        numberListOfMonth.add(sraForNumber);
                                        SimpleRankAbilityVO sraForMoney = new SimpleRankAbilityVO();
                                        sraForMoney.setName(temp.getString("name"));
                                        sraForMoney.setValue(Double.parseDouble(temp.getString("money")));
                                        moneyListOfMonth.add(sraForMoney);
                                    }
                                }

                                if (oneQuarterJA.length() > 0) {
                                    for (int i = 0; i < oneQuarterJA.length(); i++) {
                                        JSONObject temp = (JSONObject) oneQuarterJA.get(i);
                                        SimpleRankAbilityVO sraForNumber = new SimpleRankAbilityVO();
                                        sraForNumber.setName(temp.getString("name"));
                                        sraForNumber.setValue(Double.parseDouble(temp.getString("number")));
                                        numberListOfQuarter.add(sraForNumber);
                                        SimpleRankAbilityVO sraForMoney = new SimpleRankAbilityVO();
                                        sraForMoney.setName(temp.getString("name"));
                                        sraForMoney.setValue(Double.parseDouble(temp.getString("money")));
                                        moneyListOfQuarter.add(sraForMoney);
                                    }
                                }

                                if (oneYearJA.length() > 0) {
                                    for (int i = 0; i < oneYearJA.length(); i++) {
                                        JSONObject temp = (JSONObject) oneYearJA.get(i);
                                        SimpleRankAbilityVO sraForNumber = new SimpleRankAbilityVO();
                                        sraForNumber.setName(temp.getString("name"));
                                        sraForNumber.setValue(Double.parseDouble(temp.getString("number")));
                                        numberListOfYear.add(sraForNumber);
                                        SimpleRankAbilityVO sraForMoney = new SimpleRankAbilityVO();
                                        sraForMoney.setName(temp.getString("name"));
                                        sraForMoney.setValue(Double.parseDouble(temp.getString("money")));
                                        moneyListOfYear.add(sraForMoney);
                                    }
                                }

                                //默认显示一周业绩
                                adapter = new GrabRankAdapter(VirtualPerformanceRankActivity.this, numberListOfWeek);
                                listView.setAdapter(adapter);
                                if (numberListOfWeek.size() > 0) {
                                    listView.setVisibility(View.VISIBLE);
                                    no_data.setVisibility(View.GONE);
                                } else {
                                    listView.setVisibility(View.GONE);
                                    no_data.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Log.e("VirtualP", VirtualPerformanceRankActivity.this.getResources().getString(R.string.status_exception));
                            }
                        } else {
                            Toast.makeText(VirtualPerformanceRankActivity.this, VirtualPerformanceRankActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    Log.e("VirtualPnkActivity", VirtualPerformanceRankActivity.this.getResources().getString(R.string.handler_what_exception));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_performance_rank);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initViews();
        initData();
        initEvents();

    }

    void initViews() {
        back = (RelativeLayout) findViewById(R.id.vpr_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sort = (TextView) findViewById(R.id.vpr_perf_sort);
        select_time = (TextView) findViewById(R.id.vpr_tv_timeSelect);

        listView = (ListView) findViewById(R.id.vpr_performance);

        choose_sort = (LinearLayout) findViewById(R.id.vpr_performRanking);
        choose_time = (LinearLayout) findViewById(R.id.vpr_timeSelect);
        no_data = (RelativeLayout) findViewById(R.id.vpr_no_data);
    }

    void initData() {
        chooseSortAdapter = new ChooseSortAdapter(Constants.shengHangChooseSortList, VirtualPerformanceRankActivity.this);
        chooseTimeAdapter = new ChooseTimeAdapter(Constants.chooseTimeList, VirtualPerformanceRankActivity.this);

        new Thread() {
            @Override
            public void run() {
                super.run();
                final PostParameter[] parameters = new PostParameter[0];
                String recode = ConnectUtil.httpRequest(ConnectUtil.GetVirtualBasicPerformanceRanking, parameters, ConnectUtil.POST);
                Log.e("recode", "" + recode);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = recode;
                handler.sendMessage(msg);
            }
        }.start();
    }

    void initEvents() {
        choose_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortPopWindow();
            }
        });

        choose_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePopWindow();
            }
        });
    }

    void setListAndAdaper(List<SimpleRankAbilityVO> list, String name) {

        if (list.size() == 0) {
            listView.setVisibility(View.GONE);
            no_data.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.GONE);
            ListUtils.sort(list, false, name);
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }
    }


    void showSortPopWindow() {
        View root = LayoutInflater.from(VirtualPerformanceRankActivity.this).inflate(R.layout.choose_time_listview, null);
        ListView lv_timeList = (ListView) root.findViewById(R.id.choose_time_listview);
        lv_timeList.setAdapter(chooseSortAdapter);
        pop = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        pop.setFocusable(true);
        // 不设置这句话，popWindow无法消失（点击旁边、返回键都没用）
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.showAsDropDown(select_time);
        // 在顶部显示
        pop.showAtLocation(findViewById(R.id.activity_lobby_performance), Gravity.TOP, 0, 0);

        lv_timeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                chooseSortAdapter.setPosition(position);
                sort.setText(Constants.shengHangChooseSortList.get(position));
                switch (Constants.shengHangChooseSortList.get(position)) {
                    case "成功业务数量降序":
                        choosen_sort = number;
                        switch (choosen_time) {
                            case "one_week":
                                setListAndAdaper(numberListOfWeek, "value");
                                break;

                            case "one_month":
                                setListAndAdaper(numberListOfMonth, "value");
                                break;

                            case "three_month":
                                setListAndAdaper(numberListOfQuarter, "value");
                                break;

                            case "one_year":
                                setListAndAdaper(numberListOfYear, "value");
                                break;
                        }
                        break;

                    case "成功业务金额降序":
                        choosen_sort = money;
                        switch (choosen_time) {
                            case "one_week":
                                setListAndAdaper(moneyListOfWeek, "value");
                                break;

                            case "one_month":
                                setListAndAdaper(moneyListOfMonth, "value");
                                break;

                            case "three_month":
                                setListAndAdaper(moneyListOfQuarter, "value");
                                break;

                            case "one_year":
                                setListAndAdaper(moneyListOfYear, "value");
                                break;
                        }
                        break;

                }
            }
        });
    }

    void showTimePopWindow() {
        View root = LayoutInflater.from(VirtualPerformanceRankActivity.this).inflate(R.layout.choose_time_listview, null);
        ListView lv_timeList = (ListView) root.findViewById(R.id.choose_time_listview);
        lv_timeList.setAdapter(chooseTimeAdapter);
        pop = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        pop.setFocusable(true);
        // 不设置这句话，popWindow无法消失（点击旁边、返回键都没用）
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.showAsDropDown(select_time);
        // 在顶部显示
        pop.showAtLocation(findViewById(R.id.activity_lobby_performance), Gravity.TOP, 0, 0);

        lv_timeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                chooseTimeAdapter.setPosition(position);
                select_time.setText(Constants.chooseTimeList.get(position));
                switch (Constants.chooseTimeList.get(position)) {
                    case "近一周业绩":
                        choosen_time = "one_week";
                        switch (choosen_sort) {
                            case "number":
                                ListUtils.sort(numberListOfWeek, false, "value");
                                adapter.setList(numberListOfWeek);
                                adapter.notifyDataSetChanged();
                                break;
                            case "money":
                                setListAndAdaper(moneyListOfWeek, "value");
                                break;
                        }
                        break;

                    case "近一月业绩":
                        choosen_time = "one_month";
                        switch (choosen_sort) {
                            case "number":
                                setListAndAdaper(numberListOfMonth, "value");
                                break;
                            case "money":
                                setListAndAdaper(moneyListOfMonth, "value");
                                break;
                        }
                        break;

                    case "近一季度业绩":
                        choosen_time = "three_month";
                        switch (choosen_sort) {
                            case "number":
                                setListAndAdaper(numberListOfQuarter, "value");
                                break;
                            case "money":
                                setListAndAdaper(moneyListOfQuarter, "value");
                                break;
                        }
                        break;

                    case "近一年业绩":
                        choosen_time = "one_year";
                        switch (choosen_sort) {
                            case "number":
                                setListAndAdaper(numberListOfYear, "value");
                                break;
                            case "money":
                                setListAndAdaper(moneyListOfYear, "value");
                                break;
                        }
                        break;
                }
            }
        });
    }
}

