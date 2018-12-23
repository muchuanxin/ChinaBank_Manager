package com.xd.aselab.chinabankmanager.grabOrder;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.Constants;
import com.xd.aselab.chinabankmanager.util.ImageSettingUtil;
import com.xd.aselab.chinabankmanager.util.PostParameter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class GrabOrderAdapter extends BaseAdapter {
    private Context mcontext;
    private List<GrabOrderItem> list;
    private LayoutInflater mInflater;
    private ListBtnListener mListener;

    public GrabOrderAdapter(Context context, List<GrabOrderItem> list, ListBtnListener mListener) {
        super();
        this.mcontext = context;
        this.list = list;
        this.mListener = mListener;
        mInflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
    }

    public List<GrabOrderItem> getList() {
        return list;
    }

    public void setList(List<GrabOrderItem> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mcontext).inflate(R.layout.activity_grab_order_item, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.tel = (TextView) convertView.findViewById(R.id.tel);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.type = (TextView) convertView.findViewById(R.id.type);
            viewHolder.status = (TextView) convertView.findViewById(R.id.status);
            viewHolder.phone = (ImageView) convertView.findViewById(R.id.phone);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 普通信息显示的设置
        viewHolder.name.setText(list.get(position).getApplicant());
        viewHolder.tel.setText("联系电话：" + list.get(position).getTelephone());

        // 产品类型英中文转换
        switch (list.get(position).getType()) {
            case "car":
                viewHolder.type.setText("产品类型：" + "汽车分期");
                break;
            case "parking":
                viewHolder.type.setText("产品类型：" + "车位分期");
                break;
            case "decoration":
                viewHolder.type.setText("产品类型：" + "家装分期");
                break;
            case "tour":
                viewHolder.type.setText("产品类型：" + "旅游分期");
                break;
            case "youke":
                viewHolder.type.setText("产品类型：" + "优客业务");
                break;
        }

        // 订单状态英中文转换
        // 抢单和完成状态有特殊处理
        switch (list.get(position).getStatus()) {
            case "WAIT_TO_BE_GRABED":
                viewHolder.status.setText("抢单");
                viewHolder.time.setVisibility(View.VISIBLE);
                viewHolder.time.setText("推荐时间：" + list.get(position).getTime());
                viewHolder.status.setBackgroundResource(R.drawable.red_corner_rectangle);
                break;
            case "TO_CONTACT":
                viewHolder.status.setText("去联系");
                viewHolder.status.setBackgroundResource(R.drawable.red_corner_rectangle);
                break;
            case "TO_FACE":
                viewHolder.status.setText("去面签");
                viewHolder.status.setBackgroundResource(R.drawable.red_corner_rectangle);
                break;
            case "TO_SUBMIT":
                viewHolder.status.setText("去提交");
                viewHolder.status.setBackgroundResource(R.drawable.red_corner_rectangle);
                break;
            case "SUCCESS":
                viewHolder.status.setText("已完成");
                viewHolder.status.setBackgroundResource(R.drawable.blue_corner_rectangle);
                break;
        }

        // 位置的绑定
        viewHolder.phone.setTag(position);
        viewHolder.name.setTag(position);
        viewHolder.status.setTag(position);
        viewHolder.tel.setTag(position);
        viewHolder.time.setTag(position);
        viewHolder.type.setTag(position);

        // 状态和电话点击事件的绑定
        viewHolder.status.setOnClickListener(mListener);
        viewHolder.phone.setOnClickListener(mListener);

        return convertView;
    }

    // 控件打包
    private static class ViewHolder {
        public TextView name;
        public TextView time;
        public TextView tel;
        public TextView type;
        public TextView status;
        public ImageView phone;
    }

    // 控件点击监听器
    public static abstract class ListBtnListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            myOnClick((Integer) v.getTag(), v);
        }

        public abstract void myOnClick(int position, View v);

    }
}