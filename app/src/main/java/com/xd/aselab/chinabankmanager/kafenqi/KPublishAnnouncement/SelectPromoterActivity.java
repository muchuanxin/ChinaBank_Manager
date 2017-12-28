package com.xd.aselab.chinabankmanager.kafenqi.KPublishAnnouncement;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.kafenqi.KPublishAnnouncement.adapter.PromoterAdapter;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectPromoterActivity extends AppCompatActivity {

    private RelativeLayout back;
    private TextView ensure;

    private ArrayAdapter<String> select_adapter;

    private SharePreferenceUtil spu;
    private Handler handler;
    private SimpleDateFormat format;
    private Calendar calendar;
    private String time;
    private String[] names;
    private float[] numbers;
    private String[] select_string;
    private int type;
    private ListView list_view;
    private ImageView no_data_img;
    private TextView no_data_txt;
    private List<Map<String, String>> list;

    private int position;

    PromoterAdapter adapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==909&&requestCode==908){
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_select_promoter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.act_announ_select_promoter_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ensure = (TextView) findViewById(R.id.act_announ_promoter_ensure);
        ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> checkBoxIDList=adapter.getCheckBoxIDList();
                if(checkBoxIDList.size()<=0)
                {
                    Toast.makeText(SelectPromoterActivity.this, "请先至少选中一个4S店销售，再点击确定！", Toast.LENGTH_SHORT).show();
                }else {

                    Intent mIntent = new Intent(SelectPromoterActivity.this,AnnouncementPublish.class);
                    mIntent.putStringArrayListExtra("checkedList", checkBoxIDList);
                    Log.e("123", "3:"+checkBoxIDList.toString());
                    startActivityForResult(mIntent,908);
                }
            }
        });

        list_view = (ListView) findViewById(R.id.act_announcement_list_view);

        spu = new SharePreferenceUtil(SelectPromoterActivity.this, "user");

        no_data_img = (ImageView) findViewById(R.id.act_announcement_no_data_img);
        no_data_txt = (TextView) findViewById(R.id.act_announcement_no_data_txt);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                super.handleMessage(msg);

                switch (msg.what){
                    case 0 :
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){

                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(SelectPromoterActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONArray jsonArray = json.getJSONArray("worker_list");
                                    if (jsonArray.length()==0){
                                        list_view.setVisibility(View.GONE);
                                        no_data_img.setVisibility(View.VISIBLE);
                                        no_data_txt.setVisibility(View.VISIBLE);
                                        Toast.makeText(SelectPromoterActivity.this, "没有4S店销售的信息", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                       // Log.e("123","exist ja");
                                        list_view.setVisibility(View.VISIBLE);
                                        no_data_img.setVisibility(View.GONE);
                                        no_data_txt.setVisibility(View.GONE);
                                        list = new ArrayList<>();
                                       // Log.e("123","ja length="+jsonArray.length());
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject temp = (JSONObject) jsonArray.get(i);
                                            Map<String, String> map = new HashMap<>();
                                            map.put("account", temp.getString("account"));
                                            map.put("name", temp.getString("name"));
                                            map.put("telephone", temp.getString("telephone"));
                                            map.put("place", temp.getString("company"));
                                            list.add(map);
                                        }
                                     //   Log.e("123","list length="+list.size());

                                        adapter = new PromoterAdapter(SelectPromoterActivity.this, list);
                                        list_view.setAdapter(adapter);
                                        list_view.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    Log.e("SelectPromoter_Activity", SelectPromoterActivity.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(SelectPromoterActivity.this, SelectPromoterActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("SelectPromoter_Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("SelectPromoter_Activity", SelectPromoterActivity.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

        new Thread(){
            @Override
            public void run() {
                super.run();
                PostParameter[] params = new PostParameter[2];
                params[0] = new PostParameter("account", spu.getAccount());
                Log.e("spu.getAccount", "="+spu.getAccount());
                params[1] = new PostParameter("cookie", spu.getCookie());

                 String reCode= ConnectUtil.httpRequest(ConnectUtil.GetInstallmentWorker, params, ConnectUtil.POST);


                //reCode="{\"status\":\"true\",\"worker_list\":[{\"account\":\"123\",\"name\":\"小明\",\"telephone\":\"12345679812\",\"company\":\"某售楼处\"},{\"account\":\"1234\",\"name\":\"小红\",\"telephone\":\"12345679812\",\"company\":\"某售楼处\"},{\"account\":\"1235\",\"name\":\"小红\",\"telephone\":\"12345679812\",\"company\":\"某售楼处\"},{\"account\":\"1236\",\"name\":\"小红\",\"telephone\":\"12345679812\",\"company\":\"某售楼处\"},{\"account\":\"1237\",\"name\":\"小红\",\"telephone\":\"12345679812\",\"company\":\"某售楼处\"},{\"account\":\"1238\",\"name\":\"小红\",\"telephone\":\"12345679812\",\"company\":\"某售楼处\"},{\"account\":\"1239\",\"name\":\"小红\",\"telephone\":\"12345679812\",\"company\":\"某售楼处\"},{\"account\":\"1240\",\"name\":\"小红\",\"telephone\":\"12345679812\",\"company\":\"某售楼处\"}]}";

                Message msg = new Message();
                msg.what = 0;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();



    }


}