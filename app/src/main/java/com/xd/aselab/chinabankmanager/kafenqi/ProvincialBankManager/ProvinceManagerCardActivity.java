package com.xd.aselab.chinabankmanager.kafenqi.ProvincialBankManager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xd.aselab.chinabankmanager.R;

public class ProvinceManagerCardActivity extends AppCompatActivity {

    private RelativeLayout back;
    private LinearLayout erji_performance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province_manager_card);
        back = (RelativeLayout) findViewById(R.id.act_province_manager_card_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        erji_performance = (LinearLayout) findViewById(R.id.act_province_manager_card_erji_perf);
        erji_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ProvinceManagerCardActivity.this, ProvinceManagerCardDivErjiPerformance.class);
                startActivity(intent);
            }
        });
    }
}
