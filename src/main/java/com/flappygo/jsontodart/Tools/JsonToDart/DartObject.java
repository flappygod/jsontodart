package com.flappygo.jsontodart.Tools.JsonToDart;

import java.util.ArrayList;
import java.util.List;

//用于生成Dart Class的对象
public class DartObject {
    //名称
    private String name;

    //对象
    private List<DartObjectValue> values = new ArrayList<>();

    //构造
    public DartObject(String name, List<DartObjectValue> values) {
        this.name = name;
        this.values = values;
    }

    //获取名称
    public String getName() {
        return name;
    }

    //设置名称
    public void setName(String name) {
        this.name = name;
    }

    //获取values
    public List<DartObjectValue> getValues() {
        return values;
    }

    //设置Values
    public void setValues(List<DartObjectValue> values) {
        this.values = values;
    }
}
