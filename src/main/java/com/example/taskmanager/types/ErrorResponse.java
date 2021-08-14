package com.example.taskmanager.types;

import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponse  {

    ResponseEntity response;
    public ErrorResponse(int status, String message){
        Map<String, String> error = new HashMap<>();
        error.put("message", message);
        this.response= new ResponseEntity(error,null ,status);
    }
    public ResponseEntity build(){
        return response;
    }
}
