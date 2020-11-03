package com.example.leonardo.watermeter.utils;

import java.util.List;

public class FirstModel {
    private String id;
    private String name;
    private List<SecondModel> child;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SecondModel> getChild() {
        return child;
    }

    public void setChild(List<SecondModel> child) {
        this.child = child;
    }

    @Override
    public String toString() {
        return "FirstModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", child=" + child +
                '}';
    }
}
