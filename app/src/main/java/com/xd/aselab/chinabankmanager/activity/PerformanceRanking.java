package com.xd.aselab.chinabankmanager.activity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.fragment.PerfRankingAdapter;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerformanceRanking extends AppCompatActivity {

    private RelativeLayout back;
    private TextView title;
    private TextView select;
    private ListView list_view;
    private ImageView no_data_img;
    private TextView no_data_txt;

    private SharePreferenceUtil spu;
    private SimpleDateFormat format;
    private Calendar calendar;
    private Handler handler;

    private String flag="card";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance_ranking);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.act_perf_ranking_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        spu = new SharePreferenceUtil(PerformanceRanking.this, "user");
        format = new SimpleDateFormat("yyyy-MM-dd");
        calendar = Calendar.getInstance();

        title = (TextView) findViewById(R.id.act_perf_ranking_title);
        list_view = (ListView) findViewById(R.id.act_perf_ranking_list_view);
        no_data_img = (ImageView) findViewById(R.id.act_perf_ranking_no_data_img);
        no_data_txt = (TextView) findViewById(R.id.act_perf_ranking_no_data_txt);

        select = (TextView) findViewById(R.id.act_perf_ranking_select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivityForResult(new Intent().setClass(PerformanceRanking.this, PerfRankTransparent.class),102);
                AlertDialog.Builder builder = new AlertDialog.Builder(PerformanceRanking.this);
                builder.setTitle("选择时间范围");
                //    指定下拉列表的显示数据
                final String[] select_string = {"累计业绩排名", "近半年排名", "近三月排名", "累计发展店铺"};
                //    设置一个下拉的列表选择项
                builder.setItems(select_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(PerformanceRanking.this, select_string[which], Toast.LENGTH_SHORT).show();
                        final PostParameter[] params = new PostParameter[5];
                        params[0] = new PostParameter("account", spu.getAccount());
                        params[1] = new PostParameter("branchLevel4", spu.getBranchLevel4());
                        calendar.setTime(new Date());
                        switch (select_string[which]){
                            case "累计业绩排名" :
                                title.setText("累计业绩排名");
                                flag = "card";
                                params[2] = new PostParameter("begin", "2000-01-01");
                                params[3] = new PostParameter("end", "3000-12-31");
                                break;
                            case "近半年排名" :
                                title.setText("近半年业绩排名");
                                flag = "card";
                                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 6);
                                params[2] = new PostParameter("begin", format.format(calendar.getTime()));
                                params[3] = new PostParameter("end", format.format(new Date()));
                                break;
                            case "近三月排名" :
                                title.setText("近三月业绩排名");
                                flag = "card";
                                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 3);
                                params[2] = new PostParameter("begin", format.format(calendar.getTime()));
                                params[3] = new PostParameter("end", format.format(new Date()));
                                break;
                            case "累计发展店铺" :
                                title.setText("累计发展店铺");
                                flag = "shop";
                                break;
                        }
                        params[4] = new PostParameter("cookie", spu.getCookie());
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                String reCode=null;
                                if ("card".equals(flag)){
                                    reCode = ConnectUtil.httpRequest(ConnectUtil.PERFORMANCE_RANKING_CARD, params, ConnectUtil.POST);
                                }
                                else if ("shop".equals(flag)){
                                    reCode = ConnectUtil.httpRequest(ConnectUtil.PERFORMANCE_RANKING_SHOP, params, ConnectUtil.POST);
                                }
                                Message msg = new Message();
                                msg.what = 0;
                                msg.obj = reCode;
                                handler.sendMessage(msg);
                            }
                        }.start();
                    }
                });
                builder.show();
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
                                Log.e("PerformRanking：reCode", reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(PerformanceRanking.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONArray jsonArray = json.getJSONArray("list");
                                    if (jsonArray.length()==0){
                                        list_view.setVisibility(View.GONE);
                                        no_data_img.setVisibility(View.VISIBLE);
                                        no_data_txt.setVisibility(View.VISIBLE);
                                        Toast.makeText(PerformanceRanking.this, "还没有基层经理排名信息", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        no_data_img.setVisibility(View.GONE);
                                        no_data_txt.setVisibility(View.GONE);
                                        List<Map<String, String>> list = new ArrayList<>();
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject temp = (JSONObject) jsonArray.get(i);
                                            Map<String, String> map = new HashMap<>();
                                            map.put("number", ""+ (i+1) );
                                            map.put("real_name", temp.getString("manager_realname"));
                                            if ("card".equals(flag)){
                                                map.put("count", temp.getString("sum_card_count")+"张");
                                            }
                                            else if ("shop".equals(flag)){
                                                map.put("count", temp.getString("count")+"家");
                                            }
                                            list.add(map);
                                        }
                                        PerfRankingAdapter adapter = new PerfRankingAdapter(PerformanceRanking.this, list);
                                        /*SimpleAdapter adapter = new SimpleAdapter(PerformanceRanking.this, list, R.layout.list_view_perf_ranking,
                                                new String[]{"number","real_name","count"},
                                                new int[]{R.id.list_view_perf_ranking_number, R.id.list_view_perf_ranking_name, R.id.list_view_perf_ranking_count});*/
                                        list_view.setAdapter(adapter);
                                        list_view.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    Log.e("PerformRanking_Activity", PerformanceRanking.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(PerformanceRanking.this, PerformanceRanking.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("PerformRanking_Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("PerformRanking_Activity", PerformanceRanking.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

        new Thread(){
            @Override
            public void run() {
                super.run();
                PostParameter[] params = new PostParameter[5];
                params[0] = new PostParameter("account", spu.getAccount());
                params[1] = new PostParameter("begin", "2000-01-01");
                params[2] = new PostParameter("end", "3000-12-31");
                params[3] = new PostParameter("branchLevel4", spu.getBranchLevel4());
                params[4] = new PostParameter("cookie", spu.getCookie());
                flag = "card";
                String reCode = ConnectUtil.httpRequest(ConnectUtil.PERFORMANCE_RANKING_CARD, params, ConnectUtil.POST);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();

    }

    /*@Override
    protected  void onActivityResult(int requestCode, int resultCode, final Intent intent){
        if(132==resultCode){
            final PostParameter[] params = new PostParameter[3];
            params[0] = new PostParameter("account", spu.getAccount());
            calendar.setTime(new Date());
            switch (intent.getStringExtra("time")){
                case "card_total" :
                    title.setText("累计业绩排名");
                    flag = "card";
                    params[1] = new PostParameter("begin", "2000-01-01");
                    params[2] = new PostParameter("end", "3000-12-31");
                    break;
                case "half_year" :
                    title.setText("近半年业绩排名");
                    flag = "card";
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 6);
                    params[1] = new PostParameter("begin", format.format(calendar.getTime()));
                    params[2] = new PostParameter("end", format.format(new Date()));
                    break;
                case "three_month" :
                    title.setText("近三月业绩排名");
                    flag = "card";
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 3);
                    params[1] = new PostParameter("begin", format.format(calendar.getTime()));
                    params[2] = new PostParameter("end", format.format(new Date()));
                    break;
                case "shop_total" :
                    title.setText("累计发展店铺");
                    flag = "shop";
                    break;
            }
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    String reCode=null;
                    if ("card".equals(flag)){
                        reCode = ConnectUtil.httpRequest(ConnectUtil.PERFORMANCE_RANKING_CARD, params, ConnectUtil.POST);
                    }
                    else if ("shop".equals(flag)){
                        reCode = ConnectUtil.httpRequest(ConnectUtil.PERFORMANCE_RANKING_SHOP, params, ConnectUtil.POST);
                    }
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = reCode;
                    handler.sendMessage(msg);
                }
            }.start();
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }*/
}
