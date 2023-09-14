package com.example.testjava;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class CustomObject implements Serializable {
    //宣告一個繼承 Serializable 的物件

    int id= 1;
    String name = "Tiv";

    @NonNull
    @Override
    public String toString() {
        return "id="+id+", name="+name;
    }
}
