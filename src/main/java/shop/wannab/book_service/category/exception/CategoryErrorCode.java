package shop.wannab.book_service.category.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.wannab.book_service.global.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum CategoryErrorCode implements ErrorCode {
    PARENTS_CATEGORY_NOT_FOUND(404,5001,"상위 카테고리를 찾을 수 없습니다")
    ;

    private final int status;
    private final int code;
    private final String message;

}
