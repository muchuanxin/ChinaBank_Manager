package com.xd.aselab.chinabankmanager.activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.fragment.HistogramView;

public class PerformanceChart extends AppCompatActivity {

    private RelativeLayout back;
    private RelativeLayout chart;

    private HistogramView histogramView;

    private String[] names=new String[]{"1","2","3","4","5","6","7","8","9","10","11","12"};
    private float[] numbers=new float[]{120,2,3,4,5,6,7,8,9,10,11,120};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance_chart);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

//        Intent intent = getIntent();
//        names = intent.getStringArrayExtra("names");
//        numbers = intent.getFloatArrayExtra("numbers");

        chart = (RelativeLayout) findViewById(R.id.act_perf_chart_chart);
        histogramView = new HistogramView(PerformanceChart.this, names, numbers);
        chart.addView(histogramView);//默认长宽都是match_parent，即和父控件等大

        back = (RelativeLayout) findViewById(R.id.act_perf_chart_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
