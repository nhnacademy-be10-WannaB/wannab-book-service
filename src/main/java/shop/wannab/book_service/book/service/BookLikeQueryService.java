package shop.wannab.book_service.book.service;

public interface BookLikeQueryService {
    boolean isBookLiked(Long bookId, Long userId);
}
