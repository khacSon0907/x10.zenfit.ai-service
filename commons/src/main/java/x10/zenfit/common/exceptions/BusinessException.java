package x10.zenfit.common.exceptions;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorDescriptor error;

    public BusinessException(ErrorDescriptor error) {
        super(error.defaultMessage());
        this.error = error;
    }

    public BusinessException(ErrorDescriptor error, String message) {
        super(message);              // ghi đè message mặc định khi cần
        this.error = error;
    }

    public BusinessException(ErrorDescriptor error, Throwable cause) {
        super(error.defaultMessage(), cause);
        this.error = error;
    }
}