package com.remicartier.appdirect.hiring;

import org.springframework.http.HttpStatus;

/**
 * Created by IntelliJ IDEA.
 * User: remicartier
 * Date: 2015-04-11
 * Time: 12:11 PM
 */
public class EventException extends Exception {
    private Result result;
    private HttpStatus httpStatus;

    public EventException(Throwable cause, Result result, HttpStatus httpStatus) {
        super(cause);
        this.result = result;
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Result getResult() {
        return result;
    }
}
