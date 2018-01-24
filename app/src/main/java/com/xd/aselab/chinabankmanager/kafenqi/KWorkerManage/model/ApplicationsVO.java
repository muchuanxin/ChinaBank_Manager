package com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.model;

/**
 * Created by Administrator on 2017/10/4.
 */

public class ApplicationsVO {
    private String applicationID; //卡分期的业务编号
    private String applicatinName;
    private String applicateTime;
    private double fenqi_money;
    private int fenqi_num;
    private String buy_commodity;
    private int score;
    private String state;
    private String tel;
    private String serial_num;


    public String getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public String getApplicatinName() {
        return applicatinName;
    }

    public void setApplicatinName(String applicatinName) {
        this.applicatinName = applicatinName;
    }

    public String getApplicateTime() {
        return applicateTime;
    }

    public void setApplicateTime(String applicateTime) {
        this.applicateTime = applicateTime;
    }

    public double getFenqi_money() {
        return fenqi_money;
    }

    public void setFenqi_money(double fenqi_money) {
        this.fenqi_money = fenqi_money;
    }

    public int getFenqi_num() {
        return fenqi_num;
    }

    public void setFenqi_num(int fenqi_num) {
        this.fenqi_num = fenqi_num;
    }

    public String getBuy_commodity() {
        return buy_commodity;
    }

    public void setBuy_commodity(String buy_commodity) {
        this.buy_commodity = buy_commodity;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setserial_num(String serial_num) {
        this.serial_num = serial_num;
    }

    public String getSerial_num() {
        return serial_num;
    }



}
