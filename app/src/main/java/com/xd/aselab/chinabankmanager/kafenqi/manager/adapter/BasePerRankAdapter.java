package com.xd.aselab.chinabankmanager.kafenqi.manager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.kafenqi.manager.model.BaseVO;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Administrator on 2017/10/15.
 */

public class BasePerRankAdapter extends BaseAdapter {
    private List<BaseVO> list;
    private Context mContext;
    private LayoutInflater mInflater;

    public BasePerRankAdapter(List<BaseVO> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
    }

    public List<BaseVO> getList() {
        return list;
    }

    public void setList(List<BaseVO> list) {
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
        ViewHolder holder;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.base_per_rank_adapter, null);
            holder.tv_number = (TextView)convertView.findViewById(R.id.list_view_perf_ranking_number);
            holder.tv_base_name = (TextView)convertView.findViewById(R.id.list_view_perf_ranking_name);
            holder.tv_count = (TextView)convertView.findViewById(R.id.list_view_perf_ranking_count);
            holder.tv_money = (TextView)convertView.findViewById(R.id.list_view_perf_ranking_money);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }

        int rank = position+1;
        holder.tv_number.setText(""+rank);
        holder.tv_base_name.setText(list.get(position).getBase_name());
        holder.tv_count.setText(list.get(position).getBase_fenqi_num()+"笔");
        DecimalFormat df = new DecimalFormat("#0.00");
        holder.tv_money.setText(df.format(list.get(position).getBase_fenqi_money())+"万元");
        if(("fenqi_num").equals(list.get(position).getFlag())){
            holder.tv_count.setVisibility(View.VISIBLE);
            holder.tv_money.setVisibility(View.GONE);
        }else if(("fenqi_money").equals(list.get(position).getFlag())){
            holder.tv_count.setVisibility(View.GONE);
            holder.tv_money.setVisibility(View.VISIBLE);
        }
        switch (position){
            case 0 :
                holder.tv_number.setTextColor(Color.TRANSPARENT);
                holder.tv_number.setBackgroundResource(R.drawable.top1_round);
                break;
            case 1 :
                holder.tv_number.setTextColor(Color.TRANSPARENT);
                holder.tv_number.setBackgroundResource(R.drawable.top2_round);
                break;
            case 2 :
                holder.tv_number.setTextColor(Color.TRANSPARENT);
                holder.tv_number.setBackgroundResource(R.drawable.top3_round);
                break;
            default :
                holder.tv_number.setTextColor(Color.BLACK);
                holder.tv_number.setBackground(null);
                break;
        }


        return convertView;
    }

    private static class ViewHolder {
        private TextView tv_number;
        private TextView tv_base_name;
        private TextView tv_count;
        private TextView tv_money;
    }

}
