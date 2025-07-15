
package shop.wannab.book_service.book.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shop.wannab.book_service.author.entity.Author;
import shop.wannab.book_service.category.entity.Category;
import shop.wannab.book_service.publisher.entity.Publisher;
import shop.wannab.book_service.tag.entity.Tag;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookEntityTest {

    @Test
    @DisplayName("Book 엔티티 빌더 테스트")
    void bookEntityBuilderTest() {
        Book book = Book.builder()
                .title("Test Book")
                .description("Test Description")
                .publicationDate(LocalDate.now())
                .isbn("1234567890")
                .originPrice(20000)
                .salesPrice(18000)
                .stock(100)
                .status(true)
                .build();

        assertNotNull(book);
        assertEquals("Test Book", book.getTitle());
        assertEquals(100, book.getStock());
    }

    @Test
    @DisplayName("Book 연관관계 엔티티 테스트")
    void bookRelationEntitiesTest() {
        Book book = new Book();
        Author author = Author.builder().authorName("Test Author").build();
        Category category = new Category("Test Category", null);
        Publisher publisher = Publisher.builder().publisherName("Test Publisher").build();
        Tag tag = Tag.builder().name("Test Tag").build();

        BookAuthor bookAuthor = BookAuthor.builder().book(book).author(author).build();
        BookCategory bookCategory = BookCategory.builder().book(book).category(category).build();
        BookPublisher bookPublisher = BookPublisher.builder().book(book).publisher(publisher).build();
        BookTag bookTag = BookTag.builder().book(book).tag(tag).build();
        BookImage bookImage = BookImage.builder().book(book).imageUrl("test.jpg").build();
        BookLike bookLike = BookLike.builder().book(book).userId(1L).build();

        assertEquals(book, bookAuthor.getBook());
        assertEquals(author, bookAuthor.getAuthor());
        assertEquals(book, bookCategory.getBook());
        assertEquals(category, bookCategory.getCategory());
        assertEquals(book, bookPublisher.getBook());
        assertEquals(publisher, bookPublisher.getPublisher());
        assertEquals(book, bookTag.getBook());
        assertEquals(tag, bookTag.getTag());
        assertEquals(book, bookImage.getBook());
        assertEquals("test.jpg", bookImage.getImageUrl());
        assertEquals(book, bookLike.getBook());
        assertEquals(1L, bookLike.getUserId());
    }

    @Test
    @DisplayName("Book 재고 변경 메소드 테스트")
    void bookStockMethodTest() {
        Book book = Book.builder().stock(10).build();
        book.setStock(5);
        assertEquals(5, book.getStock());

        book.setStock(8);
        assertEquals(8, book.getStock());
    }
}
