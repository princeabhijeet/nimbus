package dev.prince.nimbus.exception;

public class ClientRegistrationNotFoundException extends NimbusParentException {

    public ClientRegistrationNotFoundException(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

}
