package com.flappygo.jsontodart.JsonToDart;


//用于生成Dart Class的对象参数
public class DartObjectValue {
    //名称
    private String valueName;

    //如果是TYPE_MODEL或者TYPE_MODEL_LIST时有值,代表value的类的类型
    private DartObject typeClass;

    //类型
    private DartObjectsValueType type;

    //构造器
    public DartObjectValue(String valueName, DartObject typeClass, DartObjectsValueType type) {
        this.valueName = valueName;
        this.typeClass = typeClass;
        this.type = type;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public DartObject getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(DartObject typeClass) {
        this.typeClass = typeClass;
    }

    public DartObjectsValueType getType() {
        return type;
    }

    public void setType(DartObjectsValueType type) {
        this.type = type;
    }

}