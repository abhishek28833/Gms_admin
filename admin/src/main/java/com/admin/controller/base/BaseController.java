package com.admin.controller.base;

public class BaseController {


    /**
     * Function that sends successful API Response
     *
     * @param message
     * @param data
     * @return
     */
    public GlobalApiResponse successResponse(String message, Object data) {
        GlobalApiResponse globalApiResponse = new GlobalApiResponse();
        globalApiResponse.setStatus(true);
        globalApiResponse.setMessage(message);
        globalApiResponse.setData(data);
        return globalApiResponse;
    }

    /**
     * Function that sends successful API Response
     *
     * @param message
     * @param data
     * @return
     */
    public GlobalApiResponse successResponse(String message, Object data, Integer statusCode) {
        GlobalApiResponse globalApiResponse = new GlobalApiResponse();
        globalApiResponse.setStatus(true);
        globalApiResponse.setMessage(message);
        globalApiResponse.setData(data);
        globalApiResponse.setStatusCode(statusCode);
        return globalApiResponse;
    }

    /**
     * Function that sends error API Response
     *
     * @param message
     * @param errors
     * @return
     */
    public GlobalApiResponse errorResponse(String message, Object errors) {
        GlobalApiResponse globalApiResponse = new GlobalApiResponse();
        globalApiResponse.setStatus(false);
        globalApiResponse.setMessage(message);
        globalApiResponse.setData(errors);
        return globalApiResponse;
    }

    /**
     * Function that sends error API Response
     *
     * @param message
     * @param statusCode
     * @param errors
     * @return
     */
    public GlobalApiResponse errorResponse(String message, Integer statusCode, Object errors) {
        GlobalApiResponse globalApiResponse = new GlobalApiResponse();
        globalApiResponse.setStatusCode(statusCode);
        globalApiResponse.setStatus(false);
        globalApiResponse.setMessage(message);
        globalApiResponse.setData(errors);
        return globalApiResponse;
    }
}

