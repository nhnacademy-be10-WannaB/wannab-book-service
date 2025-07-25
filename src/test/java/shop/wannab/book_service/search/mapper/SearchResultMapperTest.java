package shop.wannab.book_service.search.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import co.elastic.clients.elasticsearch.core.msearch.MultiSearchItem;
import co.elastic.clients.elasticsearch.core.msearch.MultiSearchResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.wannab.book_service.search.domain.BookSearchField;
import shop.wannab.book_service.search.dto.response.BookSearchResult;
import shop.wannab.book_service.search.dto.response.BookSearchSource;
import shop.wannab.book_service.search.dto.response.SearchResultWithSectionResponse;

@ActiveProfiles("ci")
@ExtendWith(MockitoExtension.class)
@DisplayName("SearchResultMapper 단위 테스트")
class SearchResultMapperTest {

    @InjectMocks
    private SearchResultMapper searchResultMapper;

    @Mock
    private HitResultMapper hitResultMapper;

    @Test
    void toSections() {
        // given
        BookSearchField field = BookSearchField.TITLE;
        BookSearchSource source = new BookSearchSource(
                "book1",
                "자바의 정석",
                "ㅈㅂㅇ ㅈㅅ",
                "자바 책입니다.",
                List.of("프로그래밍", "자바"),
                List.of("남궁성"),
                List.of("도우출판"),
                List.of("IT", "개발"),
                LocalDate.of(2023, 1, 1),
                30000,
                27000,
                true,
                120,
                50,
                4.5,
                "http://example.com/thumbnail.jpg"
        );

        Hit<BookSearchSource> hit = Hit.of(h -> h
                .index("books")
                .id(source.bookId())
                .source(source)
                .highlight("title", List.of("<em>자바</em>의 정석")));

        MultiSearchItem<BookSearchSource> msItem = MultiSearchItem.of(ms -> ms
                .took(3)
                .timedOut(false)
                .shards(s -> s.total(1).successful(1).skipped(0).failed(0))
                .hits(hs -> hs
                        .total(t -> t.value(100L).relation(TotalHitsRelation.Eq))
                        .hits(List.of(hit))));

        MultiSearchResponseItem<BookSearchSource> item =
                MultiSearchResponseItem.of(r -> r.result(msItem));

        BookSearchResult mapped = new BookSearchResult(
                source, Map.of("title", List.of("<em>자바</em>의 정석"))
        );
        given(hitResultMapper.fromHit(hit)).willReturn(mapped);

        // when
        List<SearchResultWithSectionResponse> sections =
                searchResultMapper.toSections(List.of(field), List.of(item));

        // then
        assertThat(sections).hasSize(1);

        SearchResultWithSectionResponse section = sections.getFirst();
        assertThat(section.field()).isEqualTo(field);
        assertThat(section.total()).isEqualTo(100L);
        assertThat(section.results()).containsExactly(mapped);
    }
}