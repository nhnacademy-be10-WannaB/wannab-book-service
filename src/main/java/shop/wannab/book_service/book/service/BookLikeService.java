package shop.wannab.book_service.book.service;

import java.util.List;
import org.springframework.data.domain.Page;
import shop.wannab.book_service.book.controller.response.BookLikeListResponse;
import shop.wannab.book_service.book.controller.response.HotBooksListResponse;

public interface BookLikeService {
    void createBookLike(Long bookId, Long userId);
    void deleteBookLike(Long bookId, Long userId);
    Page<BookLikeListResponse> getLikedBooks(Long userId, int page, int size);
    List<HotBooksListResponse> getHotBooksList(int page, int size);
}
