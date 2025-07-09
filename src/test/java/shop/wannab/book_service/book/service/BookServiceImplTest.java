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
import shop.wannab.book_service.book.entity.BookLike;
import shop.wannab.book_service.book.exception.BookApiException;
import shop.wannab.book_service.book.repository.BookLikeRepository;
import shop.wannab.book_service.book.repository.BookRepository;
import shop.wannab.book_service.book.service.impl.BookServiceImpl;
import shop.wannab.book_service.category.entity.Category;

import java.time.LocalDate;
import java.util.Optional;

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

    @Test
    @DisplayName("도서 좋아요 등록 - 유효한 도서 및 사용자")
    void createBookLike_validBookAndUser_savesBookLike() {
        Long bookId = 1L;
        Long userId = 2L;
        Book book = new Book();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(bookLikeRepository.existsByUserIdAndBook_BookId(userId, bookId)).thenReturn(false);

        bookService.createBookLike(bookId, userId);

        verify(bookLikeRepository).save(any(BookLike.class));
    }

    @Test
    @DisplayName("도서 좋아요 등록 - 도서를 찾을 수 없음")
    void createBookLike_bookNotFound_throwsException() {
        Long bookId = 1L;
        Long userId = 2L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(BookApiException.class, () -> bookService.createBookLike(bookId, userId));
    }

    @Test
    @DisplayName("도서 좋아요 등록 - 이미 좋아요")
    void createBookLike_alreadyLiked_throwsException() {
        Long bookId = 1L;
        Long userId = 2L;
        Book book = new Book();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(bookLikeRepository.existsByUserIdAndBook_BookId(userId, bookId)).thenReturn(true);

        assertThrows(BookApiException.class, () -> bookService.createBookLike(bookId, userId));
    }

    @Test
    @DisplayName("도서 좋아요 취소 - 좋아요된 도서")
    void deleteBookLike_likedBook_deletesSuccessfully() {
        Long bookId = 1L;
        Long userId = 2L;

        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(bookLikeRepository.existsByUserIdAndBook_BookId(userId, bookId)).thenReturn(true);

        bookService.deleteBookLike(bookId, userId);

        verify(bookLikeRepository).deleteByUserIdAndBook_BookId(userId, bookId);
    }

    @Test
    @DisplayName("도서 좋아요 취소 - 좋아요되지 않은 도서")
    void deleteBookLike_notLiked_throwsException() {
        Long bookId = 1L;
        Long userId = 2L;

        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(bookLikeRepository.existsByUserIdAndBook_BookId(userId, bookId)).thenReturn(false);

        assertThrows(BookApiException.class, () -> bookService.deleteBookLike(bookId, userId));
    }

    @Test
    @DisplayName("도서 좋아요 여부 조회 - 좋아요됨")
    void isBookLiked_liked_returnsTrue() {
        Long bookId = 1L;
        Long userId = 2L;

        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(bookLikeRepository.existsByUserIdAndBook_BookId(userId, bookId)).thenReturn(true);

        assertTrue(bookService.isBookLiked(bookId, userId));
    }

    @Test
    @DisplayName("도서 좋아요 여부 조회 - 좋아요되지 않음")
    void isBookLiked_notLiked_returnsFalse() {
        Long bookId = 1L;
        Long userId = 2L;

        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(bookLikeRepository.existsByUserIdAndBook_BookId(userId, bookId)).thenReturn(false);

        assertFalse(bookService.isBookLiked(bookId, userId));
    }

    @Test
    @DisplayName("도서 좋아요 여부 조회 - 도서를 찾을 수 없음")
    void isBookLiked_bookNotFound_throwsException() {
        Long bookId = 1L;
        Long userId = 2L;

        when(bookRepository.existsById(bookId)).thenReturn(false);

        assertThrows(BookApiException.class, () -> bookService.isBookLiked(bookId, userId));
    }
}
