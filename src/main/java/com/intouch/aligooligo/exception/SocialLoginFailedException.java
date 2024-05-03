package com.intouch.aligooligo.exception;

public class SocialLoginFailedException extends RuntimeException{
    public SocialLoginFailedException(String message) {
        super(message);
    }
}
