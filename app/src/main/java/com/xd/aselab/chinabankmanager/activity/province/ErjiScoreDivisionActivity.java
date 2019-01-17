package com.xd.aselab.chinabankmanager.activity.province;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ErjiScoreDivisionActivity extends AppCompatActivity {
    private SharePreferenceUtil spu;
    private String jsonStr;
    private List<ErjiScoreDivisionDetailItem> final_list = new ArrayList();
    private ListView lv_list;
    private RelativeLayout rl_back;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erji_score_division);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        initViews();
        getInitialData();
        handleData();
    }

    void initViews(){
        spu = new SharePreferenceUtil(ErjiScoreDivisionActivity.this, "user");

        // 返回按钮处理
        rl_back = (RelativeLayout) findViewById(R.id.ESD_back_btn);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        lv_list = (ListView) findViewById(R.id.ESD_listView);
    }

    void getInitialData(){
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                if (ConnectUtil.isNetworkAvailable(ErjiScoreDivisionActivity.this)) {
                    PostParameter[] postParameters = new PostParameter[0];
                    jsonStr = ConnectUtil.httpRequest(ConnectUtil.DivideScoreRate, postParameters, "POST");
                    if (!("").equals(jsonStr) && jsonStr != null) {
                        msg.obj = jsonStr;
                        msg.what = 0;
                    }
                } else {
                    msg.what = 1;
                    msg.obj = getString(R.string.network_connect_exception);
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    void handleData(){
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                jsonStr = (String) msg.obj;
                if (msg.what == 0) {
                    try {
                        JSONObject obj = new JSONObject(jsonStr);
                        if (obj.getString("status").equals("false")) {
                            Toast.makeText(ErjiScoreDivisionActivity.this, obj.getString("message"), Toast.LENGTH_SHORT);
                        } else {
                            JSONArray arr = obj.getJSONArray("list");
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject temp = (JSONObject) arr.get(i);
                                List<ErjiScoreDivisionDetailItem> temp_list = new ArrayList<>();
                                ErjiScoreDivisionDetailItem tempItem = new ErjiScoreDivisionDetailItem();
                                tempItem.setBank(temp.getString("erji_name"));
                                tempItem.setErji(temp.getInt("boss_rate"));
                                temp_list.add(tempItem);
                                final_list.add(tempItem);
                                if (temp_list.size() == 0) {
                                    Toast.makeText(ErjiScoreDivisionActivity.this, "当前暂无用户信息", Toast.LENGTH_SHORT).show();
                                } else {
                                    myAdapter adapter = new myAdapter(ErjiScoreDivisionActivity.this, final_list);
                                    lv_list.setAdapter(adapter);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(ErjiScoreDivisionActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        };
    }

    public class myAdapter extends BaseAdapter {
        private Context context;
        private List<ErjiScoreDivisionDetailItem> list;
        private ErjiScoreDivisionDetailItem currentItem;

        public myAdapter(Context context, List list) {
            super();
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myview;
            ViewHolder viewHolder;
            if (view == null) {
                myview = LayoutInflater.from(context).inflate(R.layout.esd_detail_item, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_bank_name = (TextView) myview.findViewById(R.id.ESD_bank_name);
                viewHolder.tv_rate = (TextView) myview.findViewById(R.id.ESD_rate);
                myview.setTag(viewHolder);
            } else {
                myview = view;
                viewHolder = (ViewHolder) myview.getTag();
            }
            currentItem = list.get(i);
            viewHolder.tv_bank_name.setText(currentItem.getBank());
            int bossRate = currentItem.getErji();
            int managerRate = currentItem.getManager();
            viewHolder.tv_rate.setText(bossRate + "% : " + managerRate + "%");
            return myview;
        }

        private class ViewHolder {
            public TextView tv_bank_name;
            public TextView tv_rate;
        }
    }

    public class ErjiScoreDivisionDetailItem {
        private String bank_name;
        private int rate_of_erji = 0;
        private int rate_of_manager = 100;

        public void setBank(String name) {
            this.bank_name = name;
        }
        public String getBank() {
            return bank_name;
        }

        public void setErji(int rateErji) {
            this.rate_of_erji = rateErji;
            this.rate_of_manager = 100-rate_of_erji;
        }
        public int getErji() {
            return rate_of_erji;
        }
        public int getManager() {
            return rate_of_manager;
        }
    }
}