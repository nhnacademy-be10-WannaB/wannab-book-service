package shop.wannab.book_service.review.dto.response;

import lombok.Builder;
import lombok.Getter;
import shop.wannab.book_service.review.entity.Review;

import java.time.LocalDateTime;

@Builder
@Getter
public class UserReviewListResponse {
    private String reviewContent;
    private Integer reviewScore;
    private LocalDateTime reviewCreatedAt;

    public static UserReviewListResponse from(Review review){
        return UserReviewListResponse.builder()
                .reviewContent(review.getReviewContent())
                .reviewScore(review.getReviewScore())
                .reviewCreatedAt(review.getReviewCreatedAt())
                .build();
    }
}
