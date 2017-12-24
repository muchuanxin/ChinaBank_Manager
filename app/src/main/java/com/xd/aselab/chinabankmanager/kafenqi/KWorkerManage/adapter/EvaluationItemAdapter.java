package com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.model.EvaluationItemVO;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Administrator on 2017/11/3.
 */

public class EvaluationItemAdapter extends BaseAdapter {
    private List<EvaluationItemVO> list;
    private Context mContext;
    private LayoutInflater mInflater;

    public EvaluationItemAdapter(List<EvaluationItemVO> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        if(convertView == null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.evaluation_item, null);
            holder.tv_title = (TextView)convertView.findViewById(R.id.tv_evaluation_title);
            holder.tv_content = (TextView)convertView.findViewById(R.id.tv_evaluation_content);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }
        holder.tv_title.setText(list.get(position).getTitle());
        holder.tv_content.setText(":  "+list.get(position).getContent());
        return convertView;
    }

    private class ViewHolder{
        private TextView tv_title;
        private TextView tv_content;

    }

}
