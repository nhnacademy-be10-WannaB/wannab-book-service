package shop.wannab.book_service.aladin.exception.response;

import shop.wannab.book_service.aladin.exception.AladinErrorCode;

public record AladinErrorResponse(
        String code,
        String message
) {
    public AladinErrorResponse(AladinErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMessage());
    }
}
