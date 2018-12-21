package com.xd.aselab.chinabankmanager.kafenqi;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;

import java.text.DecimalFormat;
import java.util.List;

public class KafenqiNonCarPerfomanceDetailAdapter extends BaseAdapter {

    private List<NonCarVO> list;
    private Context context;
    private LayoutInflater inflater;
    private ListBtnListener mListener;
    private String[] productTypes = {"车位分期", "家装分期", "旅游分期"};

    public KafenqiNonCarPerfomanceDetailAdapter(List<NonCarVO> list, Context context, ListBtnListener mListener) {
        this.list = list;
        this.context = context;
        this.mListener = mListener;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public List<NonCarVO> getList() {
        return list;
    }
    public void setList(List<NonCarVO> list) { this.list = list;}

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
        ViewHolder viewHolder;
        if(convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.kafenqi_performance_detail_adapter, null);
            viewHolder.tv_applicant_name = (TextView) convertView.findViewById(R.id.kafenqi_perf_name_text);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.kafenqi_perf_time);
            viewHolder.tv_tel = (TextView) convertView.findViewById(R.id.kafenqi_perf_tel);
            viewHolder.tv_total_money = (TextView) convertView.findViewById(R.id.kafenqi_perf_total_money);
            viewHolder.tv_month = (TextView) convertView.findViewById(R.id.kafenqi_perf_month);
            viewHolder.tv_serial_num = (TextView) convertView.findViewById(R.id.kafenqi_perf_serial_num);
            viewHolder.tv_loan_money = (TextView) convertView.findViewById(R.id.kafenqi_perf_loan_ammouont);
            viewHolder.tv_product_type = (TextView) convertView.findViewById(R.id.kafenqi_perf_product_type);
            viewHolder.beizhu = (ImageView) convertView.findViewById(R.id.kafenqi_perf_beizhu);
            viewHolder.call = (ImageView) convertView.findViewById(R.id.kafenqi_perf_call);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DecimalFormat df = new DecimalFormat("#0.0000");
        viewHolder.tv_applicant_name.setText(list.get(position).getApplicant_name());
        viewHolder.tv_time.setText(list.get(position).getTime() + "");
        viewHolder.tv_total_money.setText("分期总金额：" + df.format(list.get(position).getTotal_money()) + "万");
        viewHolder.tv_tel.setText("联系电话：" + list.get(position).getTel());
        viewHolder.tv_month.setText("分期数：" + list.get(position).getInstallment_num() + "月");
        if(list.get(position).getProduct_type().equals("parking")) {
            viewHolder.tv_product_type.setText("产品类型：" + productTypes[0]);
        }
        else if(list.get(position).getProduct_type().equals("decoration")) {
            viewHolder.tv_product_type.setText("产品类型：" + productTypes[1]);
        }
        else if(list.get(position).getProduct_type().equals("tour")) {
            viewHolder.tv_product_type.setText("产品类型：" + productTypes[2]);
        }

        String status = list.get(position).getSerial_num();
        if(!"".equals(status)) {

            viewHolder.beizhu.setVisibility(View.GONE);

            viewHolder.tv_serial_num.setVisibility(View.VISIBLE);
            viewHolder.tv_loan_money.setVisibility(View.VISIBLE);
            viewHolder.tv_serial_num.setText("流水号：" + list.get(position).getSerial_num());
            viewHolder.tv_loan_money.setText("放款金额：" + df.format(list.get(position).getLoan_money()));
        } else {
            viewHolder.beizhu.setVisibility(View.VISIBLE);

            viewHolder.tv_serial_num.setVisibility(View.GONE);
            viewHolder.tv_loan_money.setVisibility(View.GONE);
        }

        viewHolder.beizhu.setOnClickListener(mListener);
        viewHolder.call.setOnClickListener(mListener);

        viewHolder.tv_applicant_name.setTag(position);
        viewHolder.tv_time.setTag(position);
        viewHolder.tv_tel.setTag(position);
        viewHolder.tv_total_money.setTag(position);
        viewHolder.tv_month.setTag(position);
        viewHolder.tv_product_type.setTag(position);
        viewHolder.tv_serial_num.setTag(position);
        viewHolder.tv_loan_money.setTag(position);
        viewHolder.beizhu.setTag(position);
        viewHolder.call.setTag(position);

        return convertView;
    }

    private static class ViewHolder {
        private TextView tv_applicant_name;
        private TextView tv_time;
        private TextView tv_total_money;
        private TextView tv_tel;
        private TextView tv_month;
        private TextView tv_serial_num;
        private TextView tv_loan_money;
        private TextView tv_product_type;
        private ImageView beizhu;
        private ImageView call;
    }

    public static abstract class ListBtnListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            myOnClick((Integer) v.getTag(), v);
    }

        public abstract void myOnClick(int position, View v);

    }
}
