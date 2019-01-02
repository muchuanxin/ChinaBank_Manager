package com.xd.aselab.chinabankmanager.activity.Manager;

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
import com.xd.aselab.chinabankmanager.activity.Manager.model.SimpleRankAbilityVO;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.ListUtils;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class VirtualGrabRankActivity extends AppCompatActivity {

    private RelativeLayout rl_back;
    private TextView title;
    private TextView select;
    private ListView list_view, null_list_view;
    private ImageView no_data_img;
    private TextView no_data_txt;

    private GrabRankAdapter adapter;
    private GrabRankNullAdapter nullAdapter;
    private List<SimpleRankAbilityVO> listOfS, listOfP1, listOfP2, listOfP3, listOfP4;
    private List<SimpleRankAbilityVO> nullListOfS, nullListOfP1, nullListOfP2, nullListOfP3, nullListOfP4;

    private boolean noData;

    private SharePreferenceUtil spu;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_grab_rank);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initViews();
        initEvents();
        accessData();
        handleData();
    }

    private void initViews() {
        rl_back = (RelativeLayout) findViewById(R.id.vgr_back_btn);
        spu = new SharePreferenceUtil(VirtualGrabRankActivity.this, "user");
        title = (TextView) findViewById(R.id.vgr_title);
        list_view = (ListView) findViewById(R.id.vgr_rank_list);
        null_list_view = (ListView) findViewById(R.id.vgr_rank_list_null);
        no_data_img = (ImageView) findViewById(R.id.vgr_default_image);
        no_data_txt = (TextView) findViewById(R.id.vgr_default_txt);
        select = (TextView) findViewById(R.id.vgr_ranking_select);

        listOfS = new ArrayList<>();
        listOfP1 = new ArrayList<>();
        listOfP2 = new ArrayList<>();
        listOfP3 = new ArrayList<>();
        listOfP4 = new ArrayList<>();
        nullListOfS = new ArrayList<>();
        nullListOfP1 = new ArrayList<>();
        nullListOfP2 = new ArrayList<>();
        nullListOfP3 = new ArrayList<>();
        nullListOfP4 = new ArrayList<>();

        noData = false;
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(VirtualGrabRankActivity.this);
                builder.setTitle("请选择排名标准");
                //    指定下拉列表的显示数据
                final String[] select_string = {"成功率排名", "P1排名", "P2排名", "P3排名", "P4排名"};
                //    设置一个下拉的列表选择项
                builder.setItems(select_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (select_string[which]) {
                            case "成功率排名":
                                setListAndAdapter(listOfS, nullListOfS,"value");
                                break;
                            case "P1排名":
                                setListAndAdapter(listOfP1, nullListOfP1,"value");
                                break;
                            case "P2排名":
                                setListAndAdapter(listOfP2, nullListOfP2,"value");
                                break;
                            case "P3排名":
                                setListAndAdapter(listOfP3, nullListOfP3,"value");
                                break;
                            case "P4排名":
                                setListAndAdapter(listOfP4, nullListOfP4,"value");
                                break;
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void accessData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                PostParameter[] params = new PostParameter[0];
                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetVirtualBasicGrabAbilityRanking, params, ConnectUtil.POST);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void handleData() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    // 页面打开时的成功率排名
                    case 0:
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode != null) {
                                Log.e("PerformRanking：reCode", reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(VirtualGrabRankActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONArray jsonArray = json.getJSONArray("success_rate");
                                    if (jsonArray.length() == 0) {
                                        noData = true;
                                        list_view.setVisibility(View.GONE);
                                        null_list_view.setVisibility(View.GONE);
                                        no_data_img.setVisibility(View.VISIBLE);
                                        no_data_txt.setVisibility(View.VISIBLE);
                                        Toast.makeText(VirtualGrabRankActivity.this, "暂无抢单能力排名信息", Toast.LENGTH_SHORT).show();
                                    } else {
                                        noData = false;
                                        no_data_img.setVisibility(View.GONE);
                                        no_data_txt.setVisibility(View.GONE);
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject temp = (JSONObject) jsonArray.get(i);
                                            SimpleRankAbilityVO srForS = new SimpleRankAbilityVO();
                                            SimpleRankAbilityVO srForp1 = new SimpleRankAbilityVO();
                                            SimpleRankAbilityVO srForp2 = new SimpleRankAbilityVO();
                                            SimpleRankAbilityVO srForp3 = new SimpleRankAbilityVO();
                                            SimpleRankAbilityVO srForp4 = new SimpleRankAbilityVO();
                                            SimpleRankAbilityVO nullSrForS = new SimpleRankAbilityVO();
                                            SimpleRankAbilityVO nullSrForp1 = new SimpleRankAbilityVO();
                                            SimpleRankAbilityVO nullSrForp2 = new SimpleRankAbilityVO();
                                            SimpleRankAbilityVO nullSrForp3 = new SimpleRankAbilityVO();
                                            SimpleRankAbilityVO nullSrForp4 = new SimpleRankAbilityVO();
                                            if (temp.getInt("success_rate")==0) {
                                                Log.e("dardai_temp_int",temp.getInt("success_rate")+"" );
                                                Log.e("dardai_temp_int",temp.getString("name"));

                                                nullSrForS.setName(temp.getString("name"));
                                                nullSrForp1.setName(temp.getString("name"));
                                                nullSrForp2.setName(temp.getString("name"));
                                                nullSrForp3.setName(temp.getString("name"));
                                                nullSrForp4.setName(temp.getString("name"));
                                                nullSrForS.setValue(0);
                                                nullSrForp1.setValue(0);
                                                nullSrForp2.setValue(0);
                                                nullSrForp3.setValue(0);
                                                nullSrForp4.setValue(0);
                                                nullListOfS.add(nullSrForS);
                                                nullListOfP1.add(nullSrForp1);
                                                nullListOfP2.add(nullSrForp2);
                                                nullListOfP3.add(nullSrForp3);
                                                nullListOfP4.add(nullSrForp4);
                                            } else {
                                                srForS.setName(temp.getString("name"));
                                                srForp1.setName(temp.getString("name"));
                                                srForp2.setName(temp.getString("name"));
                                                srForp3.setName(temp.getString("name"));
                                                srForp4.setName(temp.getString("name"));
                                                srForS.setValue(Double.parseDouble(new DecimalFormat("#.00").format((Double.parseDouble(temp.getString("success_rate"))))));
                                                srForp1.setValue(Double.parseDouble(temp.getString("P1")));
                                                srForp2.setValue(Double.parseDouble(temp.getString("P2")));
                                                srForp3.setValue(Double.parseDouble(temp.getString("P3")));
                                                srForp4.setValue(Double.parseDouble(temp.getString("P4")));
                                                listOfS.add(srForS);
                                                listOfP1.add(srForp1);
                                                listOfP2.add(srForp2);
                                                listOfP3.add(srForp3);
                                                listOfP4.add(srForp4);
                                            }
                                        }
                                        if (listOfS.size() > 0) {
                                            // 非零的数据要升序排列
                                            ListUtils.sort(listOfS, true, "value");
                                            adapter = new GrabRankAdapter(VirtualGrabRankActivity.this, listOfS);
                                            list_view.setAdapter(adapter);
                                            list_view.setVisibility(View.VISIBLE);
                                        }
                                        if (nullListOfS.size() > 0) {
                                            nullAdapter = new GrabRankNullAdapter(VirtualGrabRankActivity.this, nullListOfS, listOfS.size());
                                            null_list_view.setAdapter(nullAdapter);
                                            null_list_view.setVisibility(View.VISIBLE);
                                        }
                                        Log.e("dardai_null_size",nullListOfS.size()+"");
                                    }
                                } else {
                                    Log.e("dardai_vgr", VirtualGrabRankActivity.this.getResources().getString(R.string.status_exception));
                                }
                            } else {
                                Toast.makeText(VirtualGrabRankActivity.this, VirtualGrabRankActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("dardai_vgr", "reCode为空");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("dardai_vgr", VirtualGrabRankActivity.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };
    }

    void setListAndAdapter(List<SimpleRankAbilityVO> list, List<SimpleRankAbilityVO> nullList, String name) {
        if (!noData) {
            ListUtils.sort(list, true, name);
            adapter.setList(list);
            adapter.notifyDataSetChanged();
            nullAdapter.setList(nullList);
            nullAdapter.notifyDataSetChanged();
        }
    }
}

