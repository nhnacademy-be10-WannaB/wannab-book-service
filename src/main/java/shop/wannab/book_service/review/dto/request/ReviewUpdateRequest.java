package shop.wannab.book_service.review.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateRequest {
    private String reviewContent;
    private Integer reviewScore;
    private LocalDateTime reviewUpdatedAt;
}
