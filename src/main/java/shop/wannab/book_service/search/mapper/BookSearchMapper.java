package shop.wannab.book_service.search.mapper;

import co.elastic.clients.elasticsearch.core.search.Hit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import shop.wannab.book_service.search.dto.response.BookSearchResponse;
import shop.wannab.book_service.search.dto.response.BookSearchSource;

public class BookSearchMapper {

    public static BookSearchResponse fromHit(Hit<BookSearchSource> hit) {
        return new BookSearchResponse(
                hit.source(),
                safeHighlight(hit)
        );
    }

    private static Map<String, List<String>> safeHighlight(Hit<?> hit) {
        return Optional.ofNullable(hit.highlight()).orElse(Collections.emptyMap());
    }
}
