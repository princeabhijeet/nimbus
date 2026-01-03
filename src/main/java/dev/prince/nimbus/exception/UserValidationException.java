package dev.prince.nimbus.exception;

public class UserValidationException extends NimbusParentException {

    public UserValidationException(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

}
