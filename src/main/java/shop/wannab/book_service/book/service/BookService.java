package shop.wannab.book_service.book.service;

import shop.wannab.book_service.book.controller.response.BookDetailResponse;

public interface BookService {
    BookDetailResponse getBookDetail(Long bookId);
    void createBookLike(Long bookId, Long userId);
    void deleteBookLike(Long bookId, Long userId);
    Boolean isBookLiked(Long bookId, Long userId);
}
