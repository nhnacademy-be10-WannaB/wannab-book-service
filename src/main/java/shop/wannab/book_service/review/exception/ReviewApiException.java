package shop.wannab.book_service.review.exception;

import lombok.Getter;
import shop.wannab.book_service.global.exception.BaseException;

@Getter
public class ReviewApiException extends BaseException {
    private final ReviewErrorCode reviewErrorCode;

    public ReviewApiException(ReviewErrorCode reviewErrorCode){
        super(reviewErrorCode);
        this.reviewErrorCode = reviewErrorCode;
    }
}
