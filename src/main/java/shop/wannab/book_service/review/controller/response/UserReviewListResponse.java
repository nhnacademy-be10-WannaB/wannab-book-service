package shop.wannab.book_service.review.controller.response;

import lombok.Builder;
import lombok.Getter;
import shop.wannab.book_service.review.entity.Review;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UserReviewListResponse {
    private String reviewContent;
    private Integer reviewScore;
    private LocalDateTime reviewCreatedAt;
    private List<ReviewImageResponse> reviewImages;

    public static UserReviewListResponse from(Review review){
        return UserReviewListResponse.builder()
                .reviewContent(review.getReviewContent())
                .reviewScore(review.getReviewScore())
                .reviewCreatedAt(review.getReviewCreatedAt())
                .reviewImages(
                        review.getReviewImages().stream()
                                .map(ReviewImageResponse::from)
                                .toList()
                )
                .build();
    }
}
