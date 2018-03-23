package com.xd.aselab.chinabankmanager.kafenqi.manager.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/17.
 */

public class BasePerfAllAdapter extends BaseAdapter {

    private List<Map<String,String>> list;
    private Context mContext;
    private LayoutInflater mInflater;

    public BasePerfAllAdapter(List<Map<String, String>> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
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
        ViewHolder holder;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.base_perf_all_list_item, null);
            holder.tv_base_name = (TextView)convertView.findViewById(R.id.base_perf_all_list_name);
            holder.tv_base_card_sum = (TextView)convertView.findViewById(R.id.base_perf_all_list_card_sum);
            holder.tv_base_card_success = (TextView)convertView.findViewById(R.id.base_perf_all_list_card_success);
            holder.tv_base_fenqi_num = (TextView)convertView.findViewById(R.id.base_perf_all_list_fenqi_num);
            holder.tv_base_fenqi_money = (TextView)convertView.findViewById(R.id.base_perf_all_list_fenqi_money);
            holder.tv_base_xiaodai_num = (TextView)convertView.findViewById(R.id.base_perf_all_list_xiaodai_num);
            holder.tv_base_xiaodai_money = (TextView)convertView.findViewById(R.id.base_perf_all_list_xiaodai_money);

            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }

        holder.tv_base_name.setText(list.get(position).get("manager_realname"));
        String card_num = list.get(position).get("sum_card_count")==null?"0":list.get(position).get("sum_card_count");
        String card_success_num = list.get(position).get("success_sum")==null?"0":list.get(position).get("success_sum");
        holder.tv_base_card_sum.setText("共有"+card_num+"人扫码");
        holder.tv_base_card_success.setText("共有"+card_success_num+"人成功办卡");


        DecimalFormat df = new DecimalFormat("#0.0000");
        String fenqi_num = list.get(position).get("sum_fenqi_count")==null?"0":list.get(position).get("sum_fenqi_count");
        String fenqi_money = list.get(position).get("sum_fenqi_money")==null?"0":list.get(position).get("sum_fenqi_money");
        holder.tv_base_fenqi_num.setText("分期业务数量："+fenqi_num+"笔");
        holder.tv_base_fenqi_money.setText("分期业务金额："+df.format(Double.valueOf(fenqi_money))+"万元");

        String xiaodai_num = list.get(position).get("sum_xiaodai_count")==null?"0":list.get(position).get("sum_xiaodai_count");
        String xiaodai_money = list.get(position).get("sum_xiaodai_money")==null?"0":list.get(position).get("sum_xiaodai_money");
        holder.tv_base_xiaodai_num.setText("贷款业务数量："+xiaodai_num+"笔");
        holder.tv_base_xiaodai_money.setText("贷款业务金额："+df.format(Double.valueOf(xiaodai_money))+"万元");
        return convertView;
    }

    private static class ViewHolder {
        private TextView tv_base_name;
        private TextView tv_base_card_sum;
        private TextView tv_base_card_success;
        private TextView tv_base_fenqi_num;
        private TextView tv_base_fenqi_money;
        private TextView tv_base_xiaodai_num;
        private TextView tv_base_xiaodai_money;
    }

}
