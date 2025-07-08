package shop.wannab.book_service.category.exception;

import lombok.Getter;
import shop.wannab.book_service.global.exception.BaseException;

@Getter
public class CategoryApiException extends BaseException {
    private final CategoryErrorCode categoryErrorCode;

    public CategoryApiException(CategoryErrorCode errorCode) {
        super(errorCode);
        this.categoryErrorCode = errorCode;
    }
}
