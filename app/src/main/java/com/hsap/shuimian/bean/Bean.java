package com.hsap.shuimian.bean;

/**
 * Created by zhao on 2018/1/30.
 */

public class Bean {
    private String address;
    private String deviceId;
    private String deviceName;
    public Bean(String deviceId,String deviceName,String address){
        this.deviceId=deviceId;
        this.deviceName=deviceName;
        this.address=address;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
