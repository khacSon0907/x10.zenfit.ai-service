package x10.zenfit.user.core.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import x10.zenfit.common.exceptions.ErrorDescriptor;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum UserErrorCode implements ErrorDescriptor {

    EMAIL_ALREADY_EXISTS(409, "USER.EMAIL_ALREADY_EXISTS", "Email đã được sử dụng", "BUSINESS"),
    USERNAME_ALREADY_EXISTS(409, "USER.USERNAME_ALREADY_EXISTS", "Username đã tồn tại", "BUSINESS"),
    USER_NOT_FOUND(404, "USER.NOT_FOUND", "Không tìm thấy người dùng", "BUSINESS"),
    USER_DISABLED(403, "USER.DISABLED", "Tài khoản đã bị khóa", "BUSINESS");

    private final int httpStatus;
    private final String code;
    private final String defaultMessage;
    private final String type;
}