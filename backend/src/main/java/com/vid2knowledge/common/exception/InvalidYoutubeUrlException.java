package com.vid2knowledge.common.exception;

public class InvalidYoutubeUrlException extends RuntimeException {
    public InvalidYoutubeUrlException(String message){
        super(message);
    }
}
