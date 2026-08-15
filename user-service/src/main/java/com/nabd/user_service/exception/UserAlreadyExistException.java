package com.nabd.user_service.exception;

public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException(String msg){
        super(msg);
    }
}
