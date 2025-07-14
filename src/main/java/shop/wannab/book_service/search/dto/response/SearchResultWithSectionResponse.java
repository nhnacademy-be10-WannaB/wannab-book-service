package shop.wannab.book_service.search.dto.response;

import java.util.List;
import shop.wannab.book_service.search.domain.BookSearchField;

public record SearchResultWithSectionResponse(
        BookSearchField field,
        String label,
        Long total,
        List<BookSearchResult> results
) {
}
