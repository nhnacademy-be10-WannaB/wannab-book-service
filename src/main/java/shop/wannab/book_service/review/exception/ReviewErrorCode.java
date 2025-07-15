package shop.wannab.book_service.review.exception;

import lombok.Getter;
import shop.wannab.book_service.global.exception.ErrorCode;

@Getter
public enum ReviewErrorCode implements ErrorCode {
    REVIEW_NOT_FOUND(404,2004,"리뷰를 찾을수 없습니다."),
    REVIEW_ALREADY_EXISTS(409, 2005, "이미 존재하는 리뷰입니다."),
    REVIEW_NOT_ELIGIBLE(400,2006,"배송이 완료되지않아 리뷰를 작성할수 없습니다.");

    private final int status;
    private final int code;
    private final String message;

    ReviewErrorCode(int status, int code, String message){
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
