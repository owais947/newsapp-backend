package com.navi.assignment2.Exception;

public class NewsApiErrorException extends RuntimeException{
    public NewsApiErrorException(String message) {
        super(message);
    }
}
