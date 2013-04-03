package com.magicpixellabs.beans;

public class Device {

    private String platform;
    private String pingId;

    public Device() {
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPingId() {
        return pingId;
    }

    public void setPingId(String pingId) {
        this.pingId = pingId;
    }
}
