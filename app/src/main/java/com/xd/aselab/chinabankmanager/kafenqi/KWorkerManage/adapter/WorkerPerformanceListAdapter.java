package com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.model.WorkerVO;

import java.util.List;

/**
 * Created by Administrator on 2017/10/4.
 */

public class WorkerPerformanceListAdapter extends BaseAdapter {

    private List<WorkerVO> list;
    private Context mContext;
    private LayoutInflater mInflater;

    public WorkerPerformanceListAdapter(List<WorkerVO> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
    }

    public List<WorkerVO> getList() {
        return list;
    }

    public void setList(List<WorkerVO> list) {
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
            convertView = mInflater.inflate(R.layout.worker_performance_list_item, null);
            holder.tv_worker_name = (TextView)convertView.findViewById(R.id.worker_name);
            holder.tv_worker_status = (TextView)convertView.findViewById(R.id.worker_status);
            holder.tv_worker_address = (TextView)convertView.findViewById(R.id.worker_address);
            holder.tv_recommend_number = (TextView)convertView.findViewById(R.id.worker_recommend_number);
            holder.tv_success_number = (TextView)convertView.findViewById(R.id.worker_success_number);
            holder.tv_success_money = (TextView)convertView.findViewById(R.id.worker_success_money);
            holder.tv_sum_credit = (TextView)convertView.findViewById(R.id.worker_sum_credit);
            holder.tv_exchange_credit = (TextView) convertView.findViewById(R.id.worker_exchange_credit);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }

        holder.tv_worker_name.setText(list.get(position).getName());
        holder.tv_worker_address.setText("工作单位："+list.get(position).getWorker_address());
        holder.tv_recommend_number.setText("推荐分期业务数量："+list.get(position).getRecommend_number()+"笔");
        holder.tv_success_number.setText("分期业务成功数量："+list.get(position).getSuccess_number()+"笔");
        holder.tv_success_money.setText("分期业务成功金额："+list.get(position).getSuccess_money()+"万元");
        holder.tv_sum_credit.setText("积分数："+list.get(position).getSum_credit());
        holder.tv_exchange_credit.setText("已兑换积分："+list.get(position).getExchange_credit());

        String worker_status = list.get(position).getStatus();
        switch (worker_status){
            case "已加盟":
                holder.tv_worker_status.setVisibility(View.GONE);
                break;
            case "正在申请解约":
                holder.tv_worker_status.setVisibility(View.VISIBLE);
                holder.tv_worker_status.setText(worker_status);
                break;
            case "已解约":
                holder.tv_worker_status.setVisibility(View.VISIBLE);
                holder.tv_worker_status.setText(worker_status);
                break;
        }

        return convertView;
    }

    private static class ViewHolder {

        private TextView tv_worker_name;
        private TextView tv_worker_status;
        private TextView tv_worker_address;
        private TextView tv_recommend_number;
        private TextView tv_success_number;
        private TextView tv_success_money;
        private TextView tv_sum_credit;
        private TextView tv_exchange_credit;
    }

}
