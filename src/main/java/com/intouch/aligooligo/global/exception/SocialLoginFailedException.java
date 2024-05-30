package com.intouch.aligooligo.global.exception;

public class SocialLoginFailedException extends RuntimeException{
    public SocialLoginFailedException(String message) {
        super(message);
    }
}
