package com.flappygo.jsontodart.Controller;


import com.flappygo.jsontodart.Config.WebConfig;
import com.flappygo.jsontodart.Tools.QrCode.QrCodeGenerate;
import com.flappygo.jsontodart.Tools.Unicode.UnicodeTool;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.flappygo.jsontodart.Controller.Base.BaseController;
import org.springframework.web.bind.annotation.RequestParam;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.flappygo.jsontodart.Tools.JsonToDart.DartJsonTool;
import com.flappygo.jsontodart.Models.ResponseModel;
import org.apache.commons.lang3.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSON;

import java.net.URLDecoder;
import java.net.URLEncoder;


@RestController
@RequestMapping("/api")
public class RequestController extends BaseController {

    @Autowired
    WebConfig webConfig;

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

    //json转换为dart
    @RequestMapping(value = "/jsonToDart", produces = "application/json; charset=utf-8")
    public ResponseModel jsonToDart(@RequestParam(value = "jsonStr", defaultValue = "") String jsonStr,
                                    @RequestParam(value = "className", defaultValue = "") String className,
                                    @RequestParam(value = "nullSafety",required = false) boolean nullSafety,
                                    @RequestParam(value = "compileFlag",required = false) boolean compileFlag) {


        if (StringUtils.isNotEmpty(jsonStr)) {
            try {
                //名称
                String name = StringUtils.isEmpty(className) ? "AutoGenerated" : className;

                //默认
                DartJsonTool.SafetyType safetyType;

                //判断
                if (nullSafety) {
                    safetyType = DartJsonTool.SafetyType.SAFETY_NOTNULL;
                } else {
                    safetyType = DartJsonTool.SafetyType.SAFETY_CAN_NULL;
                }

                //设置
                return getSuccessResult(DartJsonTool.generateDartToJson(jsonStr, name, safetyType,compileFlag));

            } catch (Exception ex) {
                return getFailureResult(ex.getMessage());
            }
        }
        return getFailureResult("Json字符串不能为空");

    }

    //Md5加密
    @RequestMapping(value = "/md5Generate", produces = "application/json; charset=utf-8")
    public ResponseModel md5Generate(@RequestParam(value = "md5Str", defaultValue = "") String md5Str) {

        try {
            JSONObject jsonObject = new JSONObject();
            String md32Lower = DigestUtils.md5Hex(md5Str).toLowerCase();
            String md32Upper = DigestUtils.md5Hex(md5Str).toUpperCase();
            jsonObject.put("md16Lower", md32Lower.substring(8, 24));
            jsonObject.put("md16Upper", md32Upper.substring(8, 24));
            jsonObject.put("md32Lower", md32Lower);
            jsonObject.put("md32Upper", md32Upper);
            return getSuccessResult(jsonObject.toJSONString());
        } catch (Exception ex) {
            return getFailureResult(ex.getMessage());
        }
    }

    //url编码
    @RequestMapping(value = "/urlEncode", produces = "application/json; charset=utf-8")
    public ResponseModel urlEncode(@RequestParam(value = "urlStr", defaultValue = "") String urlStr) {
        try {
            return getSuccessResult(URLEncoder.encode(urlStr, "utf-8"));
        } catch (Exception ex) {
            return getFailureResult(ex.getMessage());
        }
    }

    //url解码
    @RequestMapping(value = "/urlDecode", produces = "application/json; charset=utf-8")
    public ResponseModel urlDecode(@RequestParam(value = "urlStr", defaultValue = "") String urlStr) {
        try {
            return getSuccessResult(URLDecoder.decode(urlStr, "utf-8"));
        } catch (Exception ex) {
            return getFailureResult(ex.getMessage());
        }
    }

    //unicode编码
    @RequestMapping(value = "/unicodeEncode", produces = "application/json; charset=utf-8")
    public ResponseModel unicodeEncode(@RequestParam(value = "unicodeStr", defaultValue = "") String unicodeStr) {
        try {
            return getSuccessResult(UnicodeTool.cnToUnicode(unicodeStr));
        } catch (Exception ex) {
            return getFailureResult(ex.getMessage());
        }
    }

    //unicode解码
    @RequestMapping(value = "/unicodeDecode", produces = "application/json; charset=utf-8")
    public ResponseModel unicodeDecode(@RequestParam(value = "unicodeStr", defaultValue = "") String unicodeStr) {
        try {
            return getSuccessResult(UnicodeTool.unicodeToCn(unicodeStr));
        } catch (Exception ex) {
            return getFailureResult(ex.getMessage());
        }
    }

    //unicode解码
    @RequestMapping(value = "/qrcodeGenerate", produces = "application/json; charset=utf-8")
    public ResponseModel qrcodeGenerate(@RequestParam(value = "qrcodeStr", defaultValue = "") String qrcodeStr) {
        try {
            String path = webConfig.getUploadFolder() + System.currentTimeMillis() + ".png";
            String retPath = webConfig.getStaticAccessPath() + System.currentTimeMillis() + ".png";
            QrCodeGenerate.generateQRCodeImage(qrcodeStr, 512, 512, path);
            return getSuccessResult(retPath);
        } catch (Exception ex) {
            return getFailureResult(ex.getMessage());
        }
    }
}
