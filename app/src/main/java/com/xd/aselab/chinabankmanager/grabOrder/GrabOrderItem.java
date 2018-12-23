package com.xd.aselab.chinabankmanager.grabOrder;

public class GrabOrderItem {
    private String id = "";
    private String applicant = "";
    private String time = "";
    private String telephone = "";
    private String product_type = "";
    private String status = "";

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTelephone(String tel) {
        this.telephone = tel;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setType(String product_type) {
        this.product_type = product_type;
    }

    public String getType() {
        return product_type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
