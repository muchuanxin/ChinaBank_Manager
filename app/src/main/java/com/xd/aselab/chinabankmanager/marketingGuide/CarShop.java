package com.xd.aselab.chinabankmanager.marketingGuide;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/30.
 */

public class CarShop {
    private String carshopID;
    private String carshopName;
    private String carshopAddress;
    private List<Map<String,String>> worker_list;

    public String getCarshopID() {
        return carshopID;
    }

    public void setCarshopID(String carshopID) {
        this.carshopID = carshopID;
    }

    public String getCarshopName() {
        return carshopName;
    }

    public void setCarshopName(String carshopName) {
        this.carshopName = carshopName;
    }

    public String getCarshopAddress() {
        return carshopAddress;
    }

    public void setCarshopAddress(String carshopAddress) {
        this.carshopAddress = carshopAddress;
    }

    public List<Map<String, String>> getWorker_list() {
        return worker_list;
    }

    public void setWorker_list(List<Map<String, String>> worker_list) {
        this.worker_list = worker_list;
    }
}
