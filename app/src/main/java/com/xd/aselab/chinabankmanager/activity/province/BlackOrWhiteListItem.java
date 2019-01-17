package com.xd.aselab.chinabankmanager.activity.province;

public class BlackOrWhiteListItem {
    private String name = "";
    private String account = "";
    private String telephone = "";
    private String bankName = "";

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAccount(String account) { this.account = account; }

    public String getAccount() { return account;}

    public void setTelephone(String tel) {
        this.telephone = tel;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setBankName(String BankName) { this.bankName = BankName;}

    public String getBankName() { return bankName;}
}
