package shop.wannab.book_service.book.exception;

import lombok.Getter;
import shop.wannab.book_service.global.exception.BaseException;

@Getter
public class BookApiException extends BaseException {
    private final BookErrorCode bookErrorCode;

    public BookApiException(BookErrorCode bookErrorCode) {
        super(bookErrorCode);
        this.bookErrorCode = bookErrorCode;
    }
}
