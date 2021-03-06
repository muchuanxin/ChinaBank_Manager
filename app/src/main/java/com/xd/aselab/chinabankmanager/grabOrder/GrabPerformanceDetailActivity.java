package com.xd.aselab.chinabankmanager.grabOrder;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.Constants;
import com.xd.aselab.chinabankmanager.util.DialogFactory;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrabPerformanceDetailActivity extends AppCompatActivity {
    private SharePreferenceUtil sp;
    private String jsonStr;
    private Dialog mDialog = null;
    private List final_list = new ArrayList();
    private ListView listView;
    private final int REQUEST_CODE = 0x1001;
    private String phone;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dismissRequestDialog();
            super.handleMessage(msg);

            jsonStr = (String) msg.obj;
            if (msg.what == 0) {
                try {
                    JSONObject obj = new JSONObject(jsonStr);
                    if (obj.getString("status").equals("false")) {
                        Toast.makeText(GrabPerformanceDetailActivity.this, obj.getString("message"), Toast.LENGTH_SHORT);
                    } else {

                        JSONArray arr = obj.getJSONArray("recommend");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject temp = (JSONObject) arr.get(i);
                            List<Map<String, String>> temp_list = new ArrayList<>();
                            Map<String, String> map = new HashMap<>();
                            map.put("id", temp.getString("id"));
                            map.put("applicant", temp.getString("applicant"));
                            map.put("telephone", temp.getString("telephone"));
                            map.put("product_type", temp.getString("product_type"));
                            map.put("serial_num", temp.getString("serial_num"));
                            DecimalFormat df = new DecimalFormat("#.00");
                            map.put("money", df.format(temp.getDouble("money"))+"");
                            map.put("status", temp.getString("confirm"));
                            temp_list.add(map);
                            final_list.add(map);
                            if (temp_list.size() == 0) {
                                Toast.makeText(GrabPerformanceDetailActivity.this, "当前暂无用户信息", Toast.LENGTH_SHORT).show();
                            } else {
                                myAdapter adapter = new myAdapter(GrabPerformanceDetailActivity.this, final_list);
                                listView.setAdapter(adapter);
                            }

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(GrabPerformanceDetailActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grab_performance_detail);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        sp = new SharePreferenceUtil(GrabPerformanceDetailActivity.this, "user");

        // 返回按钮处理
        ImageView return_button = (ImageView) findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //
        listView = (ListView) findViewById(R.id.listView);
        showRequestDialog();
        new Thread() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                if (ConnectUtil.isNetworkAvailable(GrabPerformanceDetailActivity.this)) {
                    PostParameter[] postParameters = new PostParameter[2];
                    postParameters[0] = new PostParameter("account", sp.getAccount());
                    postParameters[1] = new PostParameter("range", getIntent().getStringExtra("scope"));

                    jsonStr = ConnectUtil.httpRequest(ConnectUtil.GetVirtualInstallmentRecommendList, postParameters, "POST");
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

    public class myAdapter extends BaseAdapter {
        private Context context;
        private List<VirtualPerformanceDetailItem> list;
        private Map mymap;

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
//            CardDiv_Recommend_List_Item myitem= (CardDiv_Recommend_List_Item) getItem(i);
            if (view == null) {
                myview = LayoutInflater.from(context).inflate(R.layout.grab_performance_detail_item, null);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) myview.findViewById(R.id.name);
                viewHolder.money = (TextView) myview.findViewById(R.id.money);
                viewHolder.serial_num = (TextView) myview.findViewById(R.id.serial_num);
                viewHolder.tel = (TextView) myview.findViewById(R.id.tel);
                viewHolder.type = (TextView) myview.findViewById(R.id.type);
                viewHolder.status = (TextView) myview.findViewById(R.id.status);
                viewHolder.image = (ImageView) myview.findViewById(R.id.phone);
                myview.setTag(viewHolder);
            } else {
                myview = view;
                viewHolder = (ViewHolder) myview.getTag();
            }
            mymap = (Map) final_list.get(i);
            viewHolder.name.setText(mymap.get("applicant") + "");
            viewHolder.tel.setText("联系电话：" + mymap.get("telephone") + "");
            viewHolder.serial_num.setText("流水号：" + mymap.get("serial_num") + "");
            viewHolder.money.setText("分期金额：" + mymap.get("money") + "");
            viewHolder.status.setText("已完成");
            viewHolder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mymap.get("telephone")));

                    phone = (String) mymap.get("telephone");
/*                    if (ActivityCompat.checkSelfPermission(GrabPerformanceDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
                    }
                    startActivity(intent);*/
//                        callPhone();

                    if (Build.VERSION.SDK_INT >= 23) {

                        //判断有没有拨打电话权限
                        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                            //请求拨打电话权限
                            ActivityCompat.requestPermissions(GrabPerformanceDetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);

                        } else {
                            startActivity(intent);
                        }

                    } else {
                        startActivity(intent);
                    }
                }
            });
            if(mymap.get("product_type").equals("car")){
                viewHolder.type.setText("产品类型：" + "汽车分期");
            }else if(mymap.get("product_type").equals("parking")){
                viewHolder.type.setText("产品类型：" + "车位分期");
            }else if(mymap.get("product_type").equals("decoration")){
                viewHolder.type.setText("产品类型：" + "家装分期");
            }else if(mymap.get("product_type").equals("tour")){
                viewHolder.type.setText("产品类型：" + "旅游分期");
            }else if(mymap.get("product_type").equals("youke")){
                viewHolder.type.setText("产品类型：" + "优客业务");
            }

            return myview;
        }

/*        private void callPhone() {
            Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phone));
            startActivity(intent);
        }*/

        private class ViewHolder {
            public TextView name;
            public TextView money;
            public TextView serial_num;
            public TextView tel;
            public TextView type;
            public TextView status;
            public ImageView image;
        }
    }

    public void showRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = DialogFactory.creatRequestDialog(GrabPerformanceDetailActivity.this, "请稍等...");
        mDialog.show();
    }

    public void dismissRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                    Constants.ActivityCompatRequestPermissionsCode);
            return;
        }
        Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phone));
        startActivity(intent);
    }

}