package jmaster.io.gatewayservice.exception.custom;

public class MissingAuthorizationHeader extends RuntimeException {
    public MissingAuthorizationHeader(String message) {
        super(message);
    }
}
