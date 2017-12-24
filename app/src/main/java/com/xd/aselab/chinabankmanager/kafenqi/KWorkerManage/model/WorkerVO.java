package com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.model;

/**
 * Created by Administrator on 2017/10/4.
 */

public class WorkerVO {

    private String account;
    private String name;
    private String worker_address;
    private String worker_tel;
    private int recommend_number;
    private int success_number;
    private double success_money;
    private int sum_credit;
    private int exchange_credit;
    private String cancel_reason;
    private double success_rate;
    private String status;
    private String manager_account;
    private String manager_name;
    private String head_image;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorker_address() {
        return worker_address;
    }

    public void setWorker_address(String worker_address) {
        this.worker_address = worker_address;
    }

    public String getWorker_tel() {
        return worker_tel;
    }

    public void setWorker_tel(String worker_tel) {
        this.worker_tel = worker_tel;
    }

    public int getRecommend_number() {
        return recommend_number;
    }

    public void setRecommend_number(int recommend_number) {
        this.recommend_number = recommend_number;
    }

    public int getSuccess_number() {
        return success_number;
    }

    public void setSuccess_number(int success_number) {
        this.success_number = success_number;
    }

    public double getSuccess_money() {
        return success_money;
    }

    public void setSuccess_money(double success_money) {
        this.success_money = success_money;
    }

    public int getSum_credit() {
        return sum_credit;
    }

    public void setSum_credit(int sum_credit) {
        this.sum_credit = sum_credit;
    }

    public int getExchange_credit() {
        return exchange_credit;
    }

    public void setExchange_credit(int exchange_credit) {
        this.exchange_credit = exchange_credit;
    }

    public String getCancel_reason() {
        return cancel_reason;
    }

    public void setCancel_reason(String cancel_reason) {
        this.cancel_reason = cancel_reason;
    }

    public double getSuccess_rate() {
        return success_rate;
    }

    public void setSuccess_rate(double success_rate) {
        this.success_rate = success_rate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getManager_account() {
        return manager_account;
    }

    public void setManager_account(String manager_account) {
        this.manager_account = manager_account;
    }

    public String getManager_name() {
        return manager_name;
    }

    public void setManager_name(String manager_name) {
        this.manager_name = manager_name;
    }

    public String getHead_image() {
        return head_image;
    }

    public void setHead_image(String head_image) {
        this.head_image = head_image;
    }
}
