package com.xd.aselab.chinabankmanager.kafenqi.ProvincialBankManager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;

import java.text.DecimalFormat;
import java.util.List;

public class LobbyManagerPerformanceListAdapter extends BaseAdapter {
    private List<LobbyManagerVO> list;
    private Context context;
    private LayoutInflater inflater;

    public LobbyManagerPerformanceListAdapter(List<LobbyManagerVO> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public List<LobbyManagerVO> getList() {
        return list;
    }
    public void setList(List<LobbyManagerVO> list) { this.list = list;}

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
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_view_lobby_erji_performance, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.erji_name);
            holder.tv_recommend_num = (TextView) convertView.findViewById(R.id.erji_recommend_number_text);
            holder.tv_success_num = (TextView) convertView.findViewById(R.id.erji_success_number_text);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DecimalFormat df = new DecimalFormat("#0.0000");
        holder.tv_name.setText(list.get(position).getName());
        holder.tv_recommend_num.setText(list.get(position).getRecommend_num() + "笔");
        holder.tv_success_num.setText(list.get(position).getSuccess_num() + "笔");

        return convertView;
    }

    private static  class ViewHolder {
        private TextView tv_name;
        private TextView tv_recommend_num;
        private TextView tv_success_num;
    }
}
