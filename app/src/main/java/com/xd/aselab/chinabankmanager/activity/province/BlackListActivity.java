package com.xd.aselab.chinabankmanager.activity.province;

import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.DialogFactory;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BlackListActivity extends AppCompatActivity {
    private SwipeRefreshLayout srl_refresh;
    private ListView lv_orders;
    private ImageView iv_back;
    private TextView tv_title;
    private SharePreferenceUtil spu;
    private String jsonStr;
    private Dialog mDialog = null;
    private List<BlackOrWhiteListItem> resourceList;
    private Handler handler;
    private BlackOrWhiteListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white_list);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        initViews();
        getListData();
        parseData();
        initEvents();
    }

    // 控件绑定
    private void initViews() {
        spu = new SharePreferenceUtil(BlackListActivity.this, "user");

        // 页面标题改成黑名单
        tv_title = (TextView)findViewById(R.id.wl_title);
        tv_title.setText("黑名单");

        // 返回按钮
        iv_back = (ImageView) findViewById(R.id.wl_back_btn);

        // 客户经理列表
        lv_orders = (ListView) findViewById(R.id.wl_list);

        // 下拉刷新布局
        srl_refresh = (SwipeRefreshLayout) findViewById(R.id.wl_refresh);
    }

    // 获取抢单列表数据
    private void getListData() {
        // 列表清空，防止历史数据堆积
        resourceList = new ArrayList<>();
        showRequestDialog();
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                if (ConnectUtil.isNetworkAvailable(BlackListActivity.this)) {
                    PostParameter[] postParameters = new PostParameter[0];
                    jsonStr = ConnectUtil.httpRequest(ConnectUtil.BlackList, postParameters, "POST");
                    // 非空则成功
                    if (!("").equals(jsonStr) && jsonStr != null) {
                        msg.what = 1;
                        msg.obj = jsonStr;
                    }
                } else {
                    // 否则失败
                    msg.what = 0;
                    msg.obj = getString(R.string.network_connect_exception);
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    // 接口拿到数据后的各种处理
    private void parseData() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dismissRequestDialog();
                super.handleMessage(msg);
                jsonStr = (String) msg.obj;
                switch (msg.what) {
                    // 接口拿数据失败的情况
                    case 0:
                        Toast.makeText(BlackListActivity.this, "获取数据失败，请稍候再试", Toast.LENGTH_SHORT).show();
                        // 如果RefreshableLayout正在刷新，停止之
                        if (srl_refresh.isRefreshing()) {
                            // adapter.notifyDataSetChanged();
                            srl_refresh.setRefreshing(false);
                        }
                        break;
                    // 页面列表源数据获取
                    case 1:
                        try {
                            JSONObject obj = new JSONObject(jsonStr);
                            if (obj.getString("status").equals("false")) {
                                Toast.makeText(BlackListActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                // 获取通话时长阈值
                                JSONArray arr = obj.getJSONArray("list");
                                // 遍历JSON数组，把每条JSON的属性抽出来
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject temp = (JSONObject) arr.get(i);
                                    BlackOrWhiteListItem tempItem = new BlackOrWhiteListItem();
                                    tempItem.setName(temp.getString("name"));
                                    tempItem.setAccount(temp.getString("account"));
                                    tempItem.setTelephone(temp.getString("telephone"));
                                    tempItem.setBankName(temp.getString("erji_name"));
                                    resourceList.add(tempItem);
                                }
                                Log.e("dardai_list_size", resourceList.size() + "");
                                // list数据源判空
                                // 注意列表为空时也要重新绑定adapter和resourceList，以防历史数据捣乱
                                if (resourceList.size() == 0) {
                                    Toast.makeText(BlackListActivity.this, "当前黑名单为空", Toast.LENGTH_SHORT).show();
                                    adapter.setList(resourceList);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    adapter.setList(resourceList);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // 如果RefreshableLayout正在刷新，停止之
                        if (srl_refresh.isRefreshing()) {
                            // adapter.notifyDataSetChanged();
                            srl_refresh.setRefreshing(false);
                        }
                        break;
                    // 提交数据后的处理
                    // 反馈下成功与否
                    // 如果成功就刷新页面
                    case 2:
                        try {
                            JSONObject obj = new JSONObject(jsonStr);
                            if (obj.getString("status").equals("true")) {
                                // 抢单成功，刷新页面
                                getListData();
                            }
                            Toast.makeText(BlackListActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    // 其他失败的情况，比如what==0
                    default:
                        break;
                }
            }
        };
    }

    private void initEvents() {
        // 刷新时的颜色
        srl_refresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

        // 刷新监听事件，重新请求数据
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListData();
            }
        });

        // 退出按钮点击事件
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // item整体点击的无效化
        lv_orders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        // 设置操作的点击事件,即移出黑名单
        BlackOrWhiteListAdapter.ListBtnListener mListener = new BlackOrWhiteListAdapter.ListBtnListener() {
            @Override
            public void myOnClick(final int position, View v) {
                // 移出黑名单按钮操作
                if (v.getId() == R.id.bowi_operation) {
                    android.support.v7.app.AlertDialog.Builder builderForSubmit = new android.support.v7.app.AlertDialog.Builder(BlackListActivity.this);
                    final View viewForSubmit = getLayoutInflater().inflate(R.layout.alertdialog_white_black_confirm, null);
                    builderForSubmit.setPositiveButton("确定", null);
                    builderForSubmit.setNegativeButton("取消", null);
                    builderForSubmit.setView(viewForSubmit);
                    final android.support.v7.app.AlertDialog dialog = builderForSubmit.create();
                    dialog.show();
                    // 把待拉黑的客户经理名字和操作信息显示出来
                    final TextView tv_name = ((TextView) viewForSubmit.findViewById(R.id.wbc_name));
                    final TextView tv_operation = ((TextView) viewForSubmit.findViewById(R.id.wbc_operation));
                    tv_name.setText(resourceList.get(position).getName());
                    tv_operation.setText("移出黑名单");
                    dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new Thread() {
                                Message msg = new Message();

                                @Override
                                public void run() {
                                    super.run();
                                    PostParameter post[] = new PostParameter[1];
                                    post[0] = new PostParameter("account", resourceList.get(position).getAccount());
                                    String jsonstr = ConnectUtil.httpRequest(ConnectUtil.RemoveOutBlackList, post, "POST");
                                    if ("" == jsonstr || jsonstr == null) {
                                        msg.what = 0;
                                        msg.obj = "提交失败";
                                    } else {
                                        // 显示提示信息即可
                                        msg.what = 2;
                                        msg.obj = jsonstr;
                                    }
                                    handler.sendMessage(msg);
                                    dialog.dismiss();
                                }
                            }.start();
                        }
                    });
                }
            }
        };

        // 最终把adapter和ListView绑定
        // 指定adapter类型为black
        adapter = new BlackOrWhiteListAdapter(BlackListActivity.this, resourceList, mListener, "black");
        lv_orders.setAdapter(adapter);
    }

    public void showRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = DialogFactory.creatRequestDialog(BlackListActivity.this, "请稍等...");
        mDialog.show();
    }

    public void dismissRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
}