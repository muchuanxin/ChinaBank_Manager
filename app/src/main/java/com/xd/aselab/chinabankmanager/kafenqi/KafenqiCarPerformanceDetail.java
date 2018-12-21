package com.xd.aselab.chinabankmanager.kafenqi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xd.aselab.chinabankmanager.R;

public class KafenqiCarPerformanceDetail extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kafenqi_car_performance_detail);
        initViews();
        initEvents();
    }

    void initViews() {
        listView = (ListView) findViewById(R.id.act_kafenqi_car_perf_detail_listview);
    }

    void initEvents() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
}
