package dev.prince.nimbus.exception;

public class UserNotFoundException extends NimbusParentException {

    public UserNotFoundException(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

}
