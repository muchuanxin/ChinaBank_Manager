package com.xd.aselab.chinabankmanager.kafenqi.KPublishAnnouncement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by xinye on 2017/7/20.
 */

public class PromoterAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String, String>> list;
    private ArrayList<String> checkBoxIDList=new ArrayList<String>();            //存储checkBox的值
    private Map<Integer, Boolean>  check_map= new HashMap<>();

    public List<Map<String, String>> getList() {
        return list;
    }

    public void setList(List<Map<String, String>> list) {
        this.list = list;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public PromoterAdapter(Context context, List<Map<String, String>> list) {
        this.context = context;
        this.list = list;
    }

    public ArrayList<String> getCheckBoxIDList() {
        return checkBoxIDList;
    }

    public void setCheckBoxIDList(ArrayList<String> checkBoxIDList) {
        this.checkBoxIDList = checkBoxIDList;
    }

    @Override
    public int getCount() {

        return  list.size();
    }

    @Override
    public Map<String, String> getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if(convertView==null){

            convertView = LayoutInflater.from(context).inflate(R.layout.list_view_promoter_info, null);
            holder = new ViewHolder();
            holder.account = (TextView) convertView.findViewById(R.id.account);
            holder.name = (TextView) convertView.findViewById(R.id.order_promoter_name);
            holder.telephone = (TextView) convertView.findViewById(R.id.promoter_tele);
            holder.place = (TextView) convertView.findViewById(R.id.promoter_work_place);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkBox2);

            convertView.setTag(holder);
        }else{

            holder=(ViewHolder)convertView.getTag();
        }

        final Map<String, String> map = list.get(position);
        holder.account.setText(map.get("account"));
        holder.name.setText(map.get("name"));
        holder.telephone.setText("联系电话："+map.get("telephone"));
        holder.place.setText("工作单位："+map.get("place"));

        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                 //  Log.e("123", "选择点击一次！");
                   // Log.e("123", "position="+position);
                    //Log.e("123", holder.account.getText().toString());
                    if(!checkBoxIDList.contains(holder.account.getText().toString())) {
                        checkBoxIDList.add(holder.account.getText().toString());
                    }
                        check_map.put(position,true);
                    //Log.e("123", "checkBoxIDList="+checkBoxIDList.toString());
                    //Log.e("123", "check_map="+check_map.toString());
                }else
                {
                    //Log.e("123", "else");
                    //Log.e("123", "position="+position);
                    //Log.e("123", holder.account.getText().toString());
                    if(checkBoxIDList.contains(holder.account.getText().toString())) {
                        checkBoxIDList.remove(holder.account.getText().toString());
                    }

                    check_map.remove(position);
                    //Log.e("123", "checkBoxIDList="+checkBoxIDList.toString());
                    //Log.e("123", "check_map="+check_map.toString());
                }
            }
        });

        if(check_map!=null&&check_map.containsKey(position)){
            holder.checkbox.setChecked(true);
        }else {
            holder.checkbox.setChecked(false);
        }
        return convertView;
    }

    private class ViewHolder{
        TextView account;
        TextView name;
        TextView telephone;
        TextView place;
        CheckBox checkbox;

    }
}
