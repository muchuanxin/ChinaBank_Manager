package com.xd.aselab.chinabankmanager.kafenqi.KPublishAnnouncement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.activity.ShopManage;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONObject;

import java.util.ArrayList;

public class AnnouncementPublish extends AppCompatActivity {

    private RelativeLayout back;
    private EditText annuonce_title_txt;
    private EditText annuonce_content_txt;

    private Button publish;

    private String annuonce_title;
    private String annuonce_content;
    private ProgressDialog progDialog = null;
    private SharePreferenceUtil spu;
    private Handler handler;
    private ArrayList<String> checkedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_publish);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        checkedList= new ArrayList<String>();

        checkedList = getIntent().getStringArrayListExtra("checkedList");

        back = (RelativeLayout) findViewById(R.id.activity_announcement_publish_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        spu = new SharePreferenceUtil(AnnouncementPublish.this, "user");
        annuonce_title_txt = (EditText) findViewById(R.id.activity_announcement_title);
        annuonce_content_txt = (EditText) findViewById(R.id.activity_announcement_content);

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
                                    Toast.makeText(AnnouncementPublish.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    Toast.makeText(AnnouncementPublish.this,json.getString("message"), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    setResult(909, intent);
                                    finish();
                                } else {
                                    Log.e("123", AnnouncementPublish.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(AnnouncementPublish.this, AnnouncementPublish.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("123", AnnouncementPublish.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

        publish = (Button) findViewById(R.id.activity_announcement_submit_btn);
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                annuonce_title = annuonce_title_txt.getText().toString().trim();
                annuonce_content = annuonce_content_txt.getText().toString().trim();

                if(annuonce_title.equals("")||annuonce_title==null||annuonce_content.equals("")||annuonce_content==null)
                {
                    Toast.makeText(AnnouncementPublish.this, "请先完善公告信息，再发布！", Toast.LENGTH_SHORT).show();
                }else
                {
                    new Thread(){
                        @Override
                        public void run() {

                            Log.e("123", checkedList.toString());

                            super.run();
                            PostParameter[] params = new PostParameter[4];
                            params[0] = new PostParameter("sender",spu.getAccount());
                            params[1] = new PostParameter("receiver", checkedList.toString());
                            params[2] = new PostParameter("title", annuonce_title);
                            params[3] = new PostParameter("content", annuonce_content);

                            String reCode= ConnectUtil.httpRequest(ConnectUtil.PublishAnnouncement, params, ConnectUtil.POST);
                            //reCode="{\"status\":\"true\",\"message\":\"发布成功\"}";
                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = reCode;
                            handler.sendMessage(msg);
                        }
                    }.start();

                }
            }
        });
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("请稍候...");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

}
