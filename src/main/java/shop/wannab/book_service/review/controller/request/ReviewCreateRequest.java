package shop.wannab.book_service.review.controller.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.wannab.book_service.global.deserializer.CommaSeparatedToListDeserializer;
import shop.wannab.book_service.review.entity.Review;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequest {
    private String reviewContent;
    private Integer reviewScore;
    private LocalDateTime reviewCreatedAt;
    private Long obId;

    @JsonDeserialize(using = CommaSeparatedToListDeserializer.class)
    private List<String> reviewImages;
}
