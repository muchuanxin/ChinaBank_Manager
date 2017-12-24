package com.xd.aselab.chinabankmanager.activity;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;

public class PerfRankTransparent extends AppCompatActivity {

    private TextView card_total;
    private TextView half_year;
    private TextView three_month;
    private TextView shop_total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perf_rank_transparent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (getActionBar()!=null){
            getActionBar().hide();
        }

        card_total = (TextView) findViewById(R.id.act_perf_rank_trans_card_total);
        half_year = (TextView) findViewById(R.id.act_perf_rank_trans_half_year);
        three_month = (TextView) findViewById(R.id.act_perf_rank_trans_three_month);
        shop_total = (TextView) findViewById(R.id.act_perf_rank_trans_shop_total);

        View.OnClickListener myOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                switch (v.getId()){
                    case R.id.act_perf_rank_trans_card_total :
                        intent.putExtra("time", "card_total");
                        break;
                    case R.id.act_perf_rank_trans_half_year :
                        intent.putExtra("time", "half_year");
                        break;
                    case R.id.act_perf_rank_trans_three_month :
                        intent.putExtra("time", "three_month");
                        break;
                    case R.id.act_perf_rank_trans_shop_total :
                        intent.putExtra("time", "shop_total");
                        break;
                }
                setResult(132,intent);
                finish();
            }
        };

        card_total.setOnClickListener(myOnClickListener);
        half_year.setOnClickListener(myOnClickListener);
        three_month.setOnClickListener(myOnClickListener);
        shop_total.setOnClickListener(myOnClickListener);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }
}
