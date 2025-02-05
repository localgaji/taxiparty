package com.localgaji.taxi.__global__.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    public ErrorType errorType;
    public String message;

    public CustomException(ErrorType errorType) {
        this.errorType = errorType;
        this.message = errorType.getErrorMessage();
    }
}