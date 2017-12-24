package com.xd.aselab.chinabankmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.MyExtendableAdapter;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewGroup extends AppCompatActivity {

    private ImageView return_button;
    private MyExtendableAdapter adapter;
    private ExpandableListView extendable_listview;
    private Button submit;
    private SharePreferenceUtil sp;
    private String group_name;
    private String member_account;
    private String[][] head_image;
    private EditText group_name_id;
    private String[] parent;
    private String[][] basic_child;
    private String[][] basic_child_headimage;
    private String[][] basic_child_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        extendable_listview = (ExpandableListView) findViewById(R.id.extendable_listview);
        sp = new SharePreferenceUtil(NewGroup.this, "user");
        return_button = (ImageView) findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        group_name_id = (EditText) findViewById(R.id.group_name);


        if (sp.getType().equals("MANAGER")) {
            //只有下级  后台获取下级信息
            parent = new String[]{"我的银行卡客户经理"};
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    PostParameter[] params = new PostParameter[1];
                    params[0] = new PostParameter("branchLevel4", sp.getBranchLevel4());
                    String my_next = ConnectUtil.httpRequest(ConnectUtil.BASE_INFO, params, ConnectUtil.POST);
                    Message msg = handler.obtainMessage();
                    if (("").equals(my_next) || null == my_next) {
                        msg.what = 0;
                        msg.obj = "连接服务器失败";
                    } else {
                        msg.what = 3;
                        msg.obj = my_next;
                    }
                    handler.sendMessage(msg);

                }
            }.start();

        } else if (sp.getType().equals("BASIC")) {
            parent = new String[]{"我的上级", "我的特约商户", "我的4S店销售"};
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    PostParameter[] params1 = new PostParameter[1];
                    params1[0] = new PostParameter("branchLevel4", sp.getBranchLevel4());

                    //获取我的上级
                    String my_boss = ConnectUtil.httpRequest(ConnectUtil.MY_CONTACT, params1, ConnectUtil.POST);
                    Log.d("Dorise param1", my_boss);
                    //获取我的特约商户
                    PostParameter[] params2 = new PostParameter[4];
                    params2[0] = new PostParameter("account", sp.getAccount());
                    params2[1] = new PostParameter("branchLevel4", sp.getBranchLevel4());
                    params2[2] = new PostParameter("type", "" + 0);
                    params2[3] = new PostParameter("cookie", sp.getCookie());
                    String my_special_shop = ConnectUtil.httpRequest(ConnectUtil.SHOP_INFO, params2, ConnectUtil.POST);
                    Log.d("Dorise param2", my_special_shop);
                    //account
//                    branchLevel4
//                            cookie
                    //获取我的4s店销售
                    PostParameter[] params3 = new PostParameter[1];
                    params3[0] = new PostParameter("account", sp.getAccount());
                    String my_4s_list = ConnectUtil.httpRequest(ConnectUtil.GetInstallmentWorker, params3, ConnectUtil.POST);
                    Log.d("Dorise param3", my_4s_list);

                    Message msg = handler.obtainMessage();
                    if (("").equals(my_boss) || null == my_boss || ("").equals(my_special_shop) || null == my_special_shop || ("").equals(my_4s_list) || null == my_4s_list) {
                        msg.what = 0;
                        msg.obj = "连接服务器失败";
                    } else {
                        msg.what = 4;
                        Map map = new HashMap();
                        map.put("my_boss", my_boss);
                        map.put("my_special_shop", my_special_shop);
                        map.put("my_4s_list", my_4s_list);
                        msg.obj = map;
                    }
                    handler.sendMessage(msg);

                }
            }.start();


        }


        if (getIntent().getStringExtra("jump").equals("add")) {
            TextView text = (TextView) findViewById(R.id.title);
            text.setText("添加成员");
            findViewById(R.id.linear).setVisibility(View.GONE);
            findViewById(R.id.add_member).setVisibility(View.GONE);
        } else if (getIntent().getStringExtra("jump").equals("new")) {
            TextView text = (TextView) findViewById(R.id.title);
            text.setText("新建群");
            findViewById(R.id.linear).setVisibility(View.VISIBLE);
            findViewById(R.id.add_member).setVisibility(View.VISIBLE);
        }

        View foot=getLayoutInflater().inflate(R.layout.foot,extendable_listview,false);
        extendable_listview.addFooterView(foot);
        submit = (Button) foot.findViewById(R.id.submit);

//        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getStringExtra("jump").equals("new")) {
                    //新建群
                    group_name = group_name_id.getText().toString().trim();
                    if (("").equals(group_name) || null == group_name) {
                        Toast.makeText(NewGroup.this, "请输入群名称", Toast.LENGTH_SHORT).show();
                        return;
                    }//如果没有选择群成员
                    else if (null == adapter.getList() || 0 == adapter.getList().size()) {
                        if (null == adapter.getList()) {
                            Log.d("Dorise_adapterlist", "adapterlist空");
                        }
                        Toast.makeText(NewGroup.this, "请选择群成员", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        new Thread() {

                            @Override
                            public void run() {
                                super.run();

                                String member_account = listToString(adapter.getList(), ',');
                                PostParameter[] post = new PostParameter[4];
                                post[0] = new PostParameter("group_name", group_name);
                                post[1] = new PostParameter("creator_account", sp.getAccount());
                                post[2] = new PostParameter("member_account", member_account);

                                post[3] = new PostParameter("creator_name", sp.getName());
                                Log.d("Dorise 12 16 creat name",sp.getName());

                                Log.d("Dorise creator_account", sp.getAccount());
                                Log.d("Dorise member_account",member_account);

                                String jsonStr = ConnectUtil.httpRequest(ConnectUtil.CreateChatGroup, post, "POST");

                                Log.d("Dorise", group_name);
                                Log.d("Dorise creator_account", sp.getAccount());
                                Log.d("Dorise member_account", member_account);

                                Log.d("Dorise新建群后台返回", jsonStr);
                                Log.d("Dorise_PostParameter", post[2].getValue());
                                Message msg = handler.obtainMessage();

                                if (("").equals(jsonStr) || null == jsonStr) {
                                    msg.what = 0;
                                    msg.obj = "服务器连接失败";
                                } else {
                                    msg.what = 1;
                                    msg.obj = jsonStr;

                                }
                                handler.sendMessage(msg);
                            }
                        }.start();


                    }
                } else if (getIntent().getStringExtra("jump").equals("add")) {
                    //add是加人
                    if (null == adapter.getList() || 0 == adapter.getList().size()) {
                        if (null == adapter.getList()) {
                            Log.d("Dorise_adapterlist", "adapterlist空");
                        }
                        Toast.makeText(NewGroup.this, "请选择群成员", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
//群主拉人
                                String member_account = listToString(adapter.getList(), ',');
                                PostParameter[] post = new PostParameter[5];
                                post[0] = new PostParameter("owner_account", sp.getAccount());
                                post[1] = new PostParameter("group_id", getIntent().getStringExtra("group_id") + "");
                                post[2] = new PostParameter("member_account", member_account);

                                post[3] = new PostParameter("owner_name", sp.getName());
                                post[4] = new PostParameter("group_name", getIntent().getStringExtra("group_name"));

                                Log.d("Dorise 12 16 account",sp.getAccount());
                                Log.d("Dorise 12 16 gp name",getIntent().getStringExtra("group_name") + "");

                                Log.d("Dorise member_account",member_account);

                                String jsonStr = ConnectUtil.httpRequest(ConnectUtil.InviteMemberToGroup, post, "POST");
                                Message msg = handler.obtainMessage();
                                if (("").equals(jsonStr) || null == jsonStr) {
                                    msg.what = 0;
                                    msg.obj = "服务器连接失败";
                                } else {
                                    msg.what = 2;
                                    msg.obj = jsonStr;
                                }
                                handler.sendMessage(msg);
                            }
                        }.start();
                    }

                }
            }
        });


    }

    public String listToString(List list, char separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {

                    case 0:
                        Toast.makeText(NewGroup.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    //case 1  新建群
                    case 1:
                        try {
                            JSONObject obj = new JSONObject((String) msg.obj);
                            if (obj.getString("status").equals("true")) {
                                Toast.makeText(NewGroup.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                finish();
                                Log.d("Dorise建群", "成功");

                            } else if (obj.getString("status").equals("false")) {
                                Toast.makeText(NewGroup.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                                Log.d("Dorise建群", "失败");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    //case 2   建群成功后   单独添加成员的操作
                    case 2:

                        try {
                            JSONObject obj = new JSONObject(msg.obj.toString());
                            if (obj.getString("status").equals("true")) {
                                Toast.makeText(NewGroup.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                finish();
                            } else if (obj.getString("status").equals("false")) {
                                Toast.makeText(NewGroup.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                Log.d("Dorise建群", "失败");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                    //case 3   二级行管理者  下级列表处理
                    case 3:
                        JSONObject obj = new JSONObject((String) msg.obj);
                        if (obj.getString("status").equals("true")) {
                            JSONArray array = obj.getJSONArray("list");
                            basic_child = new String[1][array.length()];
                            basic_child_headimage = new String[1][array.length()];
                            basic_child_account = new String[1][array.length()];
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject temp = (JSONObject) array.get(i);
                                //basic_child获取名字   basic_child_headimage获取头像
                                basic_child[0][i] = temp.getString("real_name");
                                basic_child_headimage[0][i] = temp.getString("head_image");
                                basic_child_account[0][i] = temp.getString("account");
                            }
                            //false表示不显示人名字后面的  复选框
                            //child 是联系人名字的二维数组  head_image 是头像的二维数组
                            adapter = new MyExtendableAdapter(parent, basic_child, NewGroup.this, true, basic_child_headimage, basic_child_account);
                            adapter.setCheckBoxInvisible();
                            extendable_listview.setAdapter(adapter);
                        } else {
                            Toast.makeText(NewGroup.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    //case 4   银行卡客户经理  列表处理
                    case 4:
                        Map map = (Map) msg.obj;

                        JSONObject my_boss = new JSONObject((String) map.get("my_boss"));
                        JSONObject my_special_shop = new JSONObject((String) map.get("my_special_shop"));
                        JSONObject my_4s_list = new JSONObject((String) map.get("my_4s_list"));
                        if (my_boss.getString("status").equals("false") || my_special_shop.getString("status").equals("false") || my_4s_list.getString("status").equals("false")) {
                            Toast.makeText(NewGroup.this, my_boss.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {

                            JSONArray tempArray1 = my_boss.getJSONArray("list");
                            Log.d("Dorise tempArray1", tempArray1 + "");
                            JSONArray tempArray2 = my_special_shop.getJSONArray("shops");
                            Log.d("Dorise tempArray2", tempArray2 + "");
                            JSONArray tempArray3 = my_4s_list.getJSONArray("worker_list");
                            Log.d("Dorise tempArray3", tempArray3 + "");

                            int a = tempArray1.length(), b = tempArray2.length(), c = tempArray3.length();
                            int max = (((a > b) ? a : b) > c) ? ((a > b) ? a : b) : c;
                            basic_child = new String[3][max];
                            basic_child_headimage = new String[3][max];
                            basic_child_account = new String[3][max];
                            for (int i = 0; i < a; i++) {
                                basic_child[0][i] = tempArray1.getJSONObject(i).getString("realName");
                                Log.d("Dorise for11111", basic_child[0][i]);

                                basic_child_headimage[0][i] = tempArray1.getJSONObject(i).getString("head_image");
                                Log.d("Dorise for11111", basic_child_headimage[0][i]);

                                //可以给上级建群嘛？
//                                basic_child_account[0][i] = my_boss.getString("head_image");
                                basic_child_account[0][i] = tempArray1.getJSONObject(i).getString("jobNumber");
                                Log.d("Dorise---------", basic_child_account[0][i]);
                            }
                            for (int i = 0; i < b; i++) {
                                basic_child[1][i] = tempArray2.getJSONObject(i).getString("shopName");
                                Log.d("Dorise for22222", basic_child[1][i]);

                                basic_child_headimage[1][i] = tempArray2.getJSONObject(i).getString("head_image");
                                basic_child_account[1][i] = tempArray2.getJSONObject(i).getString("shopAccount");
                            }
                            for (int i = 0; i < c; i++) {
                                basic_child[2][i] = tempArray3.getJSONObject(i).getString("name");
                                Log.d("Dorise for33333", basic_child[2][i]);

                                basic_child_headimage[2][i] = tempArray3.getJSONObject(i).getString("head_image");
                                basic_child_account[2][i] = tempArray3.getJSONObject(i).getString("account");
                            }
                            //false表示不显示人名字后面的  复选框
                            //child 是联系人名字的二维数组  head_image 是头像的二维数组
                            adapter = new MyExtendableAdapter(parent, basic_child, NewGroup.this, true, basic_child_headimage, basic_child_account);
                            adapter.setCheckBoxInvisible();
                            extendable_listview.setAdapter(adapter);
                        }
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


}
