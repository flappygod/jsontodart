package com.flappygo.jsontodart.JsonToDart;

import org.apache.commons.text.CaseUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


//通过json 生成dart文件
public class DartJsonTool {

    //字符串class
    public static DartObject stringClass = new DartObject("String", new ArrayList<DartObjectValue>());

    //不定型class
    public static DartObject dynamicClass = new DartObject("dynamic", new ArrayList<DartObjectValue>());


    //首字母大写方法
    public static String firstLetterUpercase(String letter) {
        return letter.substring(0, 1).toUpperCase() + letter.substring(1);
    }

    //首字母小写方法
    public static String firstLetterLowercase(String letter) {
        return letter.substring(0, 1).toLowerCase() + letter.substring(1);
    }

    //转换为驼峰
    private static String toCamelCase(String str) {
        return CaseUtils.toCamelCase(str, false, new char[]{'_'});
    }

    //解析这个json
    public static String generateDartToJson(String jsonStr, String className) throws Exception {

        //转换为jsonObject
        JSONObject jsonObject = new JSONObject(jsonStr);

        //取得所有的dartObjects,全部集中到同一层级
        List<DartObject> dartObjects = generateJsonDartObject(jsonObject, className);

        //此时存在重复的，我们需要去重
        List<DartObject> filteredObjects = filterRepeatObjects(dartObjects);

        //转换为
        StringBuffer retBuffer = new StringBuffer();
        for (int s = 0; s < filteredObjects.size(); s++) {
            retBuffer.append(dartClassToString(filteredObjects.get(s)).toString());
        }
        //转换
        return retBuffer.toString();
    }


    //将一个jsonObject装换为一个DartObject列表, 如果是嵌套的，将全部转为同一平面层级
    public static List<DartObject> generateJsonDartObject(JSONObject jsonObject, String className) {
        return generateJsonDartObject(jsonObject, className, new ArrayList<DartObject>());
    }


    //将一个jsonObject装换为一个DartObject列表
    private static List<DartObject> generateJsonDartObject(JSONObject jsonObject, String className, List<DartObject> totalObjects) {

        //创建一个dartObjects
        DartObject dartObjects = new DartObject(className, new ArrayList<DartObjectValue>());

        Iterator<String> keys = jsonObject.keys();

        //遍历当前json的每个节点
        while (keys.hasNext()) {

            String key = keys.next();

            //获取
            Object childObject = jsonObject.get(key);
            //如果是对象类型
            if (childObject instanceof JSONObject) {

                //值的类型的名称
                String valueCalssName = firstLetterUpercase(toCamelCase(key));
                //值的对象
                JSONObject valueObject = (JSONObject) childObject;
                //获取值的对象的解析类型
                List<DartObject> childObjects = generateJsonDartObject(valueObject, valueCalssName, new ArrayList<DartObject>());
                //添加进入
                totalObjects.addAll(childObjects);
                //创建出这个Value
                DartObjectValue value = new DartObjectValue(key, getDartObjectsByName(childObjects, valueCalssName), DartObjectsValueType.TYPE_MODEL);
                //添加成为此类型的value
                dartObjects.getValues().add(value);


            }
            //如果是数组类型
            else if (childObject instanceof JSONArray) {
                //值的类型的名称
                String valueCalssName = firstLetterUpercase(toCamelCase(key));

                //如果是jsonArray
                JSONArray valueObjects = (JSONArray) childObject;

                //如果是存在的
                if (valueObjects.length() > 0) {
                    //获取第一个对象
                    Object valueChildData = valueObjects.get(0);

                    //如果是对象
                    if (valueChildData instanceof JSONObject) {

                        //获取子对象的objects
                        List<DartObject> childObjects = generateJsonDartObject((JSONObject) valueChildData, valueCalssName, new ArrayList<DartObject>());
                        //添加进入总体库
                        totalObjects.addAll(childObjects);
                        //创建出这个Value
                        DartObjectValue value = new DartObjectValue(key, getDartObjectsByName(childObjects, valueCalssName), DartObjectsValueType.TYPE_MODEL_LIST);
                        //添加成为此类型的value
                        dartObjects.getValues().add(value);

                    }

                    //处理成字符串
                    else if (valueChildData instanceof String ||
                            valueChildData instanceof Double ||
                            valueChildData instanceof Integer) {

                        //字符串列表
                        DartObjectValue value = new DartObjectValue(key, stringClass, DartObjectsValueType.TYPE_MODEL_LIST);
                        //添加成为此类型的value
                        dartObjects.getValues().add(value);

                    } else {

                        //不定型列表
                        DartObjectValue value = new DartObjectValue(key, dynamicClass, DartObjectsValueType.TYPE_MODEL_LIST);
                        //添加成为此类型的value
                        dartObjects.getValues().add(value);
                    }

                } else {

                    //不定型列表
                    DartObjectValue value = new DartObjectValue(key, dynamicClass, DartObjectsValueType.TYPE_MODEL_LIST);
                    //添加成为此类型的value
                    dartObjects.getValues().add(value);
                }
            }
            //如果是直接的字符串
            else if (isCanStringObject(childObject)) {
                //不定型对象
                DartObjectValue value = new DartObjectValue(key, stringClass, DartObjectsValueType.TYPE_STRING);
                //添加成为此类型的value
                dartObjects.getValues().add(value);
            }
            //其他全部为dynamic
            else {
                //不定型对象
                DartObjectValue value = new DartObjectValue(key, dynamicClass, DartObjectsValueType.TYPE_DYNAMIC);
                //添加成为此类型的value
                dartObjects.getValues().add(value);
            }
        }

        //转换完成
        totalObjects.add(dartObjects);

        return totalObjects;
    }


    //这些都转换成String处理
    private static boolean isCanStringObject(Object object) {
        return (object instanceof String ||
                object instanceof BigDecimal ||
                object instanceof Integer ||
                object instanceof Long ||
                object instanceof Float ||
                object instanceof Double);
    }


    //获取当前所有的objects中，类名为name的这条数据
    private static DartObject getDartObjectsByName(List<DartObject> objects, String name) {
        for (int s = 0; s < objects.size(); s++) {
            if (objects.get(s).getName().equals(name)) {
                return objects.get(s);
            }
        }
        return null;
    }


    //判断是否已经包含了重复的model,如果重复了，返回集合更大更多的objects
    private static DartObject checkRepeatModel(DartObject objectOne, DartObject objectTwo) {


        //判断对象一是否包含对象二
        boolean oneContainsTwo = true;
        for (int a = 0; a < objectTwo.getValues().size(); a++) {
            boolean containValue = false;
            for (int b = 0; b < objectOne.getValues().size(); b++) {
                if (isValueContain(objectOne.getValues().get(b), objectTwo.getValues().get(a))) {
                    containValue = true;
                }
            }
            if (containValue == false) {
                oneContainsTwo = false;
                break;
            }
        }
        if (oneContainsTwo) {
            return objectOne;
        }

        //判断对象二是否包含对象一
        boolean twoContainsOne = true;
        for (int a = 0; a < objectOne.getValues().size(); a++) {
            boolean containValue = false;
            for (int b = 0; b < objectTwo.getValues().size(); b++) {
                if (isValueContain(objectTwo.getValues().get(b), objectOne.getValues().get(a))) {
                    containValue = true;
                }
            }
            if (containValue == false) {
                twoContainsOne = false;
                break;
            }
        }
        if (twoContainsOne) {
            return objectTwo;
        }

        //没有相互包含
        return null;
    }


    //判断类型中的值是否是相等的
    private static boolean isValueContain(DartObjectValue one, DartObjectValue two) {
        //名称相同
        if (one.getValueName().equals(two.getValueName())) {
            //类型相同
            if (one.getType() == two.getType() && one.getTypeClass().getName().equals(two.getTypeClass().getName())) {
                return true;
            }
            //后者是动态类型，那么我们姑且认为是一样的
            if (two.getType() == DartObjectsValueType.TYPE_DYNAMIC) {
                return true;
            }
        }
        return false;
    }

    //进行筛选，剔除掉重复的类型对象，保证最后创建dart文件的时候不出现相同的类
    private static List<DartObject> filterRepeatObjects(List<DartObject> objects) {

        //复制的
        List<DartObject> copyArrayOne = new ArrayList<DartObject>(objects);
        //复制的
        List<DartObject> copyArrayTwo = new ArrayList<DartObject>(objects);
        //遍历
        for (int a = 0; a < copyArrayOne.size(); a++) {
            //遍历
            for (int b = 0; b < copyArrayTwo.size(); b++) {
                //同一位置不判断
                if (copyArrayOne.get(a) != copyArrayTwo.get(b)) {
                    //如果第一个包含了第二个
                    if (checkRepeatModel(copyArrayOne.get(a), copyArrayTwo.get(b)) == objects.get(a)) {

                        //处理掉B中的重复数据,将类型改掉
                        for (int s = 0; s < copyArrayOne.size(); s++) {
                            for (int j = 0; j < copyArrayOne.get(s).getValues().size(); j++) {
                                if (copyArrayOne.get(s).getValues().get(j).getTypeClass() == copyArrayTwo.get(b)) {
                                    copyArrayOne.get(s).getValues().get(j).setTypeClass(copyArrayOne.get(a));
                                }
                            }
                        }

                        //移除B,因为对象实际没有进行复制，b列表中value对象的typeClass同样被替换掉了
                        copyArrayTwo.remove(b);
                        //减少B的值
                        b--;

                    }
                }
            }
        }
        //移除所有的之前，需要先将value中引用的重复类替换掉才行
        return copyArrayTwo;

    }

    //转换
    private static String dartClassToString(DartObject dartObject) {

        StringBuffer stringBuffer = new StringBuffer();
        //开始
        stringBuffer.append("class " + dartObject.getName() + " {\n");

        stringBuffer.append("\n");

        //参数
        for (int s = 0; s < dartObject.getValues().size(); s++) {

            //驼峰变量名
            String valueName = toCamelCase(dartObject.getValues().get(s).getValueName());
            //驼峰类型名
            String className = dartObject.getValues().get(s).getTypeClass().getName();

            if (dartObject.getValues().get(s).getType() == DartObjectsValueType.TYPE_STRING) {
                stringBuffer.append("  " + className + " " + valueName + ";\n");
            }
            if (dartObject.getValues().get(s).getType() == DartObjectsValueType.TYPE_DYNAMIC) {
                stringBuffer.append("  " + className + " " + valueName + ";\n");
            }
            if (dartObject.getValues().get(s).getType() == DartObjectsValueType.TYPE_MODEL) {
                stringBuffer.append("  " + className + " " + valueName + ";\n");
            }
            if (dartObject.getValues().get(s).getType() == DartObjectsValueType.TYPE_MODEL_LIST) {
                stringBuffer.append("  " + "List<" + className + ">" + " " + valueName + ";\n");
            }
        }
        stringBuffer.append("\n");

        //构造器
        stringBuffer.append("  " + dartObject.getName() + "({\n");
        for (int s = 0; s < dartObject.getValues().size(); s++) {
            String valueName = toCamelCase(dartObject.getValues().get(s).getValueName());
            stringBuffer.append("    " + "this." + valueName + ",\n");
        }
        stringBuffer.append("  });\n");
        stringBuffer.append("\n");


        //从json解析过来fromJson
        stringBuffer.append("  " + dartObject.getName() + ".fromJson(Map<String, dynamic> json) {\n");
        for (int s = 0; s < dartObject.getValues().size(); s++) {
            //驼峰变量名
            String valueName = toCamelCase(dartObject.getValues().get(s).getValueName());
            //驼峰类型名
            String className = dartObject.getValues().get(s).getTypeClass().getName();

            if (dartObject.getValues().get(s).getType() == DartObjectsValueType.TYPE_STRING) {
                stringBuffer.append("    " + valueName + " = " + "json['" + dartObject.getValues().get(s).getValueName() + "']?.toString();\n");
            }
            if (dartObject.getValues().get(s).getType() == DartObjectsValueType.TYPE_DYNAMIC) {
                stringBuffer.append("    " + valueName + " = " + "json['" + dartObject.getValues().get(s).getValueName() + "'];\n");
            }
            if (dartObject.getValues().get(s).getType() == DartObjectsValueType.TYPE_MODEL) {
                stringBuffer.append("    " + "if(json['" + dartObject.getValues().get(s).getValueName() + "'] != null){\n");
                stringBuffer.append("     " + valueName + " = " + className + ".fromJson(json['" + dartObject.getValues().get(s).getValueName() + "']);\n");
                stringBuffer.append("    " + "}\n");
            }
            if (dartObject.getValues().get(s).getType() == DartObjectsValueType.TYPE_MODEL_LIST) {
                //字符串
                if (dartObject.getValues().get(s).getTypeClass() == stringClass) {
                    stringBuffer.append("    " + valueName + " = json['" + dartObject.getValues().get(s).getValueName() + "'];\n");
                }
                //动态类型
                else if (dartObject.getValues().get(s).getTypeClass() == dynamicClass) {
                    stringBuffer.append("    " + valueName + " = json['" + dartObject.getValues().get(s).getValueName() + "'];\n");
                }
                //其他类型
                else {
                    stringBuffer.append("    " + valueName + " = new List<" + className + ">();\n");
                    stringBuffer.append("    " + "if (json['" + dartObject.getValues().get(s).getValueName() + "'] != null) {\n");
                    stringBuffer.append("     " + "json['" + dartObject.getValues().get(s).getValueName() + "'].forEach((v) {\n");
                    stringBuffer.append("     " + valueName + ".add(new " + className + ".fromJson(v));\n");
                    stringBuffer.append("     " + "});\n");
                    stringBuffer.append("    " + "}\n");
                }
            }
        }
        stringBuffer.append("  }\n");
        stringBuffer.append("\n");


        //转换为json toJson
        stringBuffer.append("  " + "Map<String, dynamic> toJson() {\n");
        stringBuffer.append("    " + "final Map<String, dynamic> data = new Map<String, dynamic>();\n");
        for (int s = 0; s < dartObject.getValues().size(); s++) {
            //驼峰变量名
            String valueName = toCamelCase(dartObject.getValues().get(s).getValueName());
            //如果是字符串好处理
            if (dartObject.getValues().get(s).getType() == DartObjectsValueType.TYPE_STRING) {
                stringBuffer.append("    " + "data['" + dartObject.getValues().get(s).getValueName() + "'] = this." + valueName + ";\n");
            }
            //如果是动态类型好处理
            if (dartObject.getValues().get(s).getType() == DartObjectsValueType.TYPE_DYNAMIC) {
                stringBuffer.append("    " + "data['" + dartObject.getValues().get(s).getValueName() + "'] = this." + valueName + ";\n");
            }
            //如果是模型
            if (dartObject.getValues().get(s).getType() == DartObjectsValueType.TYPE_MODEL) {
                stringBuffer.append("    " + "if( this." + valueName + " != null){\n");
                stringBuffer.append("     " + "data['" + dartObject.getValues().get(s).getValueName() + "'] = this." + valueName + ".toJson();\n");
                stringBuffer.append("    " + "}\n");
            }
            //如果是模型列表
            if (dartObject.getValues().get(s).getType() == DartObjectsValueType.TYPE_MODEL_LIST) {

                //动态类型
                if (dartObject.getValues().get(s).getTypeClass() == dynamicClass) {
                    stringBuffer.append("    " + "if( this." + valueName + " != null){\n");
                    stringBuffer.append("     " + "data['" + dartObject.getValues().get(s).getValueName() + "'] = this." + valueName + ";\n");
                    stringBuffer.append("    " + "}\n");
                }
                //字符串类型
                else if (dartObject.getValues().get(s).getTypeClass() == stringClass) {
                    stringBuffer.append("    " + "if( this." + valueName + " != null){\n");
                    stringBuffer.append("     " + "data['" + dartObject.getValues().get(s).getValueName() + "'] = this." + valueName + ";\n");
                    stringBuffer.append("    " + "}\n");
                }
                //对象类型
                else {
                    stringBuffer.append("    " + "if( this." + valueName + " != null){\n");
                    stringBuffer.append("     " + "data['" + dartObject.getValues().get(s).getValueName() + "'] = this." + valueName + ".map((v) => v.toJson()).toList();\n");
                    stringBuffer.append("    " + "}\n");
                }

            }
        }
        stringBuffer.append("    " + "return data;\n");
        stringBuffer.append("  }\n");
        stringBuffer.append("\n");


        //结尾
        stringBuffer.append("}\n");
        //转换为字符串
        return stringBuffer.toString();
    }


}


