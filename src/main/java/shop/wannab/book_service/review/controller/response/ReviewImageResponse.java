package shop.wannab.book_service.review.controller.response;

import lombok.Builder;
import lombok.Getter;
import shop.wannab.book_service.review.entity.ReviewImage;

@Getter
@Builder
public class ReviewImageResponse {
    private String reviewImageUrl;

    public static ReviewImageResponse from(ReviewImage image) {
        return ReviewImageResponse.builder()
                .reviewImageUrl(image.getReviewImageUrl())
                .build();
    }
}
