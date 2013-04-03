package com.magicpixellabs.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class APIResponse<E> {

    private ArrayList<E> data;
    private int error;
    private boolean success;
    private int points;
    private int count;
    private long timestamp;
    private long millis;

    public APIResponse() {
    }

    public ArrayList<E> getData() {
        return data;
    }

    public void setData(ArrayList<E> data) {
        this.data = data;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }
}
