package com.xd.aselab.chinabankmanager.kafenqi.ProvincialBankManager;

import android.content.Intent;
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
import com.xd.aselab.chinabankmanager.activity.Manager.LobbyPerformanceContactTransparent;
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

public class ProvinceManagerCardDivManagerPerformance extends AppCompatActivity {

    private ListView listView;
    private RelativeLayout back;
    private LinearLayout choose_sort;
    private LinearLayout choose_time;
    private TextView sort;
    private TextView select_time;
    private TextView no_data_text;
    private LinearLayout no_data;

    private String[] select_sort_string;
    private String[] select_time_string;

    private String choosen_time = "one_week";
    private String choosen_sort = "reocmmend_num";
    private String reocmmend_num = "reocmmend_num";
    private String success_num = "success_num";

    private ChooseSortAdapter chooseSortAdapter;
    private ChooseTimeAdapter chooseTimeAdapter;

    private List<LobbyManagerVO> lobby_manager_list_one_week = new ArrayList<>();
    private List<LobbyManagerVO> lobby_manager_list_one_month = new ArrayList<>();
    private List<LobbyManagerVO> lobby_manager_list_three_month = new ArrayList<>();
    private List<LobbyManagerVO> lobby_manager_list_one_year = new ArrayList<>();
    private List<LobbyManagerVO> list = new ArrayList<>();

    private LobbyManagerPerformanceListAdapter adapter;

    private PopupWindow pop;

    private int position;

    private String secondary_num;

    private SharePreferenceUtil spu;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    try {
                        String recode = (String) msg.obj;
                        if(recode != null) {
                            Log.e("LobbyPerformance：recode", recode);
                            JSONObject jsonObject = new JSONObject(recode);
                            String status = jsonObject.getString("status");

                            if("false".equals(status)) {
                                Toast.makeText(ProvinceManagerCardDivManagerPerformance.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else if("true".equals(status)) {
                                JSONArray oneWeekJA = jsonObject.getJSONArray("one_week");
                                JSONArray oneMonthJA = jsonObject.getJSONArray("one_month");
                                JSONArray threeMonthJA = jsonObject.getJSONArray("three_month");
                                JSONArray oneYearJA = jsonObject.getJSONArray("one_year");

                                if(oneWeekJA.length() > 0) {
                                    for(int i = 0; i < oneWeekJA.length(); i++) {
                                        JSONObject temp = (JSONObject) oneWeekJA.get(i);
                                        LobbyManagerVO LobbyManagerVO = new LobbyManagerVO();
                                        LobbyManagerVO.setAccount(temp.getString("account"));
                                        LobbyManagerVO.setName(temp.getString("name"));
                                        LobbyManagerVO.setRecommend_num(temp.getInt("recommend_num"));
                                        LobbyManagerVO.setSuccess_num(temp.getInt("success_num"));

//                                        Log.e("LobbyPerformance", i + "");

                                        lobby_manager_list_one_week.add(LobbyManagerVO);
                                        list = lobby_manager_list_one_week;
                                    }
                                }

                                if(oneMonthJA.length() > 0) {
                                    for(int i = 0; i < oneMonthJA.length(); i++) {
                                        JSONObject temp = (JSONObject) oneMonthJA.get(i);
                                        LobbyManagerVO LobbyManagerVO = new LobbyManagerVO();
                                        LobbyManagerVO.setAccount(temp.getString("account"));
                                        LobbyManagerVO.setName(temp.getString("name"));
                                        LobbyManagerVO.setRecommend_num(temp.getInt("recommend_num"));
                                        LobbyManagerVO.setSuccess_num(temp.getInt("success_num"));

//                                        Log.e("LobbyPerformance", i + "");

                                        lobby_manager_list_one_month.add(LobbyManagerVO);
                                        list = lobby_manager_list_one_month;
                                    }
                                }

                                if(threeMonthJA.length() > 0) {
                                    for(int i = 0; i < threeMonthJA.length(); i++) {
                                        JSONObject temp = (JSONObject) threeMonthJA.get(i);
                                        LobbyManagerVO LobbyManagerVO = new LobbyManagerVO();
                                        LobbyManagerVO.setAccount(temp.getString("account"));
                                        LobbyManagerVO.setName(temp.getString("name"));
                                        LobbyManagerVO.setRecommend_num(temp.getInt("recommend_num"));
                                        LobbyManagerVO.setSuccess_num(temp.getInt("success_num"));

//                                        Log.e("LobbyPerformance", i + "");

                                        lobby_manager_list_three_month.add(LobbyManagerVO);
                                        list = lobby_manager_list_three_month;
                                    }
                                }

                                if(oneYearJA.length() > 0) {
                                    for(int i = 0; i < oneYearJA.length(); i++) {
                                        JSONObject temp = (JSONObject) oneYearJA.get(i);
                                        LobbyManagerVO LobbyManagerVO = new LobbyManagerVO();
                                        LobbyManagerVO.setAccount(temp.getString("account"));
                                        LobbyManagerVO.setName(temp.getString("name"));
                                        LobbyManagerVO.setRecommend_num(temp.getInt("recommend_num"));
                                        LobbyManagerVO.setSuccess_num(temp.getInt("success_num"));

//                                        Log.e("LobbyPerformance", i + "");

                                        lobby_manager_list_one_year.add(LobbyManagerVO);
                                        list = lobby_manager_list_one_year;
                                    }
                                }
                                //默认显示一周业绩
                                adapter = new LobbyManagerPerformanceListAdapter(lobby_manager_list_one_week, ProvinceManagerCardDivManagerPerformance.this);
                                listView.setAdapter(adapter);
                                if(lobby_manager_list_one_week.size() > 0) {
                                    listView.setVisibility(View.VISIBLE);
                                    no_data.setVisibility(View.GONE);
                                } else {
                                    listView.setVisibility(View.GONE);
                                    no_data.setVisibility(View.VISIBLE);
                                }
                            }
                            else {
                                Log.e("ProvinceManagerNonCar", ProvinceManagerCardDivManagerPerformance.this.getResources().getString(R.string.status_exception));
                            }
                        }
                        else {
                            Toast.makeText(ProvinceManagerCardDivManagerPerformance.this, ProvinceManagerCardDivManagerPerformance.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    Log.e("ProvinceManagerNonCar", ProvinceManagerCardDivManagerPerformance.this.getResources().getString(R.string.handler_what_exception));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province_manager_card_div_manager_performance);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initViews();
        initDatas();
        initEvents();

    }

    void initViews() {
        back = (RelativeLayout) findViewById(R.id.province_non_car_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sort = (TextView) findViewById(R.id.province_sort);
        select_time = (TextView) findViewById(R.id.province_time);

        listView = (ListView) findViewById(R.id.list_province);

        choose_sort = (LinearLayout) findViewById(R.id.perfRanking);
        choose_time = (LinearLayout) findViewById(R.id.timeSelect);
        no_data = (LinearLayout) findViewById(R.id.province_no_data);
    }

    void initDatas() {
        secondary_num = getIntent().getStringExtra("secondary_num");
        spu = new SharePreferenceUtil(ProvinceManagerCardDivManagerPerformance.this, "user");
        select_sort_string = new String[] {"推荐办卡数量降序", "成功办卡数量降序"};
        select_time_string = new String[] {"近一周业绩", "近一月业绩", "近一季度业绩", "近一年业绩"};
        chooseSortAdapter = new ChooseSortAdapter(Constants.lobbyChooseSortList, ProvinceManagerCardDivManagerPerformance.this);
        chooseTimeAdapter = new ChooseTimeAdapter(Constants.chooseTimeList, ProvinceManagerCardDivManagerPerformance.this);

        new Thread() {
            @Override
            public void run() {
                super.run();
                final PostParameter[] parameters = new PostParameter[1];
                parameters[0] = new PostParameter("secondary_num", secondary_num);
                String recode = ConnectUtil.httpRequest(ConnectUtil.GetLobbyBossManagerPerformace, parameters, ConnectUtil.POST);
                Log.e("recode",""+recode);
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("erji_num", spu.getBranchLevel2());
                intent.setClass(ProvinceManagerCardDivManagerPerformance.this, ProvinceManagerCardDivLobbyPerformance.class);
//                intent.putExtra("position", position);
                startActivity(intent);
//                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Intent intent = new Intent();
//        if (912==resultCode){
//            position = data.getIntExtra("position", 0);
//            switch (data.getStringExtra("action")){
//                case "call" :
//                    switch (choosen_time) {
//                        case "one_week" :
//                            intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+erji_list_one_week.get(position).getLobby_tel()));
//                            break;
//
//                        case "one_month" :
//                            intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+erji_list_one_month.get(position).getLobby_tel()));
//                            break;
//
//                        case "three_month" :
//                            intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+erji_list_one_week.get(position).getLobby_tel()));
//                            break;
//
//                        case "one_year" :
//                            intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+erji_list_one_week.get(position).getLobby_tel()));
//                            break;
//
//                        default:
//                            break;
//                    }
//                    if (Build.VERSION.SDK_INT >= 23) {
//                        //判断有没有拨打电话权限
//                        if (PermissionChecker.checkSelfPermission(LobbyPerformance.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                            //请求拨打电话权限
//                            ActivityCompat.requestPermissions(LobbyPerformance.this, new String[]{Manifest.permission.CALL_PHONE}, 10015);
//                        } else {
//                            startActivity(intent);
//                        }
//
//                    } else {
//                        startActivity(intent);
//                    }
//                    break;
//            }
//        }
//    }

    private void sleep_and_finish(final int s){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(s * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    finish();
                }
            }
        }.start();
    }

    void setListAndAdaper(List<LobbyManagerVO> list, String name){

        if(list.size()==0){
            listView.setVisibility(View.GONE);
            no_data.setVisibility(View.VISIBLE);
        }else{
            listView.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.GONE);
            ListUtils.sort(list,false,name);
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }
    }



    void showSortPopWindow() {
        View root = LayoutInflater.from(ProvinceManagerCardDivManagerPerformance.this).inflate(R.layout.choose_time_listview, null);
        ListView lv_timeList = (ListView) root.findViewById(R.id.choose_time_listview);
        lv_timeList.setAdapter(chooseSortAdapter);
        pop = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        pop.setFocusable(true);
        // 不设置这句话，popWindow无法消失（点击旁边、返回键都没用）
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.showAsDropDown(select_time);
        // 在顶部显示
        pop.showAtLocation(findViewById(R.id.activity_province_manager_card_div_manager_performance), Gravity.TOP, 0, 0);

        lv_timeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                chooseSortAdapter.setPosition(position);
                sort.setText(Constants.chooseSortList.get(position));
                switch (Constants.chooseSortList.get(position)) {
                    case "推荐办卡数量降序":
                        choosen_sort = reocmmend_num;
                        switch (choosen_time) {
                            case "one_week":
                                setListAndAdaper(lobby_manager_list_one_week, "reocmmend_num");
                                break;

                            case "one_month":
                                setListAndAdaper(lobby_manager_list_one_month, "reocmmend_num");
                                break;

                            case "three_month":
                                setListAndAdaper(lobby_manager_list_three_month, "reocmmend_num");
                                break;

                            case "one_year":
                                setListAndAdaper(lobby_manager_list_one_year, "reocmmend_num");
                                break;
                        }
                        break;

                    case "成功办卡数量降序":
                        choosen_sort = success_num;
                        switch (choosen_time) {
                            case "one_week":
                                setListAndAdaper(lobby_manager_list_one_week, "success_num");
                                break;

                            case "one_month":
                                setListAndAdaper(lobby_manager_list_one_month, "success_num");
                                break;

                            case "three_month":
                                setListAndAdaper(lobby_manager_list_three_month, "success_num");
                                break;

                            case "one_year":
                                setListAndAdaper(lobby_manager_list_one_year, "success_num");
                                break;
                        }
                        break;

                }
            }
        });
    }

    void showTimePopWindow() {
        View root = LayoutInflater.from(ProvinceManagerCardDivManagerPerformance.this).inflate(R.layout.choose_time_listview,null);
        ListView lv_timeList = (ListView)root.findViewById(R.id.choose_time_listview) ;
        lv_timeList.setAdapter(chooseTimeAdapter);
        pop = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        pop.setFocusable(true);
        // 不设置这句话，popWindow无法消失（点击旁边、返回键都没用）
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.showAsDropDown(select_time);
        // 在顶部显示
        pop.showAtLocation(findViewById(R.id.activity_province_manager_card_div_manager_performance), Gravity.TOP, 0, 0);

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
                            case "reocmmend_num":
                                ListUtils.sort(lobby_manager_list_one_week,false,"reocmmend_num");
                                adapter.setList(lobby_manager_list_one_week);
                                adapter.notifyDataSetChanged();
                                break;
                            case "success_num":
                                setListAndAdaper(lobby_manager_list_one_week,"success_num");
                                break;
                        }
                        break;

                    case "近一月业绩" :
                        choosen_time="one_month";
                        switch (choosen_sort){
                            case "reocmmend_num":
                                setListAndAdaper(lobby_manager_list_one_month,"reocmmend_num");
                                break;
                            case "success_num":
                                setListAndAdaper(lobby_manager_list_one_month,"success_num");
                                break;
                        }
                        break;

                    case "近一季度业绩" :
                        choosen_time="three_month";
                        switch (choosen_sort){
                            case "reocmmend_num":
                                setListAndAdaper(lobby_manager_list_three_month,"reocmmend_num");
                                break;
                            case "success_num":
                                setListAndAdaper(lobby_manager_list_three_month,"success_num");
                                break;
                        }
                        break;

                    case "近一年业绩" :
                        choosen_time="one_year";
                        switch (choosen_sort){
                            case "reocmmend_num":
                                setListAndAdaper(lobby_manager_list_one_year,"reocmmend_num");
                                break;
                            case "success_num":
                                setListAndAdaper(lobby_manager_list_one_year,"success_num");
                                break;
                        }
                        break;
                }
            }
        });
    }
}
