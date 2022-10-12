package com.myrss;

public class RSS {
    private int id;
    private String name;
    private String url;
    private Integer collect;

    public RSS(){}

    public RSS(String name, String url, Integer collect) {
        this.name = name;
        this.url = url;
        this.collect = collect;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }


    public String getURL() {
        return url;
    }


    public Integer getCollect() {
        return collect;
    }

}
