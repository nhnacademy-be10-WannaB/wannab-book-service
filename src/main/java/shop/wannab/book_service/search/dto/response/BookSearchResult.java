package shop.wannab.book_service.search.dto.response;

import java.util.List;
import java.util.Map;

public record BookSearchResult(
    BookSearchSource source,
    Map<String, List<String>> highlight
) {
}
