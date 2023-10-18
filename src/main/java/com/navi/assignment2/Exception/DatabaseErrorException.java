package com.navi.assignment2.Exception;

public class DatabaseErrorException extends RuntimeException{
    public DatabaseErrorException(String message) {
        super(message);
    }
}
