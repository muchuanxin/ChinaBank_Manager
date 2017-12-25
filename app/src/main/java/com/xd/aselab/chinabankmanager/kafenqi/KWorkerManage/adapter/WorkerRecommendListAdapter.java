package com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.model.ApplicationsVO;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Administrator on 2017/10/4.
 */

public class WorkerRecommendListAdapter extends BaseAdapter {

    private List<ApplicationsVO> list;
    private Context mContext;
    private LayoutInflater mInflater;
    private ListBtnListener mListener;

    public WorkerRecommendListAdapter(List<ApplicationsVO> list, Context context, ListBtnListener mListener) {
        this.list = list;
        this.mContext = context;
        this.mListener=mListener;
        mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
    }

    public List<ApplicationsVO> getList() {
        return list;
    }

    public void setList(List<ApplicationsVO> list) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.recommend_list_item, null);
            holder.tv_application_name = (TextView)convertView.findViewById(R.id.applicatinName);
            holder.tv_applicate_time = (TextView)convertView.findViewById(R.id.applicateTime);
            holder.rl_tel = (RelativeLayout)convertView.findViewById(R.id.call_application);
            holder.tel = (ImageView)convertView.findViewById(R.id.act_tel);
            holder.tv_applicate_money = (TextView)convertView.findViewById(R.id.fenqi_money);
            holder.tv_application_fenqi_num = (TextView)convertView.findViewById(R.id.fenqi_num);
            holder.tv_application_comm = (TextView)convertView.findViewById(R.id.buy_commodity);
            holder.tv_application_tel = (TextView)convertView.findViewById(R.id.application_tel);
            holder.tv_score = (TextView)convertView.findViewById(R.id.score);
            holder.bt_confirm = (Button) convertView.findViewById(R.id.bt_list_confirm);
            holder.bt_refuse = (Button) convertView.findViewById(R.id.bt_list_refuse);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }

        holder.tv_application_name.setText(list.get(position).getApplicatinName());
        holder.tv_applicate_time.setText(""+list.get(position).getApplicateTime());
        holder.tv_application_tel.setText("联系电话："+list.get(position).getTel());
        holder.tv_applicate_money.setText("分期金额："+list.get(position).getFenqi_money()+"元");
        holder.tv_application_fenqi_num.setText("分期数："+list.get(position).getFenqi_num()+"笔");
        holder.tv_application_comm.setText("购买商品："+list.get(position).getBuy_commodity());
        holder.tv_score.setText(""+list.get(position).getScore());

        String status = list.get(position).getState();
        switch (status){
            case "SUCCESS":
                holder.bt_confirm.setText("业务成功");
                holder.bt_confirm.setBackgroundResource(R.drawable.grey_corner);
                holder.bt_confirm.setVisibility(View.VISIBLE);
                holder.bt_refuse.setVisibility(View.GONE);
                holder.bt_confirm.setClickable(false);
                holder.bt_refuse.setClickable(false);
                break;
            case "YES":
                holder.bt_confirm.setText("已确认");
                holder.bt_confirm.setBackgroundResource(R.drawable.grey_corner);
                holder.bt_confirm.setVisibility(View.VISIBLE);
                holder.bt_refuse.setVisibility(View.GONE);
                holder.bt_confirm.setClickable(false);
                holder.bt_refuse.setClickable(false);
                break;
            case "NO":
                holder.bt_confirm.setText("已拒绝");
                holder.bt_confirm.setBackgroundResource(R.drawable.grey_corner);
                holder.bt_confirm.setVisibility(View.VISIBLE);
                holder.bt_refuse.setVisibility(View.GONE);
                holder.bt_confirm.setClickable(false);
                holder.bt_refuse.setClickable(false);
                break;
            case "CHECK":
                holder.bt_refuse.setVisibility(View.VISIBLE);
                holder.bt_confirm.setVisibility(View.VISIBLE);
                holder.bt_confirm.setText("确认");
                holder.bt_refuse.setText("拒绝");
                holder.bt_confirm.setBackgroundResource(R.drawable.red_corner_light);
                holder.bt_refuse.setBackgroundResource(R.drawable.blue_corner);
                holder.bt_confirm.setClickable(true);
                holder.bt_refuse.setClickable(true);
                holder.bt_confirm.setOnClickListener(mListener);
                holder.bt_refuse.setOnClickListener(mListener);
                break;
        }

        holder.rl_tel.setOnClickListener(mListener);
        holder.tel.setOnClickListener(mListener);
        holder.bt_confirm.setTag(position);
        holder.bt_refuse.setTag(position);
        holder.rl_tel.setTag(position);
        holder.tel.setTag(position);
        return convertView;
    }

    private static class ViewHolder {

        private TextView tv_application_name;
        private TextView tv_applicate_time;
        private RelativeLayout rl_tel;
        private ImageView tel;
        private TextView tv_applicate_money;
        private TextView tv_application_fenqi_num;
        private TextView tv_application_comm;
        private TextView tv_application_tel;
        private TextView tv_score;
        private Button bt_confirm;
        private Button bt_refuse;
    }

    public static abstract class ListBtnListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            myOnClick((Integer) v.getTag(), v);
        }

        public abstract void myOnClick(int position, View v);

    }

}