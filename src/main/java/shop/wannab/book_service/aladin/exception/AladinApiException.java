package shop.wannab.book_service.aladin.exception;

import lombok.Getter;

@Getter
public class AladinApiException extends RuntimeException {
    private final AladinErrorCode errorCode;

    public AladinApiException(AladinErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
