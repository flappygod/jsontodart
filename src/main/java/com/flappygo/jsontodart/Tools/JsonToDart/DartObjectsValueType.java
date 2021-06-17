package com.flappygo.jsontodart.Tools.JsonToDart;

public enum DartObjectsValueType {
    //直接是字符串
    TYPE_BOOL,
    //直接是字符串
    TYPE_STRING,
    //动态类型
    TYPE_DYNAMIC,
    //如果是对象类型
    TYPE_MODEL,
    //如果是对象列表类型
    TYPE_MODEL_LIST,
}
