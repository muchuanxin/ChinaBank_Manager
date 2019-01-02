package com.xd.aselab.chinabankmanager.activity.Manager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.activity.Manager.model.SimpleRankAbilityVO;

import java.util.List;

public class GrabRankNullAdapter extends BaseAdapter {

    private Context context = null;
    private List<SimpleRankAbilityVO> list = null;
    private int startNo;

    public GrabRankNullAdapter(Context context, List<SimpleRankAbilityVO> list, int start) {
        this.context = context;
        this.list = list;
        startNo = start;
    }

    public int getCount() {
        return list.size();
    }

    public void setList(List<SimpleRankAbilityVO> l) {
        list = l;
    }

    public SimpleRankAbilityVO getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_view_perf_ranking, null);
            viewHolder = new ViewHolder();
            viewHolder.number = (TextView) convertView.findViewById(R.id.list_view_perf_ranking_number);
            viewHolder.name = (TextView) convertView.findViewById(R.id.list_view_perf_ranking_name);
            viewHolder.count = (TextView) convertView.findViewById(R.id.list_view_perf_ranking_count);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.number.setText(position + startNo + "");
        viewHolder.number.setTextColor(Color.BLACK);
        viewHolder.number.setBackground(null);
        viewHolder.name.setText(list.get(position).getName());
        viewHolder.count.setText(list.get(position).getValue() + "");
        return convertView;
    }

    private class ViewHolder {
        public TextView number;
        public TextView name;
        public TextView count;
    }
}
