package shop.wannab.book_service.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.MsearchRequest;
import co.elastic.clients.elasticsearch.core.MsearchResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shop.wannab.book_service.global.properties.ElasticsearchIndexProperties;
import shop.wannab.book_service.search.domain.BookSearchField;
import shop.wannab.book_service.search.dto.response.BookSearchResult;
import shop.wannab.book_service.search.dto.response.BookSearchSource;
import shop.wannab.book_service.search.dto.response.SearchResultWithSectionResponse;
import shop.wannab.book_service.search.factory.BookSearchQueryFactory;
import shop.wannab.book_service.search.mapper.SearchResultMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookSearchService {

    private final ElasticsearchClient client;
    private final SearchResultMapper mapper;
    private final ElasticsearchIndexProperties indexProps;
    private final BookSearchQueryFactory bookSearchQueryFactory;

    /**
     * 키워드 단일 검색
     * 키워드가 제목과 설명에 포함되어있는 책을 검색하는 메서드
     */
    public List<BookSearchResult> searchBooksByKeyword(String keyword) throws IOException {
        SearchResponse<BookSearchResult> response = client.search(s -> s
                        .index(indexProps.getBook())
                        .query(q -> q
                                .match(m -> m
                                        .field("all")
                                        .query(keyword)
                                )
                        )
                        .size(10),
                BookSearchResult.class
        );

        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    /**
     * 키워드 통합 검색
     * searchField 클래스에 있는 내용을 Els index 에서 검색한다
     */
    public List<SearchResultWithSectionResponse> multiSearchBooks(String keyword, Set<BookSearchField> targets) throws IOException {

        List<BookSearchField> ordered = BookSearchField.ordered(targets);

        MsearchRequest req = bookSearchQueryFactory.buildMultiSearch(keyword, ordered);
        MsearchResponse<BookSearchSource> resp = client.msearch(req, BookSearchSource.class);
        log.info("response : {}", resp.responses());
        return mapper.toSections(ordered, resp.responses());
    }


}
