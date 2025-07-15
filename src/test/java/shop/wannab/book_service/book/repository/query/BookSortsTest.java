package shop.wannab.book_service.book.repository.query;

import com.querydsl.core.types.OrderSpecifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import shop.wannab.book_service.book.entity.QBook;

import static org.assertj.core.api.Assertions.assertThat;

class BookSortsTest {

    private final QBook book = QBook.book;

    @Test
    @DisplayName("정렬 조건 생성 - ID 내림차순")
    void from_sortByIdDesc() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "bookId"));
        OrderSpecifier<?>[] orderSpecifiers = BookSorts.from(pageable);
        assertThat(orderSpecifiers).hasSize(1);
        assertThat(orderSpecifiers[0].toString()).isEqualTo("book.bookId DESC");
    }

    @Test
    @DisplayName("정렬 조건 생성 - 제목 오름차순")
    void from_sortByTitleAsc() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));
        OrderSpecifier<?>[] orderSpecifiers = BookSorts.from(pageable);
        assertThat(orderSpecifiers).hasSize(1);
        assertThat(orderSpecifiers[0].toString()).isEqualTo("book.title ASC");
    }

    @Test
    @DisplayName("정렬 조건 생성 - 여러 조건")
    void from_sortByMultipleProperties() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "bookId").and(Sort.by(Sort.Direction.ASC, "title")));
        OrderSpecifier<?>[] orderSpecifiers = BookSorts.from(pageable);
        assertThat(orderSpecifiers).hasSize(2);
        assertThat(orderSpecifiers[0].toString()).isEqualTo("book.bookId DESC");
        assertThat(orderSpecifiers[1].toString()).isEqualTo("book.title ASC");
    }

    @Test
    @DisplayName("정렬 조건 생성 - 정렬 없음")
    void from_noSort() {
        Pageable pageable = PageRequest.of(0, 10);
        OrderSpecifier<?>[] orderSpecifiers = BookSorts.from(pageable);
        assertThat(orderSpecifiers[0].toString()).isEqualTo("book.bookId ASC");
    }
}
