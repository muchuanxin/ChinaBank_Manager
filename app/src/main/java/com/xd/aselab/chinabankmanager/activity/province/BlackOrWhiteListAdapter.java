package com.xd.aselab.chinabankmanager.activity.province;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;

import java.util.List;

public class BlackOrWhiteListAdapter extends BaseAdapter{
    private Context mcontext;
    private List<BlackOrWhiteListItem> list;
    private ListBtnListener mListener;
    private String type;

    public BlackOrWhiteListAdapter(Context context, List<BlackOrWhiteListItem> list, ListBtnListener mListener, String type) {
        super();
        this.mcontext = context;
        this.list = list;
        this.mListener = mListener;
        this.type = type;
    }

    public List<BlackOrWhiteListItem> getList() {
        return list;
    }

    public void setList(List<BlackOrWhiteListItem> list) {
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
            convertView = LayoutInflater.from(mcontext).inflate(R.layout.activity_black_or_white_item, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.bowi_name);
            viewHolder.account = (TextView) convertView.findViewById(R.id.bowi_account);
            viewHolder.tel = (TextView) convertView.findViewById(R.id.bowi_phone);
            viewHolder.bankName = (TextView) convertView.findViewById(R.id.bowi_bank);
            viewHolder.operator = (TextView) convertView.findViewById(R.id.bowi_operation);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 普通信息显示的设置
        viewHolder.name.setText(list.get(position).getName());
        viewHolder.account.setText(list.get(position).getAccount());
        String phone = list.get(position).getTelephone();
        viewHolder.tel.setText("".equals(phone) ? "（暂无电话）" : phone);
        viewHolder.bankName.setText(list.get(position).getBankName());

        // 操作显示文字的设置，根据具体类型而定
        switch (type){
            case "black":
                viewHolder.operator.setText("移出黑名单");
                break;
            case "white":
                viewHolder.operator.setText("加入黑名单");
        }

        // 位置的绑定
        viewHolder.name.setTag(position);
        viewHolder.account.setTag(position);
        viewHolder.tel.setTag(position);
        viewHolder.bankName.setTag(position);
        viewHolder.operator.setTag(position);

        // 操作点击事件的绑定
        viewHolder.operator.setOnClickListener(mListener);

        return convertView;
    }

    // 控件打包
    private static class ViewHolder {
        public TextView name;
        public TextView account;
        public TextView tel;
        public TextView bankName;
        public TextView operator;
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
