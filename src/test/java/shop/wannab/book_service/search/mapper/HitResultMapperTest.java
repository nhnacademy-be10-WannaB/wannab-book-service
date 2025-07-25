package shop.wannab.book_service.search.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import co.elastic.clients.elasticsearch.core.search.Hit;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.wannab.book_service.search.dto.response.BookSearchResult;
import shop.wannab.book_service.search.dto.response.BookSearchSource;

@ActiveProfiles("ci")
@ExtendWith(MockitoExtension.class)
@DisplayName("HitResultMapperTest 단위 테스트")
class HitResultMapperTest {

    private final HitResultMapper mapper = new HitResultMapper();

    @Test
    @SuppressWarnings("unchecked")
    void fromHit_withHighlight() {
        BookSearchSource source = dummySource();
        Map<String, List<String>> highlight =
                Map.of("title", List.of("<em>자바</em>의 정석"));

        Hit<BookSearchSource> hit = (Hit<BookSearchSource>) org.mockito.Mockito.mock(Hit.class);
        given(hit.source()).willReturn(source);
        given(hit.highlight()).willReturn(highlight);

        // when
        BookSearchResult result = mapper.fromHit(hit);

        // then
        assertThat(result.source()).isEqualTo(source);
        assertThat(result.highlight()).isEqualTo(highlight);
    }

    @Test
    @SuppressWarnings("unchecked")
    void fromHit_whenHighlightNull_returnsEmptyMap() {
        // given
        BookSearchSource source = dummySource();

        Hit<BookSearchSource> hit = (Hit<BookSearchSource>) org.mockito.Mockito.mock(Hit.class);
        given(hit.source()).willReturn(source);
        given(hit.highlight()).willReturn(null);

        // when
        BookSearchResult result = mapper.fromHit(hit);

        // then
        assertThat(result.source()).isEqualTo(source);
        assertThat(result.highlight()).isEmpty();
    }

    // 테스트용 더미 BookSearchSource 생성 헬퍼
    private BookSearchSource dummySource() {
        return new BookSearchSource(
                "book1", "자바의 정석", "ㅈㅂㅇ ㅈㅅ", "자바 책입니다.",
                List.of("프로그래밍", "자바"), List.of("남궁성"), List.of("도우출판"),
                List.of("IT", "개발"),
                LocalDate.of(2023, 1, 1),
                30_000, 27_000, true,
                120, 50, 4.5,
                "http://example.com/thumbnail.jpg"
        );
    }
}