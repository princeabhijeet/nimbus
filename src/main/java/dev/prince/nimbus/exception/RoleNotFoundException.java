package dev.prince.nimbus.exception;

public class RoleNotFoundException extends NimbusParentException {

    public RoleNotFoundException(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

}
