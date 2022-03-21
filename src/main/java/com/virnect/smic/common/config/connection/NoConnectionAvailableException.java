package com.virnect.smic.common.config.connection;

public class NoConnectionAvailableException extends RuntimeException{

    public NoConnectionAvailableException(String message) {
        super(message);
    }

}
