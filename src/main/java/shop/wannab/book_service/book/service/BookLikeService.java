package shop.wannab.book_service.book.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.wannab.book_service.book.controller.response.BookLikeListResponse;
import shop.wannab.book_service.book.controller.response.HotBooksListResponse;

import java.util.List;

public interface BookLikeService {
    void createBookLike(Long bookId, Long userId);
    void deleteBookLike(Long bookId, Long userId);
    Boolean isBookLiked(Long bookId, Long userId);
    Page<BookLikeListResponse> getLikedBooks(Long userId, int page, int size);
    List<HotBooksListResponse> getHotBooksList(int page, int size);
}
