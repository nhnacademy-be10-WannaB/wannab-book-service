package shop.wannab.book_service.book.exception;

import lombok.Getter;
import shop.wannab.book_service.global.exception.ErrorCode;

@Getter
public enum BookErrorCode implements ErrorCode {
    BOOK_NOT_FOUND(404,2001,"도서를 찾을수 없습니다."),
    BOOK_LIKE_NOT_FOUND(404,2002,"좋아요한 도서를 찾을수 없습니다."),
    DUPLICATE_BOOK_LIKE(400,2003,"이미 좋아요를 누른 도서입니다."),
    DUPLICATE_BOOK(400,2004,"이미 존재하는 도서입니다.");

    private final int status;
    private final int code;
    private final String message;

    BookErrorCode(int status, int code, String message){
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
