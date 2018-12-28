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
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.PermissionChecker;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
    private ListView lv_orders;
    private TextView tv_shoot;
    private SharePreferenceUtil sp;
    private String jsonStr;
    private Dialog mDialog = null;
    private List<GrabOrderItem> orderList;
    private String tel, selected_date, default_date;
    private ProgressDialog progDialog = null;
    private Intent photoData;
    private int year, month, day, hour, minute;
    private StringBuffer date_buffer, time_buffer;
    private Handler handler;
    private GrabOrderAdapter adapter;
    private Boolean dateChangeFlag;

    //通话监听
    private TelephonyManager tm;
    private MyPhoneListener MyPhoneListener;
    private long callTime;//通话时长
    private long firstCallTime;//点击拨号时候的时间
    private boolean isCall = false;//是否拨打过电话
    private long callTimeThreshhold = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grab_order);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        initViews();
        getListData();
        parseData();
        initEvents();
    }

    // 控件绑定和简单初始化
    private void initViews() {
        date_buffer = new StringBuffer();
        time_buffer = new StringBuffer();
        sp = new SharePreferenceUtil(GrabOrderActivity.this, "user");

        // 返回按钮处理
        ImageView return_button = (ImageView) findViewById(R.id.back_btn);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView status = (TextView) findViewById(R.id.status);

        // 页面初始化时抢单数据请求与显示
        lv_orders = (ListView) findViewById(R.id.listView);

        // 下拉刷新的相关设定
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.Refresh);

        // 刷新时的颜色
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

        // 刷新监听事件，重新请求数据
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListData();
            }
        });

        // 日期选择变动监听标志
        dateChangeFlag = true;

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        MyPhoneListener = new MyPhoneListener();
        tm.listen(MyPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    // 获取抢单列表数据
    private void getListData() {
        // 列表清空，防止历史数据堆积
        orderList = new ArrayList<>();
        showRequestDialog();
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                if (ConnectUtil.isNetworkAvailable(GrabOrderActivity.this)) {
                    PostParameter[] postParameters = new PostParameter[1];
                    postParameters[0] = new PostParameter("account", sp.getAccount());
                    jsonStr = ConnectUtil.httpRequest(ConnectUtil.GetGrabBusinessList, postParameters, "POST");
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
                        Toast.makeText(GrabOrderActivity.this, "获取数据失败，请稍候再试", Toast.LENGTH_SHORT).show();
                        // 如果RefreshableLayout正在刷新，停止之
                        if (swipeRefreshLayout.isRefreshing()) {
                            // adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        break;
                    // 页面列表源数据获取
                    case 1:
                        try {
                            JSONObject obj = new JSONObject(jsonStr);
                            if (obj.getString("status").equals("false")) {
                                Toast.makeText(GrabOrderActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                // 获取通话时长阈值
                                callTimeThreshhold = obj.getLong("duration");
                                Log.e("Dardai_call_threshhold", callTimeThreshhold+"");
                                JSONArray arr = obj.getJSONArray("list");
                                // 遍历JSON数组，把每条JSON的属性抽出来
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject temp = (JSONObject) arr.get(i);
                                    GrabOrderItem tempItem = new GrabOrderItem();
                                    tempItem.setId(temp.getString("id"));
                                    tempItem.setApplicant(temp.getString("applicant"));
                                    tempItem.setTelephone(temp.getString("telephone"));
                                    tempItem.setTime(temp.getString("time"));
                                    tempItem.setType(temp.getString("product_type"));
                                    tempItem.setStatus(temp.getString("status"));
                                    orderList.add(tempItem);
                                }
                                Log.e("dardai_list_size", orderList.size() + "");
                                // list数据源判空
                                // 注意列表为空时也要重新绑定adapter和orderList，以防历史数据捣乱
                                if (orderList.size() == 0) {
                                    Toast.makeText(GrabOrderActivity.this, "当前暂无业务信息", Toast.LENGTH_SHORT).show();
                                    adapter.setList(orderList);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    adapter.setList(orderList);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // 如果RefreshableLayout正在刷新，停止之
                        if (swipeRefreshLayout.isRefreshing()) {
                            // adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        break;
                    // 抢单、去联系、去面签、提交数据后的处理
                    // 反馈下成功与否
                    // 如果成功就刷新页面
                    case 2:
                        try {
                            JSONObject obj = new JSONObject(jsonStr);
                            if (obj.getString("status").equals("true")) {
                                // 抢单成功，刷新页面
                                getListData();
                            }
                            Toast.makeText(GrabOrderActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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

        // item整体点击的无效化
        lv_orders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        // 开始设置点击事件，整个activity的关键
        GrabOrderAdapter.ListBtnListener mListener = new GrabOrderAdapter.ListBtnListener() {
            @Override
            public void myOnClick(final int position, View v) {
                // 电话图标的点击事件
                // 1：打电话；2：保存时间，供联系的时间控件用
                if (v.getId() == R.id.phone) {
                    // 监听通话时长清空
                    callTime = 0;
                    // 把当前订单的信息传给通话监听类，绑定通话时间
                    MyPhoneListener.setCurrentIDAndPhone(orderList.get(position).getId()+orderList.get(position).getTelephone());
                    // 点击时获取时间，存储之
                    setCallTime();
                    sp.setOrderInfo(orderList.get(position).getId(), year + "-" + one2Two(month) + '-' + one2Two(day) + ' ' + one2Two(hour) + ':' + one2Two(minute));
                    Log.e("Dardai_call_time", sp.getOrderInfo(orderList.get(position).getId()));
                    // 拿到当前item的电话号码
                    tel = orderList.get(position).getTelephone();
                    // 获取权限后打电话
                    if (ActivityCompat.checkSelfPermission(GrabOrderActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(GrabOrderActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                                Constants.ActivityCompatRequestPermissionsCode);
                        return;
                    }
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
                    startActivity(intent);
                } else if (v.getId() == R.id.status) {
                    // 重中之重，状态按钮的点击事件
                    switch (orderList.get(position).getStatus()) {
                        // 抢单时的点击
                        // 直接查询接口
                        case "WAIT_TO_BE_GRABED":
                            new Thread() {
                                @Override
                                public void run() {
                                    Message msg = new Message();
                                    if (ConnectUtil.isNetworkAvailable(GrabOrderActivity.this)) {
                                        PostParameter[] postParameters = new PostParameter[2];
                                        postParameters[0] = new PostParameter("account", sp.getAccount());
                                        postParameters[1] = new PostParameter("id", orderList.get(position).getId());
                                        Log.e("dardai_order_id", orderList.get(position).getId());
                                        jsonStr = ConnectUtil.httpRequest(ConnectUtil.GrabBusiness, postParameters, "POST");
                                        // 非空则成功
                                        if (!("").equals(jsonStr) && jsonStr != null) {
                                            msg.obj = jsonStr;
                                            msg.what = 2;
                                        }
                                    } else {
                                        msg.what = 0;
                                        msg.obj = getString(R.string.network_connect_exception);
                                    }
                                    handler.sendMessage(msg);
                                }
                            }.start();
                            break;
                        // 去联系的点击处理
                        // 先判断当前订单是否有对应的打电话时间
                        // 再弹出时间控件窗口
                        case "TO_CONTACT":
                            // 如果用户没联系过,提醒用户先打电话
                            // 注意联系用户成功与否的主键名是订单ID+联系电话
                            String contactTime = sp.getOrderInfo(orderList.get(position).getId());
                            Log.e("dardai_call_id",orderList.get(position).getId());
                            Log.e("dardai_call_time",callTime/1000 + "");
                            Log.e("dardai_call_flag",sp.getCustomerContactFlag(orderList.get(position).getId()+orderList.get(position).getTelephone())?"true":"false");
                            if ("".equals(contactTime)) {
                                new AlertDialog.Builder(GrabOrderActivity.this)
                                        .setTitle("提示")
                                        .setMessage("请先点击红色电话图标，联系用户")
                                        .create().show();
                            // 如果用户打了电话，但最近一次通话时间没达到阈值，也不行
                            } else if (callTime / 1000 < callTimeThreshhold && !sp.getCustomerContactFlag(orderList.get(position).getId()+orderList.get(position).getTelephone())) {
                                Log.e("dardai_call_time","fail:" + callTime/1000);
                                new AlertDialog.Builder(GrabOrderActivity.this)
                                        .setTitle("提示")
                                        .setMessage("您与用户通话的时长不足1分钟，请重新联系")
                                        .create().show();
                            // spu通话成功的flag设置在通话管理类的onCallStateChanged里实现
                            } else {
                                Log.e("dardai_call_time", "successd: " + callTime / 1000);
                                // 有时间戳，就拿出来把时间变量初始化
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                // 用parse方法，可能会异常，所以要try-catch
                                try {
                                    Date date = format.parse(contactTime);
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(date);
                                    year = calendar.get(calendar.YEAR);
                                    month = calendar.get(calendar.MONTH);
                                    day = calendar.get(calendar.DAY_OF_MONTH);
                                    hour = calendar.get(calendar.HOUR_OF_DAY);//24小时制
                                    minute = calendar.get(calendar.MINUTE);
                                    Log.e("dardai_contact_time", year + " " + (month + 1) + " " + day + " " + hour + " " + minute);
                                } catch (java.text.ParseException e) {
                                    e.printStackTrace();
                                }
                                // 弹出对话框
                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(GrabOrderActivity.this);
                                final View mview = getLayoutInflater().inflate(R.layout.alertdialog_contact, null);
                                builder.setPositiveButton("确定", null);
                                builder.setView(mview);
                                final android.support.v7.app.AlertDialog dialog = builder.create();
                                dialog.show();
                                // 准备日期和时间选择的点击事件
                                final TextView tv_date = ((TextView) mview.findViewById(R.id.tv_date));
                                final TextView tv_time = ((TextView) mview.findViewById(R.id.tv_time));
                                // 日期和时间tv的初始化
                                // 就用打电话时间
                                if (date_buffer.length() > 0) { //清除上次记录的日期
                                    date_buffer.delete(0, date_buffer.length());
                                }
                                if (time_buffer.length() > 0) { //清除上次记录的时间
                                    time_buffer.delete(0, time_buffer.length());
                                }
                                // 把默认时间保存下来，比较选择时间
                                default_date = new String();
                                default_date = year + one2Two(month + 1) + one2Two(day);
                                tv_date.setText(date_buffer.append(String.valueOf(year)).append("-").append(String.valueOf(month + 1)).append("-").append(day));
                                tv_time.setText(time_buffer.append(one2Two(hour)).append(" : ").append(one2Two(minute)));

                                // 日期tv点击后弹出DatePicker
                                tv_date.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // 再次创建窗口，绑定里边的picker
                                        AlertDialog.Builder builder = new AlertDialog.Builder(GrabOrderActivity.this);
                                        // 日期选择窗口的确定和取消
                                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // 更新日期buffer和tv
                                                // 选择的日期在默认时间之后才能生效，用flag监测
                                                if (dateChangeFlag) {
                                                    date_buffer.delete(0, date_buffer.length());
                                                    tv_date.setText(date_buffer.append(String.valueOf(year)).append("-").append(String.valueOf(month + 1)).append("-").append(String.valueOf(day)));
                                                } else {
                                                    Toast.makeText(GrabOrderActivity.this, "请选择" + default_date + "之后的日期", Toast.LENGTH_SHORT).show();
                                                }
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
                                        View dialogView = View.inflate(GrabOrderActivity.this, R.layout.grab_date_picker, null);
                                        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);
                                        dialog.setTitle("设置日期");
                                        dialog.setView(dialogView);

                                        // 初始化日期监听事件
                                        // datePicker选择时变动的值，通过onDateChangedListner监听
                                        datePicker.init(year, month, day, GrabOrderActivity.this);
                                        // 显示窗口
                                        dialog.show();
                                    }
                                });
                                // 时间tv点击后弹出timePicker
                                tv_time.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // 准备创建时间选择控件窗口
                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(GrabOrderActivity.this);
                                        // 时间弹窗的确定取消按钮时间
                                        builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // 更新选择的时间
                                                time_buffer.delete(0, time_buffer.length());
                                                tv_time.setText(time_buffer.append(one2Two(hour)).append(" : ").append(one2Two(minute)));
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
                                        View dialogView2 = View.inflate(GrabOrderActivity.this, R.layout.grab_time_picker, null);
                                        // 绑定timePicker控件，初始化
                                        // 和datePicker不太一样
                                        TimePicker timePicker = (TimePicker) dialogView2.findViewById(R.id.timePicker);
                                        timePicker.setCurrentHour(hour);
                                        timePicker.setCurrentMinute(minute);
                                        timePicker.setIs24HourView(true); //设置24小时制
                                        timePicker.setOnTimeChangedListener(GrabOrderActivity.this);// 变化监听
                                        dialog2.setTitle("设置时间");
                                        dialog2.setView(dialogView2);
                                        // 显示弹窗
                                        dialog2.show();
                                    }
                                });

                                // 联系弹窗的确定按钮
                                // 给接口发数据
                                dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        new Thread() {
                                            Message msg = new Message();

                                            @Override
                                            public void run() {
                                                super.run();
                                                PostParameter post[] = new PostParameter[3];
                                                post[0] = new PostParameter("account", sp.getAccount());
                                                post[1] = new PostParameter("id", orderList.get(position).getId());
                                                // 如果月、天、时、分有个位数，处理成两位数
                                                post[2] = new PostParameter("contact_time", year + "-" + one2Two(month + 1) + '-' + one2Two(day) + ' ' + one2Two(hour) + ':' + one2Two(minute));
                                                Log.e("dardai_send_time", year + "-" + one2Two(month + 1) + '-' + one2Two(day) + ' ' + one2Two(hour) + ':' + one2Two(minute));
                                                String jsonstr = ConnectUtil.httpRequest(ConnectUtil.GrabBusinessContact, post, "POST");
                                                if ("" == jsonstr || jsonstr == null) {
                                                    msg.what = 0;
                                                    msg.obj = "提交失败";
                                                } else {
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
                            break;
                        // 去拍照的点击事件
                        // 弹出面签对话框，拍照并上传
                        // 防止变量名冲突，使用namespace
                        case "TO_FACE": {
                            // 准备弹出对话框
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(GrabOrderActivity.this);
                            final View mview = getLayoutInflater().inflate(R.layout.alertdialog_shoot, null);
                            builder.setPositiveButton("确定", null);
                            builder.setView(mview);
                            final android.support.v7.app.AlertDialog dialog = builder.create();
                            dialog.show();
                            // 去面签按钮的点击事件
                            // 就是拍照
                            tv_shoot = ((TextView) mview.findViewById(R.id.shoot));
                            tv_shoot.setOnClickListener(new View.OnClickListener() {
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

                            // 窗口的确定按钮
                            dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // 没拍照前不能点确定
                                    if (tv_shoot.getText().equals("面签拍照")) {
                                        Toast.makeText(GrabOrderActivity.this, "请先拍照", Toast.LENGTH_SHORT).show();
                                    } else {
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

                                            // 上传图片
                                            new Thread() {
                                                @Override
                                                public void run() {
                                                    super.run();
                                                    PostParameter[] postParams = new PostParameter[2];
                                                    postParams[0] = new PostParameter("account", sp.getAccount());
                                                    postParams[1] = new PostParameter("id", orderList.get(position).getId());
                                                    Log.e("图片参数------", sp.getAccount() + " " + orderList.get(position).getId());
                                                    InputStream is = null;
                                                    // 图片压缩
                                                    if (media.isCompressed()) {
                                                        is = ImageSettingUtil.compressJPG(media.getCompressPath());
                                                    } else {
                                                        is = ImageSettingUtil.compressJPG(media.getPath());
                                                    }
                                                    ImageSettingUtil.uploadImage(GrabOrderActivity.this, finalI, postParams, is, Constants.uploadGrabFacePhoto);
                                                }
                                            }.start();
                                        }
                                        // 上传中的图标和对话框的消失
                                        dismissProgressDialog();
                                        dialog.dismiss();
                                        // 上传成功，刷新页面
                                        getListData();
                                    }
                                }
                            });
                        }
                        break;
                        // 去提交点击事件
                        // 提交流水号和金额，很简单
                        case "TO_SUBMIT": {
                            android.support.v7.app.AlertDialog.Builder builderForSubmit = new android.support.v7.app.AlertDialog.Builder(GrabOrderActivity.this);
                            final View viewForSubmit = getLayoutInflater().inflate(R.layout.alertdialog_submit, null);
                            builderForSubmit.setPositiveButton("确定", null);
                            builderForSubmit.setView(viewForSubmit);

                            final android.support.v7.app.AlertDialog dialog = builderForSubmit.create();
                            dialog.show();
                            dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    EditText et_number = ((EditText) viewForSubmit.findViewById(R.id.number));
                                    final String number = (et_number.getText().toString().trim());
                                    EditText et_amount = ((EditText) viewForSubmit.findViewById(R.id.amount));
                                    final String amount = et_amount.getText().toString().trim();

                                    // 隐藏输入法键盘
                                    hideKeyboard(et_amount);
                                    hideKeyboard(et_number);

                                    // 输入判空
                                    if ("".equals(number)) {
                                        Toast.makeText(GrabOrderActivity.this, "请输入流水号", Toast.LENGTH_SHORT).show();
                                        Log.d("Dorise流水号", number + "----------");
                                        return;
                                    } else if ("".equals(amount)) {
                                        Toast.makeText(GrabOrderActivity.this, "请输入放款金额", Toast.LENGTH_SHORT).show();
                                        Log.d("Dorise放款金额", amount + "----------");
                                        return;
                                    } else {
                                        // 判空通过，发接口
                                        new Thread() {
                                            Message msg = new Message();

                                            @Override
                                            public void run() {
                                                super.run();

                                                PostParameter post[] = new PostParameter[4];
                                                post[0] = new PostParameter("account", sp.getAccount());
                                                post[1] = new PostParameter("id", orderList.get(position).getId());
                                                post[2] = new PostParameter("serial_num", number);
                                                post[3] = new PostParameter("money", amount);
                                                String jsonstr = ConnectUtil.httpRequest(ConnectUtil.GrabBusinessSubmit, post, "POST");
                                                if ("" == jsonstr || jsonstr == null) {
                                                    msg.what = 0;
                                                    msg.obj = "提交失败";
                                                } else {
                                                    msg.what = 2;
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
                        break;
                        default:
                            break;
                    }
                }
            }
        };

        // 最终把adapter和ListView绑定
        adapter = new GrabOrderAdapter(GrabOrderActivity.this, orderList, mListener);
        lv_orders.setAdapter(adapter);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.TAKE_PICTURE:
                    photoData = data;
                    tv_shoot.setText("已拍照");
                    break;
                default:
                    break;
            }
        }
    }

    // 拿到年月日时分
    private void setCallTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm");
        String time = sDateFormat.format(new java.util.Date());
        hour = Integer.parseInt(time.substring(0, 2));
        minute = Integer.parseInt(time.substring(3, 5));
    }

    @Override
    public void setUploadProgress(int index, double x) {

    }

    // 拍照最后的收尾工作
    @Override
    public void getRecodeFromServer(int index, String reCode) {
        try {
            JSONObject jb = new JSONObject(reCode);
            Log.d("Dorise  reCode", reCode);
            String status = jb.getString("status");
            if (status.equalsIgnoreCase("true") && index == 0) {
                Looper.prepare();
                Toast.makeText(GrabOrderActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_OK);
                Looper.loop();
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
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
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

    private void dismissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 监听通话时长
     */
    class MyPhoneListener extends PhoneStateListener {
        String currentIDAndPhone = new String();

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE://空闲
                    System.err.println(" 空闲");
                    if (isCall) {
                        callTime = System.currentTimeMillis() - firstCallTime;
                        Log.e("dardai_endCall_id", currentIDAndPhone);
                        Log.e("dardai_endCall_time", callTime/1000+"");
                        // 挂电话的时候看通话是否够长，对应订单ID是否非空
                        if(!"".equals(currentIDAndPhone) && callTime/1000 > callTimeThreshhold){
                            // 通话成功，设置成功flag，以后就跳过时间检测
                            sp.setCustomerContactFlag(currentIDAndPhone, true);
                            // 同时把通话时间全局变量清零，防止干扰其他订单的抢单
                            callTime = 0;
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING://响铃
                    Log.e("dardai_call", "发现来电");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://通话状态
                    isCall = true;
                    firstCallTime = System.currentTimeMillis();
                    Log.e("dardai_call", "通话状态");
                    break;
                default:
                    break;
            }
        }

        public void setCurrentIDAndPhone(String s){
            currentIDAndPhone = s;
        }
    }

    // 把小于10的数字前加0
    private String one2Two(int s) {
        return s < 10 ? "0" + s : "" + s;
    }

    // 从日历取出来的月份，从0开始，比实际的小1
    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        selected_date = new String();
        selected_date = year + "" + one2Two(monthOfYear + 1) + one2Two(dayOfMonth);
        Log.e("dardai_select_date", selected_date);
        Log.e("dardai_default_date", default_date);
        // 比较选择日期和当前日期
        if (selected_date.compareTo(default_date) < 0) {
            Log.e("dardai_date_less", "true");
            dateChangeFlag = false;
        } else {
            Log.e("dardai_date_less", "false");
            dateChangeFlag = true;
            this.year = year;
            // 记得+1
            this.month = monthOfYear;
            this.day = dayOfMonth;
        }
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
    }

    //用于返回界面隐藏软键盘
    private void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }
}