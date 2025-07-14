package shop.wannab.book_service.book.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.wannab.book_service.book.controller.response.BookDetailResponse;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.entity.BookCategory;
import shop.wannab.book_service.book.repository.BookLikeRepository;
import shop.wannab.book_service.book.repository.BookRepository;
import shop.wannab.book_service.book.service.impl.BookServiceImpl;
import shop.wannab.book_service.category.entity.Category;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("ci")
class BookServiceImplTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookLikeRepository bookLikeRepository;

    private Book createMockBookWithCategories() {
        Category parent = new Category("소설", null);
        Category child = new Category("스릴러", parent);

        Book book = Book.builder()
                .bookId(1L)
                .title("테스트 도서")
                .description("설명")
                .publicationDate(LocalDate.now())
                .isbn("123456789")
                .originPrice(10000)
                .salesPrice(9000)
                .stock(10)
                .status(true)
                .build();

        BookCategory cat1 = BookCategory.builder()
                .book(book)
                .category(parent)
                .build();

        BookCategory cat2 = BookCategory.builder()
                .book(book)
                .category(child)
                .build();

        book.getBookCategories().add(cat1);
        book.getBookCategories().add(cat2);

        return book;
    }

    @Test
    @DisplayName("도서 상세 조회 - 유효한 도서 ID")
    void getBookDetail_validBookId_returnsBookDetailResponse() {
        Long bookId = 1L;
        Book mockBook = createMockBookWithCategories();

        when(bookRepository.findBookDetail(bookId)).thenReturn(mockBook);

        BookDetailResponse response = bookService.getBookDetail(bookId);

        assertEquals("소설>스릴러", response.getCategories());
    }
}
