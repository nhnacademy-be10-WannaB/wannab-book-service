package shop.wannab.book_service.book.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.entity.BookLike;
import shop.wannab.book_service.book.exception.BookApiException;
import shop.wannab.book_service.book.repository.BookLikeRepository;
import shop.wannab.book_service.book.repository.BookRepository;
import shop.wannab.book_service.book.service.impl.BookLikeServiceImpl;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("ci")
class BookLikeServiceImplTest {

    @Spy
    @InjectMocks
    private BookLikeServiceImpl bookLikeService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookLikeRepository bookLikeRepository;

    @Mock
    private BookLikeQueryService bookLikeQueryService;

    @Test
    @DisplayName("도서 좋아요 등록 - 유효한 도서 및 사용자")
    void createBookLike_validBookAndUser_savesBookLike() {
        Long bookId = 1L;
        Long userId = 2L;
        Book book = new Book();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookLikeQueryService.isBookLiked(bookId, userId)).thenReturn(false);

        bookLikeService.createBookLike(bookId, userId);

        verify(bookLikeRepository).save(any(BookLike.class));
    }

    @Test
    @DisplayName("도서 좋아요 등록 - 도서를 찾을 수 없음")
    void createBookLike_bookNotFound_throwsException() {
        Long bookId = 1L;
        Long userId = 2L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(BookApiException.class, () -> bookLikeService.createBookLike(bookId, userId));
    }

    @Test
    @DisplayName("도서 좋아요 등록 - 이미 좋아요")
    void createBookLike_alreadyLiked_throwsException() {
        Long bookId = 1L;
        Long userId = 2L;
        Book book = new Book();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookLikeQueryService.isBookLiked(bookId, userId)).thenReturn(true);

        assertThrows(BookApiException.class, () -> bookLikeService.createBookLike(bookId, userId));
    }

    @Test
    @DisplayName("도서 좋아요 취소 - 좋아요된 도서")
    void deleteBookLike_likedBook_deletesSuccessfully() {
        Long bookId = 1L;
        Long userId = 2L;

        when(bookLikeQueryService.isBookLiked(bookId, userId)).thenReturn(true);

        bookLikeService.deleteBookLike(bookId, userId);

        verify(bookLikeRepository).deleteByUserIdAndBook_BookId(userId, bookId);
    }

    @Test
    @DisplayName("도서 좋아요 취소 - 좋아요되지 않은 도서")
    void deleteBookLike_notLiked_throwsException() {
        Long bookId = 1L;
        Long userId = 2L;

        when(bookLikeQueryService.isBookLiked(bookId, userId)).thenReturn(false);

        assertThrows(BookApiException.class, () -> bookLikeService.deleteBookLike(bookId, userId));
    }
}
