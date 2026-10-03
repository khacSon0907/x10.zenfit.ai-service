package x10.zenfit.common.dto.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public final class ApiResponse<T> extends BaseResponse {

    private final T data;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .type("SUCCESS")
                .title("Success")
                .status(200)
                .code("SUCCESS")
                .message("OK")
                .data(data)
                .build();
    }
}