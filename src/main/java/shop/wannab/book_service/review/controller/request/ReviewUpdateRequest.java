package shop.wannab.book_service.review.controller.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.wannab.book_service.global.deserializer.CommaSeparatedToListDeserializer;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateRequest {
    private String reviewContent;
    private Integer reviewScore;
    private LocalDateTime reviewUpdatedAt;

    @JsonDeserialize(using = CommaSeparatedToListDeserializer.class)
    private List<String> reviewImages;
}
