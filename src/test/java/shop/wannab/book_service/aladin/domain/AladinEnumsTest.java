package shop.wannab.book_service.aladin.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shop.wannab.book_service.aladin.domain.AladinEnums.CoverOption;
import shop.wannab.book_service.aladin.domain.AladinEnums.QueryType;
import shop.wannab.book_service.aladin.domain.AladinEnums.SearchTarget;
import shop.wannab.book_service.aladin.domain.AladinEnums.SortOption;

@DisplayName("알라딘 API 매핑을 돕는 Enum 클래스 테스트")
class AladinEnumsTest {

    @Test
    @DisplayName("QueryType enum description 매핑 테스트")
    void queryTypeDescriptionsMappingTest() {
        assertThat(QueryType.Keyword.description).isEqualTo("제목, 저자");
        assertThat(QueryType.Title.description).isEqualTo("제목");
        assertThat(QueryType.Author.description).isEqualTo("저자");
        assertThat(QueryType.Publisher.description).isEqualTo("출판사");
    }

    @Test
    @DisplayName("SearchTarget enum description 매핑 테스트")
    void searchTargetDescriptionsMappingTest() {
        assertThat(SearchTarget.Book.description).isEqualTo("도서");
        assertThat(SearchTarget.Music.description).isEqualTo("음반");
        assertThat(SearchTarget.Used.description).isEqualTo("중고");
        assertThat(SearchTarget.eBook.description).isEqualTo("전자책");
    }

    @Test
    @DisplayName("SortOption enum description 매핑 테스트")
    void sortOptionDescriptions() {
        assertThat(SortOption.Accuracy.description).isEqualTo("관련도");
        assertThat(SortOption.PublishTime.description).isEqualTo("출간일");
        assertThat(SortOption.Title.description).isEqualTo("제목");
        assertThat(SortOption.SalesPoint.description).isEqualTo("판매량");
        assertThat(SortOption.CustomerRating.description).isEqualTo("고객평점");
    }

    @Test
    @DisplayName("CoverOption enum description 매핑 테스트")
    void coverOptionDescriptions() {
        assertThat(CoverOption.Big.description).isEqualTo("200px");
        assertThat(CoverOption.MidBig.description).isEqualTo("150px");
        assertThat(CoverOption.Mid.description).isEqualTo("85px");
        assertThat(CoverOption.Small.description).isEqualTo("75px");
        assertThat(CoverOption.Mini.description).isEqualTo("65px");
        assertThat(CoverOption.None.description).isEqualTo("0px");
    }

    @Test
    @DisplayName("모든 enum 값들의 설명은 비어있으면 안된다")
    void allQueryTypesShouldHaveNonNullDescriptions() {
        for (QueryType type : QueryType.values()) {
            assertThat(type.description).isNotNull();
        }
    }
}