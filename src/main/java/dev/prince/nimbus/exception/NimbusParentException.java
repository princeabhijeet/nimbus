package dev.prince.nimbus.exception;

import lombok.Getter;

@Getter
public class NimbusParentException extends RuntimeException {

    private final int errorCode;
    private final String errorMessage;

    public NimbusParentException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static String error(int code, String message) {
        return code + ": " + message;
    }
}
