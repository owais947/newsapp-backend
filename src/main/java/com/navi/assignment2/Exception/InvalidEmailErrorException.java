package com.navi.assignment2.Exception;

public class InvalidEmailErrorException extends RuntimeException{
    public InvalidEmailErrorException(String message) {
        super(message);
    }
}
