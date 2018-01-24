package com.xd.aselab.chinabankmanager.kafenqi.manager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.kafenqi.manager.model.BaseVO;

import java.util.List;

/**
 * Created by Administrator on 2017/10/15.
 */

public class BasePerfAdapter extends BaseAdapter {

    private List<BaseVO> list;
    private Context mContext;
    private LayoutInflater mInflater;

    public BasePerfAdapter(List<BaseVO> list, Context mContext) {
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
            convertView = mInflater.inflate(R.layout.kafenqi_base_perf_adapter, null);
            holder.tv_base_name = (TextView)convertView.findViewById(R.id.kafenqi_base_perf_name);
            holder.tv_base_num = (TextView)convertView.findViewById(R.id.kafenqi_base_perf_num);
            holder.tv_base_money = (TextView)convertView.findViewById(R.id.kafenqi_base_perf_money);

            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }

        holder.tv_base_name.setText(list.get(position).getBase_name());
        holder.tv_base_money.setText("分期业务金额："+list.get(position).getBase_fenqi_money()+"万元");
        holder.tv_base_num.setText("分期业务数量："+list.get(position).getBase_fenqi_num()+"笔");
        return convertView;
    }

    private static class ViewHolder {
        private TextView tv_base_name;
        private TextView tv_base_num;
        private TextView tv_base_money;
    }

}
