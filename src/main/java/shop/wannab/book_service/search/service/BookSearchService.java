package shop.wannab.book_service.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.MsearchRequest;
import co.elastic.clients.elasticsearch.core.MsearchResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.msearch.MultiSearchResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.wannab.book_service.global.properties.ElasticsearchIndexProperties;
import shop.wannab.book_service.search.domain.BookSearchField;
import shop.wannab.book_service.search.dto.response.BookSearchResponse;
import shop.wannab.book_service.search.dto.response.BookSearchSource;
import shop.wannab.book_service.search.factory.BookSearchQueryFactory;
import shop.wannab.book_service.search.mapper.BookSearchMapper;

@Service
@RequiredArgsConstructor
public class BookSearchService {

    private final ElasticsearchClient client;
    private final ElasticsearchIndexProperties indexProps;
    private final BookSearchQueryFactory bookSearchQueryFactory;

    /**
     * 키워드 단일 검색
     * 키워드가 제목과 설명에 포함되어있는 책을 검색하는 메서드
     */
    public List<BookSearchResponse> searchBooksByKeyword(String keyword) throws IOException {
        SearchResponse<BookSearchResponse> response = client.search(s -> s
                        .index(indexProps.getBook())
                        .query(q -> q
                                .match(m -> m
                                        .field("all")
                                        .query(keyword)
                                )
                        )
                        .size(10),
                BookSearchResponse.class
        );

        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    /**
     * 키워드 통합 검색
     * searchField 클래스에 있는 내용을 Els index 검색한다
     */
    public List<List<BookSearchResponse>> multiSearchBooks(String keyword, List<BookSearchField> fields) throws IOException {

        List<BookSearchField> targets = (fields.isEmpty()) ? BookSearchField.basicList() : fields;

        MsearchRequest req = bookSearchQueryFactory.buildMultiSearch(
                keyword,
                targets
        );

        MsearchResponse<BookSearchSource> resp =
                client.msearch(req, BookSearchSource.class);

        return resp.responses().stream()
                .map(MultiSearchResponseItem::result)
                .map(ResponseBody::hits)
                .map(HitsMetadata::hits)
                .map(list -> list.stream()
                        .map(BookSearchMapper::fromHit)
                        .toList())
                .toList();
    }


}
