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

public class GrabRankAdapter extends BaseAdapter {

    private Context context = null;
    private List<SimpleRankAbilityVO> list = null;

    public GrabRankAdapter(Context context, List<SimpleRankAbilityVO> list) {
        this.context = context;
        this.list = list;
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
        viewHolder.number.setText(position + 1 + "");
        //Log.e("position",""+position);
        switch (position) {
            case 0:
                //Log.e("case","进入case0");
                viewHolder.number.setTextColor(Color.WHITE);
                viewHolder.number.setBackgroundResource(R.drawable.top1);
                break;
            case 1:
                //Log.e("case","进入case1");
                viewHolder.number.setTextColor(Color.WHITE);
                viewHolder.number.setBackgroundResource(R.drawable.top2);
                break;
            case 2:
                //Log.e("case","进入case2");
                viewHolder.number.setTextColor(Color.WHITE);
                viewHolder.number.setBackgroundResource(R.drawable.top3);
                break;
            default:
                //Log.e("case","进入default");
                viewHolder.number.setTextColor(Color.BLACK);
                viewHolder.number.setBackground(null);
                break;
        }
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
