package dev.prince.nimbus.exception;

public class LoginValidationException extends NimbusParentException {

    public LoginValidationException(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

}
