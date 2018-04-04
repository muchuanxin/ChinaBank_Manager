package com.xd.aselab.chinabankmanager.kafenqi.manager.adapter;

import android.content.Context;
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
 * Created by wangyali on 2018/4/3.
 */

public class BasePerfToAdapter extends BaseAdapter {
    private List<BaseVO> list;
    private Context mContext;
    private LayoutInflater mInflater;

    public BasePerfToAdapter(List<BaseVO> list, Context mContext) {
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
        BasePerfToAdapter.ViewHolder holder;
        if(convertView==null){
            holder = new BasePerfToAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.kafenqi_base_perfto_adapter, null);
            holder.tv_base_name = (TextView)convertView.findViewById(R.id.kafenqi_base_perf_name);
            holder.tv_base_num = (TextView)convertView.findViewById(R.id.kafenqi_base_perf_num);
            holder.tv_base_money = (TextView)convertView.findViewById(R.id.kafenqi_base_perf_money);
            holder.tv_base_confrim_num= (TextView)convertView.findViewById(R.id.kafenqi_base_pref_confirm_num);
            holder.tv_base_refuse_num= (TextView)convertView.findViewById(R.id.kafenqi_base_refuse_num);
            holder.tv_base_remark_num= (TextView)convertView.findViewById(R.id.kafenqi_base_remark_num);

            convertView.setTag(holder);
        }else{
            holder=(BasePerfToAdapter.ViewHolder)convertView.getTag();
        }

        DecimalFormat df = new DecimalFormat("#0.0000");
        holder.tv_base_name.setText(list.get(position).getBase_name());
        holder.tv_base_confrim_num.setText("确认业务数量："+list.get(position).getbase_fenqi_confirm_num()+"笔");
        holder.tv_base_refuse_num.setText("拒绝业务数量："+list.get(position).getbase_fenqi_refuse_num()+"笔");
        holder.tv_base_remark_num.setText("备注业务数量："+list.get(position).getbase_fenqi_remark_num()+"笔");
        holder.tv_base_money.setText("成功业务金额："+df.format(list.get(position).getBase_fenqi_money())+"万元");
        holder.tv_base_num.setText("成功业务数量："+list.get(position).getBase_fenqi_num()+"笔");
        return convertView;
    }

    private static class ViewHolder {
        private TextView tv_base_name;
        private TextView tv_base_num;
        private TextView tv_base_money;
        private TextView tv_base_confrim_num;
        private TextView tv_base_refuse_num;
        private TextView tv_base_remark_num;
    }

}
