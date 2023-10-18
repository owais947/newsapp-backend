package com.navi.assignment2.controller;


import com.navi.assignment2.Exception.*;
import com.navi.assignment2.contract.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({DatabaseErrorException.class,
            InvalidEmailErrorException.class,
            InvalidSourcesProvidedException.class,
            NewsApiErrorException.class,
            UserNotFoundException.class,
            EmailAlreadyExistsException.class,
            UserApiDisabledException.class})

    public ResponseEntity<?> handleException(Exception e) {
        if(e instanceof DatabaseErrorException) {
            return handleDatabaseErrorException((DatabaseErrorException) e);
        }
        else if(e instanceof InvalidEmailErrorException) {
            return handleInvalidEmailErrorException((InvalidEmailErrorException) e);
        }
        else if(e instanceof InvalidSourcesProvidedException) {
            return handleInvalidSourcesProvidedException((InvalidSourcesProvidedException) e);
        }
        else if(e instanceof NewsApiErrorException) {
            return handleNewsApiErrorException((NewsApiErrorException) e);
        }
        else if(e instanceof UserNotFoundException) {
            return handleUserNotFoundException((UserNotFoundException) e);
        }
        else if(e instanceof EmailAlreadyExistsException) {
            return handleEmailAlreadyExistsException((EmailAlreadyExistsException) e);
        }
        else if(e instanceof UserApiDisabledException) {
            return handleUserApiDisabledException((UserApiDisabledException) e);
        }
        else {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> handleDatabaseErrorException(DatabaseErrorException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    private ResponseEntity<?> handleInvalidEmailErrorException(InvalidEmailErrorException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    private ResponseEntity<?> handleInvalidSourcesProvidedException(InvalidSourcesProvidedException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    private ResponseEntity<?> handleNewsApiErrorException(NewsApiErrorException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    private ResponseEntity<?> handleUserNotFoundException(UserNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
    }
    private ResponseEntity<?> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    private ResponseEntity<?> handleUserApiDisabledException(UserApiDisabledException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
    }
}
