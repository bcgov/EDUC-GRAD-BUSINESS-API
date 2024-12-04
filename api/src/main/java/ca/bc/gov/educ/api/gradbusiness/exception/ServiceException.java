package ca.bc.gov.educ.api.gradbusiness.exception;

import lombok.Data;

@Data
public class ServiceException extends RuntimeException {

    private final int statusCode;

    public ServiceException(String message, int value) {
        super(message);
        this.statusCode = value;
    }

    public ServiceException(String s, int value, Exception e) {
        super(s, e);
        this.statusCode = value;
    }
}
