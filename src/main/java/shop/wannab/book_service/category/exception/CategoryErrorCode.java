package shop.wannab.book_service.category.exception;

import lombok.Getter;
import shop.wannab.book_service.global.exception.ErrorCode;

@Getter
public enum CategoryErrorCode implements ErrorCode {
    INVALID_CATEGORY_HIERARCHY(400, 3001, "카테고리는 최소 2단계 이상이어야 합니다.");

    private final int status;
    private final int code;
    private final String message;

    CategoryErrorCode(int status, int code, String message){
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
