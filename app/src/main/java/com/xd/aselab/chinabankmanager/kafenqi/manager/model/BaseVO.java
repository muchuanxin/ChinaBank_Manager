package com.xd.aselab.chinabankmanager.kafenqi.manager.model;

/**
 * Created by Administrator on 2017/10/15.
 */

public class BaseVO {

    private String base_account;
    private String base_name;
    private String base_tel;
    private double base_fenqi_money;
    private int base_fenqi_confirm_num;//确认业务
    private int base_fenqi_remark_num;//备注业务
    private int base_fenqi_refuse_num;//拒绝业务数
    private int base_fenqi_num;//成功业务数
    private String flag;
    private int base_fenqi_recommend_num;//推荐业务

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
    public int getbase_fenqi_remark_num() {
        return base_fenqi_remark_num;
    }
    public void setbase_fenqi_remark_num(int base_fenqi_remark_num) {
        this.base_fenqi_remark_num = base_fenqi_remark_num;
    }
    public int getbase_fenqi_confirm_num() {
        return base_fenqi_confirm_num;
    }
    public void setbase_fenqi_confirm_num(int base_fenqi_confirm_num) {
        this.base_fenqi_confirm_num = base_fenqi_confirm_num;
    }
    public int getbase_fenqi_refuse_num() {
        return base_fenqi_refuse_num;
    }
    public void setbase_fenqi_refuse_num(int base_fenqi_refuse_num) {
        this.base_fenqi_refuse_num = base_fenqi_refuse_num;
    }
    public int getBase_fenqi_recommend_num() {
        return base_fenqi_recommend_num;
    }
    public void setbase_fenqi_recommend_num(int base_fenqi_num) {
        this.base_fenqi_recommend_num = base_fenqi_recommend_num;
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
