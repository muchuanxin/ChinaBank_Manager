package com.xd.aselab.chinabankmanager.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.xd.aselab.chinabankmanager.R;

import java.util.List;

/**
 * Created by wenqr on 2017/11/30.
 */

public class ChooseTimeAdapter extends BaseAdapter {

    private List<String> list;
    private Context mContext;
    private LayoutInflater mInflater;
    private boolean isSelected = false;
    private int position=0;

    public void setList(List<String> list) {
        this.list = list;
    }

    public ChooseTimeAdapter(List<String> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
        mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            convertView = mInflater.inflate(R.layout.choose_time_list_item, null);
            holder.item = (TextView)convertView.findViewById(R.id.sort_item);
            holder.choosedIv = (ImageView)convertView.findViewById(R.id.iv_choose);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }

        holder.item.setText(list.get(position));

        if(this.position == position){
            holder.choosedIv.setVisibility(View.VISIBLE);
            holder.choosedIv.setBackgroundResource(R.drawable.item_unchoose);
        }else{
            holder.choosedIv.setVisibility(View.GONE);
          //  holder.choosedIv.setBackgroundResource(R.drawable.item_unchoose);
        }
        return convertView;
    }
    private static class ViewHolder {
        private TextView item;
        private ImageView choosedIv;
    }

    public void setPosition(int position){
        this.position = position;
        notifyDataSetChanged();
    }


}
