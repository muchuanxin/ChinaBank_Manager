package com.xd.aselab.chinabankmanager.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.activity.Login;
import com.xd.aselab.chinabankmanager.activity.MyManageBase;
import com.xd.aselab.chinabankmanager.activity.MyManagement;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.activity.NewNotificationDetailActivity;
import com.xd.aselab.chinabankmanager.kafenqi.KafenqiActivity;
import com.xd.aselab.chinabankmanager.kafenqi.ProvincialBankManager.KSubManagerPerformanceActivity;
import com.xd.aselab.chinabankmanager.kafenqi.ProvincialBankManager.KafenqiFirstActivity;
import com.xd.aselab.chinabankmanager.kafenqi.manager.MannagerKafenqiActivity;
import com.xd.aselab.chinabankmanager.marketingGuide.MarketingGuideNew;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.ImageLoader;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProvinceMainFragment extends Fragment {

    private View root;
    private ImageCycleView imageCycleView;
    private ImageCycleView.ImageCycleViewListener mAdCycleViewListener;
    private Handler handler;
    private TextView toast_i;
    private ImageView personal_info;
    private LinearLayout ll_card;
    private LinearLayout ll_kafenqi;
    private LinearLayout ll_yingxiaodaohang;
    private LinearLayout ll_tehuishangquan;
    private LinearLayout ll_main_job;
    private LinearLayout ll_shichanghuodong;
    private SharePreferenceUtil spu;
    private TextView tv_user_name;
    private RelativeLayout rl_toast_identify;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_province_main, container, false);
        initView();
        initDatas();
        initEvents();
        return root;
    }

    void initView(){
        imageCycleView = (ImageCycleView)root.findViewById(R.id.act_main_ImageCycleView);
        toast_i = (TextView)root. findViewById(R.id.act_main_toast_i);
        personal_info = (ImageView)root. findViewById(R.id.act_main_personal_info);
        imageCycleView = (ImageCycleView)root. findViewById(R.id.act_main_ImageCycleView);
        ll_card = (LinearLayout)root.findViewById(R.id.act_main_card);
        ll_kafenqi = (LinearLayout)root.findViewById(R.id.act_main_kafenqi);
        ll_yingxiaodaohang = (LinearLayout)root.findViewById(R.id.act_main_yingxiaodaohang);
        ll_tehuishangquan = (LinearLayout)root.findViewById(R.id.act_main_tehuishangquan);
        ll_main_job = (LinearLayout)root.findViewById(R.id.act_main_job);
        ll_shichanghuodong = (LinearLayout)root.findViewById(R.id.act_main_shichanghuodong);
        tv_user_name = (TextView)root.findViewById(R.id.show_user_name);
        rl_toast_identify = (RelativeLayout)root.findViewById(R.id.rl_toast_identity);
    }

    void initDatas(){
        spu = new SharePreferenceUtil(getActivity(),"user");

        if(spu.getisLogin()){
            tv_user_name.setText(spu.getName());
        }else {
            tv_user_name.setText("登录");
        }

        mAdCycleViewListener = new ImageCycleView.ImageCycleViewListener() {
            @Override
            public void onImageClick(int position, View imageView) {
            }

            @Override
            public void displayImage(String imageURL, ImageView imageView) {
                if ("no_picture".equals(imageURL)){
                    imageView.setImageResource(R.drawable.placeholder2);
                }
                else {
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.loadBitmap(getActivity(), imageURL, imageView, 0);
                }
            }
        };

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
                                    Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONArray jsonArray = json.getJSONArray("list");
                                    ArrayList<String> imageUrls = new ArrayList<>();
                                    for (int i=0; i<jsonArray.length(); i++){
                                        JSONObject temp = (JSONObject) jsonArray.get(i);
                                        String picture_url = temp.getString("picture_url");
                                        imageUrls.add(picture_url);
                                    }
                                    if (imageUrls.size()>0)
                                        imageCycleView.setImageResources(imageUrls, mAdCycleViewListener);
                                    else {
                                        imageUrls.add("no_picture");
                                        imageCycleView.setImageResources(imageUrls, mAdCycleViewListener);
                                    }
                                } else {
                                    android.util.Log.e("MyContact_Activity", getActivity().getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                android.util.Log.e("MyContact_Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        android.util.Log.e("MyContact_Activity",getActivity().getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };
    }

    private void initEvents(){

        rl_toast_identify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spu.getisLogin()){
                    if(spu.getType().equals("BASIC")){
                        Toast.makeText(getActivity(), "银行卡客户经理，您好", Toast.LENGTH_SHORT).show();
                    }else  if(spu.getType().equals("ProvinceMannager")){
                        Toast.makeText(getActivity(), "省行管理者，您好", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity(), "二级行管理者，您好", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), Login.class);
                    intent.putExtra("clickView", "toast_i");
                    startActivity(intent);
                }

            }
        });

        ll_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"省行管理者信用卡发管理界面",Toast.LENGTH_SHORT).show();
            }
        });

    /*    ll_kafenqi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"省行管理者卡分期管理界面",Toast.LENGTH_SHORT).show();
            }
        });*/

        ll_kafenqi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), KafenqiFirstActivity.class);
                startActivity(intent);
            }
        });


        ll_yingxiaodaohang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), MarketingGuideNew.class);
                startActivity(intent);
            }
        });

        ll_tehuishangquan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Toast.makeText(getActivity(),"特惠商圈界面",Toast.LENGTH_SHORT).show();
            }
        });

        ll_main_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"银行近期重点工作界面",Toast.LENGTH_SHORT).show();
            }
        });

        ll_shichanghuodong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"市场活动集锦界面",Toast.LENGTH_SHORT).show();
            }
        });

        if (spu.getisLogin()){
            JPushInterface.setAlias(getActivity(),0,spu.getAccount());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(){
            @Override
            public void run() {
                super.run();
                PostParameter[] params = new PostParameter[1];
                params[0] = new PostParameter("rolling", "picture");
                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetRollingPicture, params, ConnectUtil.POST);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();

        imageCycleView.startImageCycle();
        if (spu.getisLogin()){
            tv_user_name.setText(spu.getName());
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        imageCycleView.pushImageCycle();
    }
}
