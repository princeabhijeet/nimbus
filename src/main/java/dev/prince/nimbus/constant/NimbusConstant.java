package dev.prince.nimbus.constant;

public final class NimbusConstant {

    private NimbusConstant() {
        // Private constructor to prevent instantiation
    }

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    public static final String AUTHORIZATION_ENDPOINT_FORMAT = "/oauth2/authorization/{provider}";

}
