package com.magicpixellabs.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhoneNumber implements Bean {

    private String number;
    private String sid;

    public PhoneNumber() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    @Override
    public String toJSON() {
        return null;
    }
}
