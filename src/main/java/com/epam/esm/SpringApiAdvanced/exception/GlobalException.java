package com.epam.esm.SpringApiAdvanced.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.epam.esm.SpringApiAdvanced.exception.ErrorCode.ENTITY_ALREADY_EXIST;
import static com.epam.esm.SpringApiAdvanced.exception.ErrorCode.NOT_FOUND_ERROR_CODE;

@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleNotFoundException(Exception ex) {
        return new ErrorResponse(ex.getMessage(), NOT_FOUND_ERROR_CODE);
    }

    @ExceptionHandler(DataBaseRuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleDataIntegrityViolationException(Exception exception) {
        return new ErrorResponse(exception.getMessage(), ENTITY_ALREADY_EXIST);
    }

}
