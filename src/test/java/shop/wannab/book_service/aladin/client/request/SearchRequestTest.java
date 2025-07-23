package shop.wannab.book_service.aladin.client.request;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shop.wannab.book_service.aladin.controller.request.BookInfoRequest;
import shop.wannab.book_service.aladin.domain.AladinEnums.CoverOption;
import shop.wannab.book_service.aladin.domain.AladinEnums.QueryType;
import shop.wannab.book_service.aladin.domain.AladinEnums.SearchTarget;
import shop.wannab.book_service.aladin.domain.AladinEnums.SortOption;

@DisplayName("SearchRequest DTO 테스트")
class SearchRequestTest {

    @Test
    @DisplayName("BookInfoRequest에서 SearchRequest로 변환 시 디폴트 값이 적용된다")
    void toParamMap() {
        // given
        BookInfoRequest request = BookInfoRequest.builder()
                .query("워너비")
                .queryType(null)
                .searchTarget(null)
                .start(null)
                .maxResults(null)
                .sort(null)
                .cover(null)
                .categoryId(null)
                .build();

        // when
        SearchRequest result = SearchRequest.from(request);

        // then
        assertThat(result.query()).isEqualTo("워너비");
        assertThat(result.queryType()).isEqualTo("Keyword");
        assertThat(result.searchTarget()).isEqualTo("Book");
        assertThat(result.start()).isEqualTo(1);
        assertThat(result.maxResults()).isEqualTo(100);
        assertThat(result.sort()).isEqualTo("Accuracy");
        assertThat(result.cover()).isEqualTo("Big");
        assertThat(result.categoryId()).isZero();
    }

    @DisplayName("BookInfoRequest에서 SearchRequest로 변환 시 값이 정상 매핑된다")
    @Test
    void from_withAllFields_shouldMapCorrectly() {
        BookInfoRequest request = BookInfoRequest.builder()
                .query("워너비")
                .queryType(QueryType.Title)
                .searchTarget(SearchTarget.Music)
                .start(5)
                .maxResults(50)
                .sort(SortOption.PublishTime)
                .cover(CoverOption.Mid)
                .categoryId(123)
                .build();

        SearchRequest result = SearchRequest.from(request);

        assertThat(result.queryType()).isEqualTo("Title");
        assertThat(result.searchTarget()).isEqualTo("Music");
        assertThat(result.sort()).isEqualTo("PublishTime");
        assertThat(result.cover()).isEqualTo("Mid");
        assertThat(result.categoryId()).isEqualTo(123);
    }
}