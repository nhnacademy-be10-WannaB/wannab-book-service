package shop.wannab.book_service.book.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.wannab.book_service.book.exception.BookApiException;
import shop.wannab.book_service.book.repository.BookLikeRepository;
import shop.wannab.book_service.book.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("ci")
class BookLikeQueryServiceTest {

    @InjectMocks
    private BookLikeQueryServiceImpl bookLikeQueryService;

    @Mock private BookRepository bookRepository;
    @Mock private BookLikeRepository bookLikeRepository;

    @Test
    @DisplayName("도서 좋아요 여부 조회 - 도서 존재하고 좋아요된 경우")
    void isBookLiked_liked_returnsTrue() {
        // given
        Long bookId = 1L;
        Long userId = 2L;

        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(bookLikeRepository.existsByUserIdAndBook_BookId(userId, bookId)).thenReturn(true);

        // when
        boolean result = bookLikeQueryService.isBookLiked(bookId, userId);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("도서 좋아요 여부 조회 - 도서 존재하고 좋아요되지 않은 경우")
    void isBookLiked_notLiked_returnsFalse() {
        // given
        Long bookId = 1L;
        Long userId = 2L;

        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(bookLikeRepository.existsByUserIdAndBook_BookId(userId, bookId)).thenReturn(false);

        // when
        boolean result = bookLikeQueryService.isBookLiked(bookId, userId);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("도서 좋아요 여부 조회 - 도서가 존재하지 않으면 예외 발생")
    void isBookLiked_bookNotFound_throwsException() {
        // given
        Long bookId = 1L;
        Long userId = 2L;

        when(bookRepository.existsById(bookId)).thenReturn(false);

        // when & then
        assertThrows(BookApiException.class,
                () -> bookLikeQueryService.isBookLiked(bookId, userId));
    }
}