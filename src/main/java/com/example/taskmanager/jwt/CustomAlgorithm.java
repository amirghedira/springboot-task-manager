package com.example.taskmanager.jwt;

import com.auth0.jwt.algorithms.Algorithm;

public class CustomAlgorithm {
    private final Algorithm customAlgorithm;

    public CustomAlgorithm() {
         Algorithm algorithm =  Algorithm.HMAC256("secret".getBytes());
        this.customAlgorithm = algorithm;

    }
    public Algorithm getAlgorithm (){
        return customAlgorithm;
    }
}
