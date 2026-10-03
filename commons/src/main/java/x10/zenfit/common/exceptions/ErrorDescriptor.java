package x10.zenfit.common.exceptions;

public interface ErrorDescriptor {

    int httpStatus();           // HTTP status: 400 / 404 / 409 / ...
    String code();              // USER.EMAIL_EXISTS
    String defaultMessage();    // Message mặc định
    String type();              // BUSINESS | VALIDATION | SYSTEM
}
