package x10.zenfit.common.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;

@Getter
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"type", "title", "status", "code", "message",
        "data", "detail", "path", "traceId", "timestamp"})
public abstract class BaseResponse implements Serializable {
    protected final String type;
    protected final String title;
    protected final Integer status;
    protected final String code;
    protected final String message;
    protected final String path;
    protected final String traceId;

    @Builder.Default
    protected final Instant timestamp = Instant.now();
}