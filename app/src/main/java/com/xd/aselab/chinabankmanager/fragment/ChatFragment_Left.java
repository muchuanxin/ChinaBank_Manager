package com.xd.aselab.chinabankmanager.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.chat.ui.ChatActivity;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.MyExtendableAdapter;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017/12/7.
 */

public class ChatFragment_Left extends Fragment {
    private View root;
    private MyExtendableAdapter adapter;
    private ExpandableListView extendable_listview;
    private SharePreferenceUtil sp;
    private String[][] manager_head_image;

    private String[][] basic_child;
    private String[][] basic_child_headimage;
    private String[][] basic_child_account;
    private String[] parent;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.chat_fragment_left, container, false);
        extendable_listview = (ExpandableListView) root.findViewById(R.id.expandable_listview);
        sp = new SharePreferenceUtil(mContext, "user");


        extendable_listview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("jump", "left");
                intent.putExtra("receiver",basic_child_account[groupPosition][childPosition]);
                intent.putExtra("receiver_name",basic_child[groupPosition][childPosition]);
                intent.putExtra("receiver_head",basic_child_headimage[groupPosition][childPosition]);
                startActivity(intent);
                return false;
            }
        });
        return root;
    }

    @Override
    public void onAttach(Context context) {
        this.mContext = getActivity();
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
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
                    Log.d("Dorise 刘伟",my_next);
                    Message msg = handler.obtainMessage();
                    if (("").equals(my_next) || null == my_next) {
                        msg.what = 0;
                        msg.obj = "连接服务器失败";
                    } else {
                        msg.what = 1;
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
                        msg.what = 2;
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

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case 0:
                        Toast.makeText(mContext, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
//MANAGER
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
                                Log.d("Dorise 刘伟", basic_child[0][i]);
                                basic_child_headimage[0][i] = temp.getString("head_image");
                                Log.d("Dorise 刘伟", basic_child_headimage[0][i]);
                                basic_child_account[0][i] = temp.getString("account");
                                Log.d("Dorise 刘伟", basic_child_headimage[0][i]);
                            }

                            //false表示不显示人名字后面的  复选框
                            //child 是联系人名字的二维数组  head_image 是头像的二维数组
                            adapter = new MyExtendableAdapter(parent, basic_child, mContext, false, basic_child_headimage, basic_child_account);
                            adapter.setCheckBoxInvisible();
                            extendable_listview.setAdapter(adapter);
                        } else {
                            Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2:
                        //BASIC
                        Map map = (Map) msg.obj;
                        JSONObject my_boss = new JSONObject((String) map.get("my_boss"));
                        JSONObject my_special_shop = new JSONObject((String) map.get("my_special_shop"));
                        JSONObject my_4s_list = new JSONObject((String) map.get("my_4s_list"));
                        if (my_boss.getString("status").equals("false") || my_special_shop.getString("status").equals("false") || my_4s_list.getString("status").equals("false")) {
                            Toast.makeText(mContext, my_boss.getString("message"), Toast.LENGTH_SHORT).show();
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
                            adapter = new MyExtendableAdapter(parent, basic_child, mContext, false, basic_child_headimage, basic_child_account);
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


