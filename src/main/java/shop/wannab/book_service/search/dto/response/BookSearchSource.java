package shop.wannab.book_service.search.dto.response;

import java.time.LocalDate;
import java.util.List;

public record BookSearchSource(
        String bookId,
        String title,
        String title_chosung,
        String description,
        List<String> tags,
        List<String> authors,
        List<String> publishers,
        List<String> categories,
        LocalDate publicationDate,
        Integer originPrice,
        Integer salesPrice,
        Boolean status,
        Integer likeCount,
        Integer reviewCount,
        Double averageRating,
        String thumbnailUrl
) {
}
