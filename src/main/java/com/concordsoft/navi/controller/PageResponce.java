package com.concordsoft.navi.controller;

public class PageResponce {

    public Object children;
    public int total;

    PageResponce(Object body, int count) {
        children = body;
        total = count;
    }
}
