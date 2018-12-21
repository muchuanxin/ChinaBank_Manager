package com.xd.aselab.chinabankmanager.kafenqi;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.activity.RecommendListActivity;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.activity.WorkerPerformanceDetailActivity;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.Constants;
import com.xd.aselab.chinabankmanager.util.PostParameter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class KafenqiNonCarPerformanceDetail extends AppCompatActivity {

    private RelativeLayout back;
    private ListView listView;
    private List<NonCarVO> nonCarVOList = new ArrayList<>();

    private KafenqiNonCarPerfomanceDetailAdapter adapter;

    private String account;
    private String range;
    private String serial_num;
    private double loan_money;
    private String tel;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kafenqi_performance_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        account = getIntent().getStringExtra("account");
        range = getIntent().getStringExtra("range");

        initViews();
        initEvents();
        getListData();
        parseData();

    }

    void initViews() {
        back = (RelativeLayout) findViewById(R.id.act_kafenqi_non_car_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView = (ListView) findViewById(R.id.act_kafenqi_non_car_perf_detail_listview);
    }

    void getListData() {

        new Thread() {
            @Override
            public void run() {
                super.run();
                final PostParameter[] parameter = new PostParameter[2];
                parameter[0] = new PostParameter("account", account);
                parameter[1] = new PostParameter("range", range);

                String recode = ConnectUtil.httpRequest(ConnectUtil.GetNotCarRecommendList, parameter, ConnectUtil.POST);
                Log.e("reCode", "" + recode);
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

                                if ("false".equals(status)) {
                                    Toast.makeText(KafenqiNonCarPerformanceDetail.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {

                                    JSONArray recommendJA = object.getJSONArray("recommend");

                                    for (int i = 0; i < recommendJA.length(); i++) {
                                        JSONObject temp = recommendJA.getJSONObject(i);
                                        NonCarVO nonCarVO = new NonCarVO();
                                        nonCarVO.setApplicant_id(temp.getString("id"));
                                        nonCarVO.setApplicant_name(temp.getString("applicant"));
                                        nonCarVO.setTel(temp.getString("telephone"));
                                        nonCarVO.setTotal_money(temp.getDouble("money"));
                                        nonCarVO.setTime(temp.getString("time"));
                                        nonCarVO.setProduct_type(temp.getString("product_type"));
                                        nonCarVO.setInstallment_num(temp.getInt("installment_num"));
                                        nonCarVO.setSerial_num(temp.getString("serial_num"));

                                        nonCarVOList.add(nonCarVO);
                                    }

                                    adapter.setList(nonCarVOList);
                                    adapter.notifyDataSetChanged();

                                } else {
                                    Log.e("NonCarPerformanceDetail", KafenqiNonCarPerformanceDetail.this.getResources().getString(R.string.status_exception));
                                }

                            } else {
                                Toast.makeText(KafenqiNonCarPerformanceDetail.this, KafenqiNonCarPerformanceDetail.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("NonCarPerformanceDetail", "reCode为空");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    case 1:
                        Toast.makeText(KafenqiNonCarPerformanceDetail.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;

                    case 2:
                        try {
                            JSONObject obj = new JSONObject(msg.obj.toString());
                            Toast.makeText(KafenqiNonCarPerformanceDetail.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                            if(obj.get("status").equals("true")) {
                                nonCarVOList.get(msg.arg1).setSerial_num(serial_num);
                                nonCarVOList.get(msg.arg1).setLoan_money(loan_money);

                                adapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    default:
                        Log.e("NonCarPerformanceDetail", KafenqiNonCarPerformanceDetail.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };
    }

    void initEvents() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        KafenqiNonCarPerfomanceDetailAdapter.ListBtnListener mListener = new KafenqiNonCarPerfomanceDetailAdapter.ListBtnListener() {
            @Override
            public void myOnClick(final int position, View v) {
                if(v.getId() == R.id.kafenqi_perf_call) {
                    tel = nonCarVOList.get(position).getTel();

                    if (ActivityCompat.checkSelfPermission(KafenqiNonCarPerformanceDetail.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(KafenqiNonCarPerformanceDetail.this, new String[]{Manifest.permission.CALL_PHONE},
                                Constants.ActivityCompatRequestPermissionsCode);
                        return;
                    }
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
                    startActivity(intent);
                }
                else if(v.getId() == R.id.kafenqi_perf_beizhu) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(KafenqiNonCarPerformanceDetail.this);
                    final View mview = getLayoutInflater().inflate(R.layout.kafenqi_performance_detail_toast, null);

                    builder.setPositiveButton("提交", null);
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.setView(mview);
                    builder.setTitle("添加备注");

                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            EditText temp1 = ((EditText) mview.findViewById(R.id.serial_num));
                            serial_num = temp1.getText().toString().trim();
                            EditText temp2 = ((EditText) mview.findViewById(R.id.loan_money));
                            String money = temp2.getText().toString().trim();

                            if ("".equals(serial_num)) {
                                Toast.makeText(KafenqiNonCarPerformanceDetail.this, "请输入流水号", Toast.LENGTH_SHORT).show();
                                Log.d("Dorise流水号", serial_num + "----------");
                                return;
                            } else if ("".equals(loan_money)) {
                                Toast.makeText(KafenqiNonCarPerformanceDetail.this, "请输入放款金额", Toast.LENGTH_SHORT).show();
                                Log.d("Dorise放款金额", loan_money + "----------");
                                return;
                            } else {
                                loan_money = Double.parseDouble(money);
                                Log.d("Dorise  elseli面", loan_money + "----------");

                                new Thread() {
                                    Message msg = handler.obtainMessage();

                                    @Override
                                    public void run() {
                                        super.run();

                                        PostParameter post[] = new PostParameter[3];
                                        post[0] = new PostParameter("id", nonCarVOList.get(position).getApplicant_id());
                                        Log.d("Dorise", nonCarVOList.get(position).getApplicant_id());
                                        post[1] = new PostParameter("serial_num", serial_num + "");
                                        Log.d("Dorise", serial_num + "");
                                        post[2] = new PostParameter("money", loan_money + "");
                                        Log.d("Dorise", loan_money + "");

                                        //卡分期——添加备注
                                        String jsonstr = ConnectUtil.httpRequest(ConnectUtil.AddNotCarRemark, post, "POST");
                                        if ("" == jsonstr || jsonstr == null) {
                                            msg.what = 1;
                                            msg.obj = "提交失败";
                                        } else {
                                            msg.what = 2;
                                            msg.arg1 = position;
                                            msg.obj = jsonstr;
                                        }
                                        handler.sendMessage(msg);
                                        dialog.dismiss();
                                    }
                                }.start();
                            }


                        }
                    });
                }
            }
        };

        adapter = new KafenqiNonCarPerfomanceDetailAdapter(nonCarVOList, KafenqiNonCarPerformanceDetail.this, mListener);
        listView.setAdapter(adapter);


    }
}
