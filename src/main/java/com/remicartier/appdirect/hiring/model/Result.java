package com.remicartier.appdirect.hiring.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: remicartier
 * Date: 2015-04-10
 * Time: 8:12 PM
 */
@XmlRootElement(name = "result")
public class Result {
    private boolean success;
    private String message;
    private String accountIdentifier;
    private String errorCode;

    public Result() {
    }

    public Result(String accountIdentifier, String errorCode, String message, boolean success) {
        this.accountIdentifier = accountIdentifier;
        this.errorCode = errorCode;
        this.message = message;
        this.success = success;
    }

    @XmlElement
    public String getAccountIdentifier() {
        return accountIdentifier;
    }

    public void setAccountIdentifier(String accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
    }

    @XmlElement
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @XmlElement
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @XmlElement
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
