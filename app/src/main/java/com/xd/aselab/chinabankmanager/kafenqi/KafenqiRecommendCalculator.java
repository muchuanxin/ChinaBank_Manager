package com.xd.aselab.chinabankmanager.kafenqi;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.R;

import java.util.HashMap;
import java.util.Map;

public class KafenqiRecommendCalculator extends AppCompatActivity {

    private RelativeLayout back;
    private EditText tv_income;
    private EditText tv_repayment;
    private EditText tv_installment;
    private Spinner choose_marriage;
    private Spinner choose_industry;
    private Spinner choose_position;
    private Spinner choose_paymethod;
    private Spinner choose_risk;

    private String income;
    private String repayment;
    private String installment;
    private String marriage;
    private String industry;
    private String position;
    private String paymethod;
    private String risk;


    private ImageView calculate;

    private Map<String, Integer> marriage_standard;
    private Map<String, Double> industry_double;
    private Map<String, Double> position_double;
    private Map<String, Double> marriage_double;
    private Map<String, Double> paymethod_double;
    private Map<String, Double> risk_double;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kafenqi_recommend_calculator);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        back = (RelativeLayout) findViewById(R.id.act_kafenqi_recommend_calculator_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initViews();
        initEvents();
        getChooseType();
        setMap();

    }

    void initViews() {
        tv_income = (EditText) findViewById(R.id.act_kafenqi_recommend_calculator_income);
        tv_repayment = (EditText) findViewById(R.id.act_kafenqi_recommend_calculator_repayment);
        tv_installment = (EditText) findViewById(R.id.act_kafenqi_recommend_calculator_installment);
        choose_marriage = (Spinner) findViewById(R.id.choose_marriage);
        choose_industry = (Spinner) findViewById(R.id.choose_industry);
        choose_position = (Spinner) findViewById(R.id.choose_position);
        choose_paymethod = (Spinner) findViewById(R.id.choose_paymethod);
        choose_risk = (Spinner) findViewById(R.id.choose_risk);

        calculate = (ImageView) findViewById(R.id.act_kafenqi_recommend_calculator_calculate);

    }

    void initEvents() {

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(tv_income);
                hideKeyboard(tv_repayment);
                hideKeyboard(tv_installment);

                income = tv_income.getText().toString().trim();
                repayment = tv_repayment.getText().toString().trim();
                installment = tv_installment.getText().toString().trim();

                if(income.equals("") || income == null) {
                    Toast.makeText(KafenqiRecommendCalculator.this, "请输入申请人匡算月收入！", Toast.LENGTH_SHORT).show();
                }
                else if(repayment.equals("") || repayment == null) {
                    Toast.makeText(KafenqiRecommendCalculator.this, "请输入个人征信报告中月还款金额！", Toast.LENGTH_SHORT).show();
                }
                else if(installment.equals("") || installment == null) {
                    Toast.makeText(KafenqiRecommendCalculator.this, "请输入申请业务期限！", Toast.LENGTH_SHORT).show();
                }
                else if(marriage.equals("") || marriage == null) {
                    Toast.makeText(KafenqiRecommendCalculator.this, "请选择婚姻状态！", Toast.LENGTH_SHORT).show();
                }
                else if(industry.equals("") || marriage == null) {
                    Toast.makeText(KafenqiRecommendCalculator.this, "请选择行业类型！", Toast.LENGTH_SHORT).show();
                }
                else if(position.equals("") || position == null) {
                    Toast.makeText(KafenqiRecommendCalculator.this, "请选择职位等级！", Toast.LENGTH_SHORT).show();
                }
                else if(paymethod.equals("") || paymethod == null) {
                    Toast.makeText(KafenqiRecommendCalculator.this, "请选择支付方式！", Toast.LENGTH_SHORT).show();
                }
                else if(risk.equals("") || risk == null) {
                    Toast.makeText(KafenqiRecommendCalculator.this, "请选择风险行为！", Toast.LENGTH_SHORT).show();
                }
                else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(KafenqiRecommendCalculator.this);
                    final View dialog_content = LayoutInflater.from(KafenqiRecommendCalculator.this).inflate(R.layout.kafenqi_recommend_calculator_result, null);
                    TextView calculator_result = (TextView) dialog_content.findViewById(R.id.calculator_result);

                    calculator_result.setText("授信额度：" + getResult());

                    builder.setTitle("计算结果");
                    builder.setView(dialog_content);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.show();
                }
            }
        });

    }

    private double getResult() {
        double result;
        double rs;
        double income_value;
        int standard_value;
        double repayment_value;
        int installment_value;
        double adjust_value;
        double paymethod_value;
        double risk_value;
        double industry_value;
        double position_value;
        double marriage_value;

        income_value = Double.valueOf(income);
        repayment_value = Double.valueOf(repayment);
        installment_value = Integer.valueOf(installment);
        standard_value = marriage_standard.get(marriage);
        industry_value = industry_double.get(industry);
        position_value = position_double.get(position);
        marriage_value = marriage_double.get(marriage);
        paymethod_value = paymethod_double.get(paymethod);
        risk_value = risk_double.get(risk);

        adjust_value = industry_value + position_value + marriage_value;

        rs = (income_value - standard_value - repayment_value) * installment_value * (1 + adjust_value) * paymethod_value * risk_value;

        result = ((rs <= 300000) ? rs : 300000);
        return result;
    }

    private void setMap() {
        marriage_standard = new HashMap<>();
        marriage_standard.put("已婚", 900);
        marriage_standard.put("未婚", 1200);
        marriage_standard.put("离异", 1200);

        industry_double = new HashMap<>();
        industry_double.put("政府机关、事业单位、金融、医院", 0.2);
        industry_double.put("学校、电信、店里、烟草", 0.1);
        industry_double.put("其他", -0.1);

        position_double = new HashMap<>();
        position_double.put("科员/普通员工", 0.0);
        position_double.put("科级", 0.1);
        position_double.put("处级", 0.2);
        position_double.put("厅级", 0.3);

        marriage_double = new HashMap<>();
        marriage_double.put("未婚", 0.0);
        marriage_double.put("已婚", 0.0);
        marriage_double.put("离异", -0.2);

        paymethod_double = new HashMap<>();
        paymethod_double.put("银行直接放款", 1.0);
        paymethod_double.put("优客专用借记卡", 0.9);

        risk_double = new HashMap<>();
        risk_double.put("无", 1.0);
        risk_double.put("征信报告空白", 0.5);
        risk_double.put("征信报告借贷信息空白", 0.5);
    }

    void getChooseType() {

        final String[] marriage_types = {"未婚", "已婚", "离异"};
        final String[] industry_types = {"政府机关、事业单位、金融、医院", "学校、电信、店里、烟草", "其他"};
        final String[] position_types = {"科员/普通员工", "科级", "处级", "厅级"};
        final String[] paymethod_types = {"银行直接放款", "优客专用借记卡"};
        final String[] risk_types = {"无", "征信报告空白", "征信报告借贷信息空白"};

        ArrayAdapter<String> marriageAdapter = new ArrayAdapter<>(KafenqiRecommendCalculator.this,
                android.R.layout.simple_spinner_item, marriage_types);

        ArrayAdapter<String> industryAdapter = new ArrayAdapter<>(KafenqiRecommendCalculator.this,
                android.R.layout.simple_spinner_item, industry_types);

        ArrayAdapter<String> positionAdapter = new ArrayAdapter<>(KafenqiRecommendCalculator.this,
                android.R.layout.simple_spinner_item, position_types);

        ArrayAdapter<String> paymethodAdapter = new ArrayAdapter<>(KafenqiRecommendCalculator.this,
                android.R.layout.simple_spinner_item, paymethod_types);

        ArrayAdapter<String> riskAdapter = new ArrayAdapter<>(KafenqiRecommendCalculator.this,
                android.R.layout.simple_spinner_item, risk_types);

        marriageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        industryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        positionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymethodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        riskAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        choose_marriage.setAdapter(marriageAdapter);
        choose_industry.setAdapter(industryAdapter);
        choose_position.setAdapter(positionAdapter);
        choose_paymethod.setAdapter(paymethodAdapter);
        choose_risk.setAdapter(riskAdapter);

        choose_marriage.setSelection(0);
        choose_industry.setSelection(0);
        choose_position.setSelection(0);
        choose_paymethod.setSelection(0);
        choose_risk.setSelection(0);

        choose_marriage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                marriage = marriage_types[pos];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        choose_industry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                industry = industry_types[pos];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        choose_position.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                position = position_types[pos];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        choose_paymethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                paymethod = paymethod_types[pos];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        choose_risk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                risk = risk_types[pos];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //用于返回界面隐藏软键盘
    private void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }
}
