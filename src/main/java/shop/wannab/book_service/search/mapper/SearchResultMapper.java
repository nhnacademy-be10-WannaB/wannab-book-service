package shop.wannab.book_service.search.mapper;

import co.elastic.clients.elasticsearch.core.msearch.MultiSearchResponseItem;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.wannab.book_service.search.domain.BookSearchField;
import shop.wannab.book_service.search.dto.response.SearchResultWithSectionResponse;
import shop.wannab.book_service.search.dto.response.BookSearchResult;
import shop.wannab.book_service.search.dto.response.BookSearchSource;

@Component
@RequiredArgsConstructor
public class SearchResultMapper {

    private final HitResultMapper hitMapper;

    /**
     * Els _msearch 쿼리 응답을 매핑하는 메서드
     * 섹션(통합검색, 제목 등) : 응답 결과 리스트 = 1 : N
     */
    public List<SearchResultWithSectionResponse> toSections(List<BookSearchField> ordered,
                                                            List<MultiSearchResponseItem<BookSearchSource>> items) {

        return IntStream.range(0, Math.min(ordered.size(), items.size()))
                .mapToObj(i -> buildSection(ordered.get(i), items.get(i)))
                .toList();
    }

    private SearchResultWithSectionResponse buildSection(BookSearchField field,
                                                         MultiSearchResponseItem<BookSearchSource> item) {

        if (item.isFailure()) {
            return new SearchResultWithSectionResponse(field, field.getDescription(), 0L, List.of());
        }

        HitsMetadata<BookSearchSource> hitsMeta = item.result().hits();
        List<BookSearchResult> results = hitsMeta.hits().stream()
                .map(hitMapper::fromHit)
                .toList();
        long total = hitsMeta.total() != null ? hitsMeta.total().value() : results.size();

        return new SearchResultWithSectionResponse(field, field.getDescription(), total, results);
    }
}
