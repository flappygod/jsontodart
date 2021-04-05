package com.flappygo.jsontodart.Controller.Base;

import com.flappygo.jsontodart.Models.ResponseModel;

public class BaseController {


    //成功
    protected  ResponseModel getSuccessResult(String data) {
        ResponseModel responseModel = new ResponseModel();
        responseModel.setData(data);
        responseModel.setCode(200);
        return responseModel;
    }

    //失败
    protected ResponseModel getFailureResult(String data) {
        ResponseModel responseModel = new ResponseModel();
        responseModel.setMsg(data);
        responseModel.setCode(400);
        return responseModel;
    }

}
