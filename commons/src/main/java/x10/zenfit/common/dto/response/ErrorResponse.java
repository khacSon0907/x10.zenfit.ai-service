package x10.zenfit.common.dto.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@SuperBuilder
public final class ErrorResponse extends BaseResponse {

    private final Object detail;

    public static ErrorResponse of(String type, int status, String code,
                                   String message, Object detail,
                                   String path, String traceId) {
        return ErrorResponse.builder()
                .type(type)
                .title(httpTitle(status))
                .status(status)
                .code(code)
                .message(message)
                .detail(detail == null ? Map.of() : detail)
                .path(path)
                .traceId(traceId)
                .build();
    }

    public static String httpTitle(int status) {
        return switch (status) {
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 409 -> "Conflict";
            case 422 -> "Validation Error";
            case 429 -> "Too Many Requests";
            case 500 -> "Internal Server Error";
            default -> "Error";
        };
    }

    public static Map<String, List<String>> fieldError(String field, String message) {
        return Map.of(field, List.of(message));
    }

    public static Map<String, Object> detailOf(Object... kv) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            map.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return map;
    }
}