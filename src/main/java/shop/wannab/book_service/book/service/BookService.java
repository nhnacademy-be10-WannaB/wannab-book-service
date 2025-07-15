package shop.wannab.book_service.book.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.wannab.book_service.book.controller.response.BookDetailResponse;
import shop.wannab.book_service.book.controller.response.BookListResponse;

public interface BookService {
    BookDetailResponse getBookDetail(Long bookId);
    Page<BookListResponse> searchBooks(Long categoryId, Pageable pageable);
    void decreaseRedisStock(shop.wannab.book_service.book.dto.OrderItemListDto orderItemListDto);
}
