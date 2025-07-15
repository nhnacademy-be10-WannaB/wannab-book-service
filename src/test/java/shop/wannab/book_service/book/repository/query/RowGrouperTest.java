package shop.wannab.book_service.book.repository.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shop.wannab.book_service.book.controller.response.BookListResponse;
import shop.wannab.book_service.book.repository.projection.BookInfoProjection;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RowGrouperTest {

    @Test
    @DisplayName("BookInfoProjection 목록을 BookListResponse 맵으로 그룹화")
    void group_shouldGroupBookInfoProjectionsIntoBookListResponseMap() {
        // Given
        Long bookId1 = 1L;
        Long bookId2 = 2L;

        BookInfoProjection projection1 = new BookInfoProjection(
                bookId1, "Book 1", "Desc 1", LocalDate.now(), 10000, "isbn1", 10, true,
                "Author A", "Publisher X", "Tag1", "image1.jpg"
        );
        BookInfoProjection projection2 = new BookInfoProjection(
                bookId1, "Book 1", "Desc 1", LocalDate.now(), 10000, "isbn1", 10, true,
                "Author B", "Publisher X", "Tag2", "image1.jpg"
        );
        BookInfoProjection projection3 = new BookInfoProjection(
                bookId2, "Book 2", "Desc 2", LocalDate.now(), 20000, "isbn2", 20, true,
                "Author C", "Publisher Y", "Tag3", "image2.jpg"
        );

        List<BookInfoProjection> rows = List.of(projection1, projection2, projection3);
        List<Long> idOrder = List.of(bookId1, bookId2);

        // When
        Map<Long, BookListResponse> result = RowGrouper.group(rows, idOrder);

        // Then
        assertThat(result).isNotNull().hasSize(2);

        BookListResponse book1Response = result.get(bookId1);
        assertThat(book1Response).isNotNull();
        assertThat(book1Response.getTitle()).isEqualTo("Book 1");
        assertThat(book1Response.getAuthorNames()).containsExactlyInAnyOrder("Author A", "Author B");
        assertThat(book1Response.getPublisherNames()).containsExactlyInAnyOrder("Publisher X");
        assertThat(book1Response.getTagNames()).containsExactlyInAnyOrder("Tag1", "Tag2");
        assertThat(book1Response.getImageUrls()).containsExactlyInAnyOrder("image1.jpg");

        BookListResponse book2Response = result.get(bookId2);
        assertThat(book2Response).isNotNull();
        assertThat(book2Response.getTitle()).isEqualTo("Book 2");
        assertThat(book2Response.getAuthorNames()).containsExactlyInAnyOrder("Author C");
        assertThat(book2Response.getPublisherNames()).containsExactlyInAnyOrder("Publisher Y");
        assertThat(book2Response.getTagNames()).containsExactlyInAnyOrder("Tag3");
        assertThat(book2Response.getImageUrls()).containsExactlyInAnyOrder("image2.jpg");
    }

    @Test
    @DisplayName("빈 목록으로 그룹화")
    void group_shouldHandleEmptyList() {
        // Given
        List<BookInfoProjection> rows = Collections.emptyList();
        List<Long> idOrder = Collections.emptyList();

        // When
        Map<Long, BookListResponse> result = RowGrouper.group(rows, idOrder);

        // Then
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("일부 필드가 null인 경우 그룹화")
    void group_shouldHandleNullFields() {
        // Given
        Long bookId1 = 1L;
        BookInfoProjection projection1 = new BookInfoProjection(
                bookId1, "Book 1", "Desc 1", LocalDate.now(), 10000, "isbn1", 10, true,
                null, null, null, null // Null fields
        );

        List<BookInfoProjection> rows = List.of(projection1);
        List<Long> idOrder = List.of(bookId1);

        // When
        Map<Long, BookListResponse> result = RowGrouper.group(rows, idOrder);

        // Then
        assertThat(result).isNotNull().hasSize(1);
        BookListResponse book1Response = result.get(bookId1);
        assertThat(book1Response).isNotNull();
        assertThat(book1Response.getTitle()).isEqualTo("Book 1");
        assertThat(book1Response.getAuthorNames()).isEmpty();
        assertThat(book1Response.getPublisherNames()).isEmpty();
        assertThat(book1Response.getTagNames()).isEmpty();
        assertThat(book1Response.getImageUrls()).isEmpty();
    }
}