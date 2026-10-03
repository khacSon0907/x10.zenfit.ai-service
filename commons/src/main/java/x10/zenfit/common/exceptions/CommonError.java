package x10.zenfit.common.exceptions;



import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import lombok.Getter;

@Getter
@Accessors(fluent = true)   // sinh httpStatus(), code()... thay vì getHttpStatus()
@AllArgsConstructor
public enum CommonError implements ErrorDescriptor {
    INTERNAL_ERROR(500, "COMMON.INTERNAL_ERROR", "Lỗi hệ thống", "SYSTEM"),
    VALIDATION_FAILED(422, "COMMON.VALIDATION_FAILED", "Dữ liệu không hợp lệ", "VALIDATION"),
    UNAUTHORIZED(401, "COMMON.UNAUTHORIZED", "Chưa đăng nhập", "BUSINESS"),
    FORBIDDEN(403, "COMMON.FORBIDDEN", "Không có quyền truy cập", "BUSINESS"),
    NOT_FOUND(404, "COMMON.NOT_FOUND", "Không tìm thấy tài nguyên", "BUSINESS"),
    INVALID_REQUEST(400, "COMMON.INVALID_REQUEST", "Yêu cầu không hợp lệ", "BUSINESS"),
    VALIDATION_ERROR(422, "COMMON.VALIDATION_ERROR", "Dữ liệu không hợp lệ", "VALIDATION"),
    INTERNAL_SERVER_ERROR(500, "COMMON.INTERNAL_SERVER_ERROR", "Lỗi hệ thống", "SYSTEM");
    private final int httpStatus;
    private final String code;
    private final String defaultMessage;
    private final String type;
}