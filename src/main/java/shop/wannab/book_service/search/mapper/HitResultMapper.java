package shop.wannab.book_service.search.mapper;

import co.elastic.clients.elasticsearch.core.search.Hit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;
import shop.wannab.book_service.search.dto.response.BookSearchResult;
import shop.wannab.book_service.search.dto.response.BookSearchSource;

@Component
public class HitResultMapper {

    public BookSearchResult fromHit(Hit<BookSearchSource> hit) {
        return new BookSearchResult(
                hit.source(),
                safeHighlight(hit)
        );
    }

    private Map<String, List<String>> safeHighlight(Hit<?> hit) {
        return Optional.ofNullable(hit.highlight()).orElse(Collections.emptyMap());
    }
}
