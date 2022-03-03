package com.virnect.smic.daemon.config.connection;

public class NoConnectionAvailableException extends RuntimeException{

    public NoConnectionAvailableException(String message) {
        super(message);
    }

}
