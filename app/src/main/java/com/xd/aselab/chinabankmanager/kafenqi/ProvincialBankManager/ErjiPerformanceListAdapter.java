package com.xd.aselab.chinabankmanager.kafenqi.ProvincialBankManager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.activity.Manager.LobbyPerformanceListAdapter;
import com.xd.aselab.chinabankmanager.activity.Manager.model.LobbyVO;

import java.text.DecimalFormat;
import java.util.List;

public class ErjiPerformanceListAdapter extends BaseAdapter {
    private List<ErjiVO> list;
    private Context context;
    private LayoutInflater inflater;

    public ErjiPerformanceListAdapter(List<ErjiVO> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public List<ErjiVO> getList() {
        return list;
    }
    public void setList(List<ErjiVO> list) { this.list = list;}

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
            convertView = inflater.inflate(R.layout.list_view_erji_performance, null);
            holder.tv_secondary_name = (TextView) convertView.findViewById(R.id.erji_name);
            holder.tv_number = (TextView) convertView.findViewById(R.id.erji_success_number_text);
            holder.tv_money = (TextView) convertView.findViewById(R.id.erji_success_money_text);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DecimalFormat df = new DecimalFormat("#0.0000");
        holder.tv_secondary_name.setText(list.get(position).getSecondary_name() + "笔");
        holder.tv_number.setText(list.get(position).getNumber() + "笔");
        holder.tv_money.setText(df.format(list.get(position).getMoney()));

        return convertView;
    }

    private static  class ViewHolder {
        private TextView tv_secondary_name;
        private TextView tv_number;
        private TextView tv_money;
    }
}
