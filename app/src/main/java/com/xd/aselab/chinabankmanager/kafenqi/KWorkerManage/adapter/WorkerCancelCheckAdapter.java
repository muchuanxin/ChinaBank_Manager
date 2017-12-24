package com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.model.WorkerVO;

import java.util.List;

/**
 * Created by Administrator on 2017/10/5.
 */

public class WorkerCancelCheckAdapter extends BaseAdapter {

    private List<WorkerVO> list;
    private Context mContext;
    private LayoutInflater mInflater;

    public WorkerCancelCheckAdapter(List<WorkerVO> list, Context mContext) {
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
            convertView = mInflater.inflate(R.layout.worker_cancel_check_list_item, null);
            holder.tv_worker_name = (TextView)convertView.findViewById(R.id.worker_cancel_name);
            holder.tv_worker_address = (TextView)convertView.findViewById(R.id.worker_cancel_address);
            holder.tv_worker_tel = (TextView)convertView.findViewById(R.id.worker_cancel_tel);
            holder.tv_worker_cancel_reason = (TextView)convertView.findViewById(R.id.worker_cancel_reason);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }

        holder.tv_worker_name.setText(list.get(position).getName());
        holder.tv_worker_address.setText("工作单位："+list.get(position).getWorker_address());
        holder.tv_worker_tel.setText("联系电话："+list.get(position).getWorker_tel());
        holder.tv_worker_cancel_reason.setText("解约理由："+list.get(position).getCancel_reason());
        return convertView;
    }

    private static class ViewHolder {
        private TextView tv_worker_name;
        private TextView tv_worker_tel;
        private TextView tv_worker_address;
        private TextView tv_worker_cancel_reason;
    }

}
