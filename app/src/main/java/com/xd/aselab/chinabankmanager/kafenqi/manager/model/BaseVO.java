package com.xd.aselab.chinabankmanager.kafenqi.manager.model;

/**
 * Created by Administrator on 2017/10/15.
 */

public class BaseVO {

    private String base_account;
    private String base_name;
    private String base_tel;
    private int base_fenqi_num;
    private double base_fenqi_money;
    private String flag;

    public String getBase_account() {
        return base_account;
    }

    public void setBase_account(String base_account) {
        this.base_account = base_account;
    }

    public String getBase_name() {
        return base_name;
    }

    public void setBase_name(String base_name) {
        this.base_name = base_name;
    }

    public String getBase_tel() {
        return base_tel;
    }

    public void setBase_tel(String base_tel) {
        this.base_tel = base_tel;
    }

    public int getBase_fenqi_num() {
        return base_fenqi_num;
    }

    public void setBase_fenqi_num(int base_fenqi_num) {
        this.base_fenqi_num = base_fenqi_num;
    }

    public double getBase_fenqi_money() {
        return base_fenqi_money;
    }

    public void setBase_fenqi_money(double base_fenqi_money) {
        this.base_fenqi_money = base_fenqi_money;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
