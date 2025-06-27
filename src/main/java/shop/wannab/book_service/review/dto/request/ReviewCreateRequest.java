package shop.wannab.book_service.review.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.wannab.book_service.review.entity.Review;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequest {
    private String reviewContent;
    private Integer reviewScore;
    private LocalDateTime reviewCreatedAt;
    private Long obId;
}
