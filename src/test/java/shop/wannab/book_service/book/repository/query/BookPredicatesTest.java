package shop.wannab.book_service.book.repository.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shop.wannab.book_service.book.entity.QBook;

class BookPredicatesTest {

    private final QBook book = QBook.book;

    @Test
    @DisplayName("제목으로 검색 조건 생성 - 키워드 유효")
    void titleLike_validKeyword() {
        String keyword = "test";
        Predicate predicate = BookPredicates.titleLike(keyword);
        assertThat(predicate).hasToString("containsIc(book.title,test)");
    }

    @Test
    @DisplayName("제목으로 검색 조건 생성 - 키워드 null")
    void titleLike_nullKeyword() {
        String keyword = null;
        Predicate predicate = BookPredicates.titleLike(keyword);
        assertThat(predicate).isNull();
    }

    @Test
    @DisplayName("제목으로 검색 조건 생성 - 키워드 공백")
    void titleLike_emptyKeyword() {
        String keyword = " ";
        Predicate predicate = BookPredicates.titleLike(keyword);
        assertThat(predicate).isNull();
    }
}
