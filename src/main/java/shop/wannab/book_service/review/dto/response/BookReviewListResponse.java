package shop.wannab.book_service.review.dto.response;

import lombok.Builder;
import lombok.Getter;
import shop.wannab.book_service.review.entity.Review;

import java.time.LocalDateTime;

@Builder
@Getter
public class BookReviewListResponse {
    private String reviewContent;
    private Integer reviewScore;
    private LocalDateTime reviewCreatedAt;
    private String username;

    public static BookReviewListResponse from(Review review, String username){
        return BookReviewListResponse.builder()
                .reviewContent(review.getReviewContent())
                .reviewScore(review.getReviewScore())
                .reviewCreatedAt(review.getReviewCreatedAt())
                .username(username)
                .build();
    }
}
