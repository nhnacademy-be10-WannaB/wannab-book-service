package shop.wannab.book_service.book.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.wannab.book_service.book.controller.response.BookDetailResponse;
import shop.wannab.book_service.book.controller.response.BookListResponse;

public interface BookService {
    BookDetailResponse getBookDetail(Long bookId);
    void createBookLike(Long bookId, Long userId);
    void deleteBookLike(Long bookId, Long userId);
    Boolean isBookLiked(Long bookId, Long userId);
    Page<BookListResponse> searchBooks(Long categoryId, Pageable pageable);
}
