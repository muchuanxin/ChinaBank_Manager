package com.xd.aselab.chinabankmanager.kafenqi.manager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.kafenqi.manager.model.NonCarBaseVO;

import java.text.DecimalFormat;
import java.util.List;

public class NonCarBasePerfListAdapter extends BaseAdapter {

    private List<NonCarBaseVO> list;
    private Context context;
    private LayoutInflater inflater;

    public NonCarBasePerfListAdapter(List<NonCarBaseVO> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public List<NonCarBaseVO> getList() { return list; }
    public void setList(List<NonCarBaseVO> list) { this.list = list; }

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
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate((R.layout.list_view_non_car_base_performance), null);
            holder.tv_non_car_base_name = (TextView) convertView.findViewById(R.id.non_car_base_name);
            holder.tv_non_car_base_num = (TextView) convertView.findViewById(R.id.non_car_base_success_number_text);
            holder.tv_non_car_base_money = (TextView) convertView.findViewById(R.id.non_car_base_success_money_text);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DecimalFormat df = new DecimalFormat("#0.0000");
        holder.tv_non_car_base_name.setText(list.get(position).getName());
        holder.tv_non_car_base_num.setText(list.get(position).getNumber() + "笔");
        holder.tv_non_car_base_money.setText(df.format(list.get(position).getMoney()));

        return convertView;
    }

    private static  class ViewHolder {
        private TextView tv_non_car_base_name;
        private TextView tv_non_car_base_num;
        private TextView tv_non_car_base_money;
    }
}
