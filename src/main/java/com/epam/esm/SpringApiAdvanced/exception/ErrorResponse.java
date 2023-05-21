package com.epam.esm.SpringApiAdvanced.exception;

public class ErrorResponse {
    private String message;

    private Integer errorCode;

    public ErrorResponse(String message, Integer errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
