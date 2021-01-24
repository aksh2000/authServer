package com.quinbay.authServer.dataModels.response;

import java.util.Map;

public class StandardApiResponse {

    private final boolean success;
    private final String error;
    private final Map<String, Object> data;


    /*
    {
        success: true,
        error:"none",
        data:{
        "jwt" :"THIS IS JWT ACCESS CODE"
        }

    }
     */


    public StandardApiResponse(boolean success, String error, Map<String, Object> data){
        this.success = success;
        this.error = error;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
