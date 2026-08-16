package com.nabd.device_service.exception;

public class DeviceNotFoundException extends RuntimeException{
    public DeviceNotFoundException(String msg){
        super(msg);
    }
}
