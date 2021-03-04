package com.flappygo.jsontodart.Controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.flappygo.jsontodart.Controller.Base.BaseController;
import com.flappygo.jsontodart.JsonToDart.DartJsonTool;
import com.flappygo.jsontodart.Models.ResponseModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class RequestController extends BaseController {


    //json转换为dart
    @RequestMapping(value = "/jsonToDart", produces = "application/json; charset=utf-8")
    public ResponseModel jsonToDart(@RequestParam(value = "jsonStr", defaultValue = "") String jsonStr,
                                    @RequestParam(value = "className", defaultValue = "") String className) {


        if (StringUtils.isNotEmpty(jsonStr)) {
            try {
                if (StringUtils.isEmpty(className)) {
                    return getSuccessResult(DartJsonTool.generateDartToJson(jsonStr, "GenerateClass"));
                } else {
                    return getSuccessResult(DartJsonTool.generateDartToJson(jsonStr, className));
                }
            } catch (Exception ex) {
                return getFailureResult(ex.getMessage());
            }
        }
        return getFailureResult("Json字符串不能为空");

    }


    //美化json字符串
    @RequestMapping(value = "/jsonToPretty", produces = "application/json; charset=utf-8")
    public ResponseModel jsonToPretty(@RequestParam(value = "jsonStr", defaultValue = "") String jsonStr) {

        if (StringUtils.isNotEmpty(jsonStr)) {
            try {
                JSONObject object = JSONObject.parseObject(jsonStr);
                String pretty = JSON.toJSONString(object, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
                return getSuccessResult(pretty);
            } catch (Exception ex) {
                return getFailureResult(ex.getMessage());
            }
        }
        return getFailureResult("Json字符串不能为空");

    }

}
