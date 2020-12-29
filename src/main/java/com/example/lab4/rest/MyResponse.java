package com.example.lab4.rest;

import com.example.lab4.models.Point;

import java.util.List;

public class MyResponse {
    public static final String statusOk = "ok";
    public static final String statusFail = "failed";

    public String status;
    public String key;
    public List<Point> data;
    public Point last_point;

    public MyResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<Point> getData() {
        return data;
    }

    public void setData(List<Point> data) {
        this.data = data;
    }

    public Point getLast_point() {
        return last_point;
    }

    public void setLast_point(Point last_point) {
        this.last_point = last_point;
    }
}

