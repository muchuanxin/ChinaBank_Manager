package com.xd.aselab.chinabankmanager.activity.Manager;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.activity.Manager.model.LobbyVO;

import java.text.DecimalFormat;
import java.util.List;

public class LobbyPerformanceListAdapter extends BaseAdapter {

    private List<LobbyVO> list;
    private Context context;
    private LayoutInflater inflater;

    public LobbyPerformanceListAdapter(List<LobbyVO> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public List<LobbyVO> getList() {
        return list;
    }
    public void setList(List<LobbyVO> list) { this.list = list;}

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
            convertView = inflater.inflate(R.layout.list_view_lobby_performance, null);
            holder.tv_lobby_name = (TextView) convertView.findViewById(R.id.lobby_name_text);
            holder.tv_recommend_num = (TextView) convertView.findViewById(R.id.recommend_card_number_text);
            holder.tv_success_num = (TextView) convertView.findViewById(R.id.success_card_number_text);
            holder.tv_lobby_tel = (TextView) convertView.findViewById(R.id.lobby_tel_text);
            holder.tv_siji_name = (TextView) convertView.findViewById(R.id.lobby_address_text);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DecimalFormat df = new DecimalFormat("#0.0000");
        holder.tv_lobby_name.setText(list.get(position).getLobby_name());
        holder.tv_recommend_num.setText(list.get(position).getLobby_recommend_num() + "笔");
        holder.tv_success_num.setText(list.get(position).getLobby_success_num() + "笔");
        holder.tv_lobby_tel.setText(list.get(position).getLobby_tel());
        holder.tv_siji_name.setText(list.get(position).getLobby_siji_name());

        return convertView;
    }

    private static  class ViewHolder {
        private TextView tv_lobby_name;
        private TextView tv_recommend_num;
        private TextView tv_success_num;
        private TextView tv_lobby_tel;
        private TextView tv_siji_name;
    }
}
