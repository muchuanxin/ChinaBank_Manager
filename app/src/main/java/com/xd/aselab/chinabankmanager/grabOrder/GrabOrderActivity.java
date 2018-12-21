package com.xd.aselab.chinabankmanager.grabOrder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.PermissionChecker;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.Constants;
import com.xd.aselab.chinabankmanager.util.DialogFactory;
import com.xd.aselab.chinabankmanager.util.ImageSettingUtil;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Environment.MEDIA_MOUNTED;

public class GrabOrderActivity extends AppCompatActivity implements ImageSettingUtil.ImageUploadDelegate, DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView orderList;
    private TextView tv_shoot;
    private SharePreferenceUtil sp;
    private String jsonStr;
    private Dialog mDialog = null;
    private List final_list = new ArrayList();
    private final int REQUEST_CODE = 0x1001;
    private String currentStatus;
    private String phone;
    private ProgressDialog progDialog = null;
    private Intent photoData;
    private int year, month, day, hour, minute;
    private StringBuffer date, time;

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
    }

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
                        Toast.makeText(GrabOrderActivity.this, obj.getString("message"), Toast.LENGTH_SHORT);
                    } else {
                        JSONArray arr = obj.getJSONArray("list");
                        // 遍历JSON数组，把每条JSON的属性抽出来
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject temp = (JSONObject) arr.get(i);
                            List<Map<String, String>> temp_list = new ArrayList<>();
                            Map<String, String> map = new HashMap<>();
                            map.put("id", temp.getString("id"));
                            map.put("applicant", temp.getString("applicant"));
                            map.put("telephone", temp.getString("telephone"));
                            map.put("time", temp.getString("time"));
                            map.put("product_type", temp.getString("product_type"));
                            map.put("status", temp.getString("status"));
                            temp_list.add(map);
                            final_list.add(map);
                            if (temp_list.size() == 0) {
                                Toast.makeText(GrabOrderActivity.this, "当前暂无订单信息", Toast.LENGTH_SHORT).show();
                            } else {
                                OrderListAdapter adapter = new OrderListAdapter(GrabOrderActivity.this, final_list);
                                orderList.setAdapter(adapter);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(GrabOrderActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grab_order);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        sp = new SharePreferenceUtil(GrabOrderActivity.this, "user");

        // 返回按钮处理
        ImageView return_button = (ImageView) findViewById(R.id.back_btn);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 状态按钮处理
        TextView status = (TextView) findViewById(R.id.status);

        // 页面初始化时抢单数据请求与显示
        orderList = (ListView) findViewById(R.id.listView);
        accessInterface();

        // 下拉刷新的相关设定
        // 绑定组件，创建Adapter
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.Refresh);
        orderList = (ListView) findViewById(R.id.listView);

        // 刷新时的颜色
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

        // 刷新监听事件，重新请求数据
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                accessInterface();
            }
        });
    }

    // 列表项初始化Adapter
    private class OrderListAdapter extends BaseAdapter {
        private Context context;
        private List<GrabOrderItem> list;
        private Map currentMap;

        public OrderListAdapter(Context context, List list) {
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
            final View myView;
            final ViewHolder viewHolder;
            if (view == null) {
                myView = LayoutInflater.from(context).inflate(R.layout.activity_grab_order_item, null);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) myView.findViewById(R.id.name);
                viewHolder.tel = (TextView) myView.findViewById(R.id.tel);
                viewHolder.time = (TextView) myView.findViewById(R.id.time);
                viewHolder.type = (TextView) myView.findViewById(R.id.type);
                viewHolder.status = (TextView) myView.findViewById(R.id.status);
                viewHolder.image = (ImageView) myView.findViewById(R.id.phone);
                myView.setTag(viewHolder);
            } else {
                myView = view;
                viewHolder = (ViewHolder) myView.getTag();
            }
            currentMap = (Map) list.get(i);
            // 普通信息显示的设置
            viewHolder.name.setText(currentMap.get("applicant") + "");
            viewHolder.tel.setText("联系电话：" + currentMap.get("telephone"));
            viewHolder.type.setText("产品类型：" + currentMap.get("product_type"));

            // 只有抢单状态的订单才有时间
            String currentStatus = currentMap.get("status").toString();
            if ("WAIT_TO_BE_GRABED".equals(currentStatus)) {
                viewHolder.time.setVisibility(View.VISIBLE);
                viewHolder.time.setText("推荐时间：" + currentMap.get("time"));
            }

            // 状态点击的设置
            switch (currentStatus) {
                case "WAIT_TO_BE_GRABED":
                    viewHolder.status.setText("抢单");
                    viewHolder.status.setOnClickListener(new View.OnClickListener() {
                        Handler handlerForGrab = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                dismissRequestDialog();
                                super.handleMessage(msg);
                                jsonStr = (String) msg.obj;
                                if (msg.what == 0) {
                                    try {
                                        JSONObject obj = new JSONObject(jsonStr);
                                        if (obj.getString("status").equals("true")) {
                                            // 抢单成功，变成去联系
                                            // 强制类型转换，得到原始的TextView
                                            viewHolder.status.setText("去联系");
                                        }
                                        Toast.makeText(GrabOrderActivity.this, obj.getString("message"), Toast.LENGTH_SHORT);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(GrabOrderActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        };

                        @Override
                        public void onClick(View view) {
                            // 给后台发结果并处理
                            new Thread() {
                                @Override
                                public void run() {
                                    Message msg = handlerForGrab.obtainMessage();
                                    if (ConnectUtil.isNetworkAvailable(GrabOrderActivity.this)) {
                                        PostParameter[] postParameters = new PostParameter[2];
                                        postParameters[0] = new PostParameter("account", sp.getAccount());
                                        postParameters[1] = new PostParameter("id", currentMap.get("id").toString());
                                        jsonStr = ConnectUtil.httpRequest(ConnectUtil.GrabBusiness, postParameters, "POST");
                                        if (!("").equals(jsonStr) && jsonStr != null) {
                                            msg.obj = jsonStr;
                                            msg.what = 0;
                                        }
                                    } else {
                                        msg.what = 1;
                                        msg.obj = getString(R.string.network_connect_exception);
                                    }
                                    handlerForGrab.sendMessage(msg);
                                }
                            }.start();
                        }
                    });
                    break;
                case "TO_CONTACT":
                    viewHolder.status.setText("去联系");
                    viewHolder.status.setOnClickListener(new View.OnClickListener() {
                        Handler handlerForContact = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                dismissRequestDialog();
                                super.handleMessage(msg);
                                jsonStr = (String) msg.obj;
                                if (msg.what == 0) {
                                    try {
                                        JSONObject obj = new JSONObject(jsonStr);
                                        if (obj.getString("status").equals("true")) {
                                            // 抢单成功，变成去联系
                                            // 强制类型转换，得到原始的TextView
                                            viewHolder.status.setText("去联系");
                                        }
                                        Toast.makeText(GrabOrderActivity.this, obj.getString("message"), Toast.LENGTH_SHORT);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(GrabOrderActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        };

                        @Override
                        public void onClick(View view) {
                            // 弹出对话框
                            // 如果用户没联系过,提醒用户先打电话
                            if ("".equals(sp.getOrderInfo(currentMap.get("id").toString()))) {
                                new AlertDialog.Builder(GrabOrderActivity.this)
                                        .setTitle("提示")
                                        .setMessage("请先点击红色电话图标，联系用户")
                                        .setIcon(R.mipmap.ic_launcher)
                                        .create().show();
                            } else {
                                // 已经联系过，就用该时间初始化
                                String contactTime = sp.getOrderInfo(currentMap.get("id").toString());
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                try {
                                    // 用parse方法，可能会异常，所以要try-catch
                                    Date date = format.parse(contactTime);
                                    // 获取日期实例
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(date);
                                    year = calendar.get(calendar.YEAR);
                                    month = calendar.get(calendar.MONTH);
                                    day = calendar.get(calendar.DAY_OF_MONTH);
                                    hour = calendar.get(calendar.HOUR);
                                    minute = calendar.get(calendar.MINUTE);
                                } catch (java.text.ParseException e) {
                                    e.printStackTrace();
                                }

                                // 准备弹出对话框
                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(GrabOrderActivity.this);
                                final View mview = getLayoutInflater().inflate(R.layout.alertdialog_contact, null);
                                builder.setPositiveButton("确定", null);
                                builder.setView(mview);
                                final android.support.v7.app.AlertDialog dialog = builder.create();
                                dialog.show();
                                // 准备日期和时间选择的点击事件
                                final TextView tv_date = ((TextView) mview.findViewById(R.id.tv_date));
                                final TextView tv_time = ((TextView) mview.findViewById(R.id.tv_time));
                                dialog.getButton(R.id.tv_date).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (date.length() > 0) { //清除上次记录的日期
                                                    date.delete(0, date.length());
                                                }
                                                tv_date.setText(date.append(String.valueOf(year)).append("年").append(String.valueOf(month)).append("月").append(day).append("日"));
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        final AlertDialog dialog = builder.create();
                                        View dialogView = View.inflate(context, R.layout.grab_date_picker, null);
                                        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);

                                        dialog.setTitle("设置日期");
                                        dialog.setView(dialogView);
                                        dialog.show();
                                        //初始化日期监听事件
                                        datePicker.init(year, month - 1, day, GrabOrderActivity.this);
                                    }
                                });
                                dialog.getButton(R.id.tv_time).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                                        builder2.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (time.length() > 0) { //清除上次的时间
                                                    time.delete(0, time.length());
                                                }
                                                tv_time.setText(time.append(String.valueOf(hour)).append("时").append(String.valueOf(minute)).append("分"));
                                                dialog.dismiss();
                                            }
                                        });
                                        builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        AlertDialog dialog2 = builder2.create();
                                        View dialogView2 = View.inflate(context, R.layout.grab_time_picker, null);
                                        TimePicker timePicker = (TimePicker) dialogView2.findViewById(R.id.timePicker);
                                        timePicker.setCurrentHour(hour);
                                        timePicker.setCurrentMinute(minute);
                                        timePicker.setIs24HourView(true); //设置24小时制
                                        timePicker.setOnTimeChangedListener(GrabOrderActivity.this);
                                        dialog2.setTitle("设置时间");
                                        dialog2.setView(dialogView2);
                                        dialog2.show();
                                    }
                                });

                                dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        new Thread() {
                                            Message msg = handlerForContact.obtainMessage();

                                            // 把小于10的数字前加0
                                            private String one2Two(int s) {
                                                return s < 10 ? " " + s : "" + s;
                                            }

                                            @Override
                                            public void run() {
                                                super.run();
                                                PostParameter post[] = new PostParameter[3];
                                                post[0] = new PostParameter("account", sp.getAccount());
                                                post[1] = new PostParameter("id", currentMap.get("id").toString());
                                                // 如果月、天、时、分有个位数，处理成两位数
                                                post[2] = new PostParameter("contact_time", year + "-" + one2Two(month) + '-' + one2Two(day) + ' ' + one2Two(hour) + ':' + one2Two(minute));
                                                String jsonstr = ConnectUtil.httpRequest(ConnectUtil.GrabBusinessContact, post, "POST");
                                                if ("" == jsonstr || jsonstr == null) {
                                                    msg.what = 0;
                                                    msg.obj = "提交失败";
                                                } else {
                                                    msg.what = 1;
                                                    msg.obj = jsonstr;
                                                }
                                                handlerForContact.sendMessage(msg);
                                                dialog.dismiss();
                                                viewHolder.status.setText("去面签");
                                            }
                                        }.start();
                                    }
                                });
                            }
                        }
                    });
                    break;
                case "TO_FACE":
                    viewHolder.status.setText("去面签");
                    viewHolder.status.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // 准备弹出对话框
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(GrabOrderActivity.this);
                            final View mview = getLayoutInflater().inflate(R.layout.alertdialog_shoot, null);
                            builder.setPositiveButton("确定", null);
                            builder.setView(mview);
                            final android.support.v7.app.AlertDialog dialog = builder.create();
                            dialog.show();
                            // 拍照按钮的点击事件
                            tv_shoot = ((TextView) mview.findViewById(R.id.shoot));
                            dialog.getButton(R.id.shoot).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    PictureSelector.create(GrabOrderActivity.this)
                                            .openCamera(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                                            //.theme(R.style.picture_benyedie_style)//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                                            .maxSelectNum(1)// 最大图片选择数量 int
                                            .minSelectNum(1)// 最小选择数量 int
                                            //.imageSpanCount(4)// 每行显示个数 int
                                            //.selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                                            //.previewImage(true)// 是否可预览图片 true or false
                                            //.previewVideo(true)// 是否可预览视频 true or false
                                            //.enablePreviewAudio(true) // 是否可播放音频 true or false
                                            .isCamera(true)// 是否显示拍照按钮 true or false
                                            .imageFormat(PictureMimeType.JPEG)// 拍照保存图片格式后缀,默认jpeg
                                            .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                                            //可能用到         .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置该参数，会导致 .glideOverride()无效
                                            //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                                            .enableCrop(false)// 是否裁剪 true or false
                                            .compress(true)// 是否压缩 true or false
                                            //可能用到         .glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                                            //.withAspectRatio()// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                                            .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                                            //.isGif(true)// 是否显示gif图片 true or false
                                            .compressSavePath(getCompressImagePath())//压缩图片保存地址
                                            //.freeStyleCropEnabled()// 裁剪框是否可拖拽 true or false
                                            //.circleDimmedLayer()// 是否圆形裁剪 true or false
                                            //.showCropFrame()// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                                            //.showCropGrid()// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                                            .openClickSound(false)// 是否开启点击声音 true or false
                                            //.selectionMedia(selectList)// 再次启动时是否传入上次已选图片 List<LocalMedia> list
                                            .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                                            .cropCompressQuality(90)// 裁剪压缩质量 默认90 int
                                            .minimumCompressSize(100)// 小于100kb的图片不压缩
                                            .synOrAsy(true)//同步true或异步false 压缩 默认同步
                                            //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                                            //.rotateEnabled() // 裁剪是否可旋转图片 true or false
                                            //.scaleEnabled()// 裁剪是否可放大缩小图片 true or false
                                            //.videoQuality()// 视频录制质量 0 or 1 int
                                            //.videoMaxSecond(15)// 显示多少秒以内的视频or音频也可适用 int
                                            //.videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int
                                            //.recordVideoSecond()//视频秒数录制 默认60s int
                                            .forResult(Constants.TAKE_PICTURE);//结果回调onActivityResult code
                                }
                            });

                            // 拍完照的确定按钮
                            dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showProgressDialog("图片上传中...");
                                    // 可能传回来多张照片，目前的规则是只有一张
                                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(photoData);
                                    // 例如 LocalMedia 里面返回三种path
                                    // 1.media.getPath(); 为原图path
                                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                                    for (int i = 0; i < selectList.size(); i++) {
                                        final LocalMedia media = selectList.get(i);
                                        Log.e("图片------", media.getPath());
                                        final int finalI = i;

                                        new Thread() {
                                            @Override
                                            public void run() {
                                                super.run();
                                                PostParameter[] postParams = new PostParameter[2];
                                                postParams[0] = new PostParameter("account", sp.getAccount());
                                                postParams[1] = new PostParameter("id", currentMap.get("id").toString());
                                                InputStream is = null;
                                                // 图片压缩
                                                if (media.isCompressed()){
                                                    is = ImageSettingUtil.compressJPG(media.getCompressPath());
                                                }else{
                                                    is = ImageSettingUtil.compressJPG(media.getPath());
                                                }
                                                ImageSettingUtil.uploadImage(GrabOrderActivity.this, finalI, postParams, is, Constants.uploadGrabFacePhoto);
                                            }
                                        }.start();
                                    }
                                    viewHolder.status.setText("去提交");
                                }
                            });
                        }
                    });
                    break;
                case "TO_SUBMIT":
                    viewHolder.status.setText("去提交");
                    viewHolder.status.setOnClickListener(new View.OnClickListener() {
                        Handler handlerForSubmit = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                dismissRequestDialog();
                                super.handleMessage(msg);
                                jsonStr = (String) msg.obj;
                                if (msg.what == 1) {
                                    try {
                                        JSONObject obj = new JSONObject(jsonStr);
                                        if (obj.getString("status").equals("true")) {
                                            // 点击后变成已完成
                                            viewHolder.status.setText("已完成");
                                            // 已完成的状态图标要改成蓝色
                                            viewHolder.status.setBackgroundResource(R.drawable.blue_corner_rectangle);
                                        }
                                        Toast.makeText(GrabOrderActivity.this, obj.getString("message"), Toast.LENGTH_SHORT);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(GrabOrderActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        };

                        @Override
                        public void onClick(View view) {
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(GrabOrderActivity.this);
                            final View mview = getLayoutInflater().inflate(R.layout.alertdialog_submit, null);
                            builder.setPositiveButton("确定", null);
                            builder.setView(mview);

                            final android.support.v7.app.AlertDialog dialog = builder.create();
                            dialog.show();
                            dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    EditText et_number = ((EditText) mview.findViewById(R.id.number));
                                    final String number = (et_number.getText().toString().trim());
                                    EditText et_amount = ((EditText) mview.findViewById(R.id.amount));
                                    final String amount = et_amount.getText().toString().trim();

                                    if ("".equals(number)) {
                                        Toast.makeText(GrabOrderActivity.this, "请输入流水号", Toast.LENGTH_SHORT).show();
                                        Log.d("Dorise流水号", number + "----------");
                                        return;
                                    } else if ("".equals(amount)) {
                                        Toast.makeText(GrabOrderActivity.this, "请输入放款金额", Toast.LENGTH_SHORT).show();
                                        Log.d("Dorise放款金额", amount + "----------");
                                        return;
                                    } else {
                                        new Thread() {
                                            Message msg = handlerForSubmit.obtainMessage();

                                            @Override
                                            public void run() {
                                                super.run();

                                                PostParameter post[] = new PostParameter[4];
                                                post[0] = new PostParameter("account", sp.getAccount());
                                                post[1] = new PostParameter("id", currentMap.get("id").toString());
                                                post[2] = new PostParameter("serial_num", number);
                                                post[3] = new PostParameter("money", amount);
                                                String jsonstr = ConnectUtil.httpRequest(ConnectUtil.GrabBusinessSubmit, post, "POST");
                                                if ("" == jsonstr || jsonstr == null) {
                                                    msg.what = 0;
                                                    msg.obj = "提交失败";
                                                } else {
                                                    msg.what = 1;
                                                    msg.obj = jsonstr;
                                                }
                                                handlerForSubmit.sendMessage(msg);
                                                dialog.dismiss();
                                            }
                                        }.start();
                                    }
                                }
                            });

                            // 给后台发结果并处理
                            new Thread() {
                                @Override
                                public void run() {
                                    Message msg = handlerForSubmit.obtainMessage();
                                    if (ConnectUtil.isNetworkAvailable(GrabOrderActivity.this)) {
                                        PostParameter[] postParameters = new PostParameter[2];
                                        postParameters[0] = new PostParameter("account", sp.getAccount());
                                        postParameters[1] = new PostParameter("id", currentMap.get("id").toString());
                                        jsonStr = ConnectUtil.httpRequest(ConnectUtil.GrabBusiness, postParameters, "POST");
                                        if (!("").equals(jsonStr) && jsonStr != null) {
                                            msg.obj = jsonStr;
                                            msg.what = 0;
                                        }
                                    } else {
                                        msg.what = 1;
                                        msg.obj = getString(R.string.network_connect_exception);
                                    }
                                    handlerForSubmit.sendMessage(msg);
                                }
                            }.start();
                        }
                    });
                    break;
                case "SUCCESS":
                    viewHolder.status.setText("已完成");
                    // 已完成的状态图标要改成蓝色
                    viewHolder.status.setBackgroundResource(R.drawable.blue_corner_rectangle);
                default:
                    break;
            }

            // 打电话的设置
            viewHolder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 点击时获取时间，存储之
                    Calendar calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH) + 1;
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    hour = calendar.get(Calendar.HOUR);
                    minute = calendar.get(Calendar.MINUTE);
                    sp.setOrderInfo(currentMap.get("id").toString(), year + "-" + month + '-' + day + '-' + ' ' + hour + ':' + minute);
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + currentMap.get("telephone")));
                    if (Build.VERSION.SDK_INT >= 23) {

                        //判断有没有拨打电话权限
                        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                            //请求拨打电话权限
                            ActivityCompat.requestPermissions(GrabOrderActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
                        } else {
                            startActivity(intent);
                        }
                    } else {
                        startActivity(intent);
                    }
                }
            });
            return myView;
        }
    }

    private class ViewHolder {
        public TextView name;
        public TextView time;
        public TextView tel;
        public TextView type;
        public TextView status;
        public ImageView image;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.TAKE_PICTURE:
                    photoData = data;
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void setUploadProgress(int index, double x) {

    }

    @Override
    public void getRecodeFromServer(int index, String reCode) {
        try {
            JSONObject jb = new JSONObject(reCode);
            Log.d("Dorise  reCode", reCode);
            String status = jb.getString("status");
            if (status.equalsIgnoreCase("true") && index == 0) {
                String imageUrl = jb.getString("image_url");
                Looper.prepare();
                if (getIntent().getStringExtra("jump").equals("personal_info")) {
                    sp.setPhotoUrl(imageUrl);
                } else if (getIntent().getStringExtra("jump").equals("group_head")) {
                    sp.setGroupHeadUrl(imageUrl);
                }
                Toast.makeText(GrabOrderActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_OK);
                finish();
                Looper.loop();
//			sp.setHeadPhoto(headPhoto);
            } else {
                Looper.prepare();
                Toast.makeText(GrabOrderActivity.this, "上传失败，请重试", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getCompressImagePath() {
        String filePath = "";
        File appCacheDir = null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), "ChinaBank/compress/images");
            //目录是否存在
            if (!appCacheDir.exists())
                appCacheDir.mkdirs();
            filePath = appCacheDir.getAbsolutePath();
        }
        if (appCacheDir == null) { //没有SD卡
            appCacheDir = GrabOrderActivity.this.getDir("images", Context.MODE_PRIVATE);
            filePath = appCacheDir.getAbsolutePath();
        }
        Log.i("liuhaoxian", filePath);
        return filePath;
    }

    private void accessInterface() {
        showRequestDialog();
        new Thread() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                if (ConnectUtil.isNetworkAvailable(GrabOrderActivity.this)) {
                    PostParameter[] postParameters = new PostParameter[1];
                    postParameters[0] = new PostParameter("account", sp.getAccount());
                    jsonStr = ConnectUtil.httpRequest(ConnectUtil.GetGrabBusinessList, postParameters, "POST");
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

/*        private void callPhone() {
            Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phone));
            startActivity(intent);
        }*/

    public void showRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = DialogFactory.creatRequestDialog(GrabOrderActivity.this, "请稍等...");
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
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage(message);
        progDialog.show();
    }
}