package shop.wannab.book_service.book.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.wannab.book_service.book.controller.request.BookCreateRequest;
import shop.wannab.book_service.book.controller.request.BookUpdateRequest;
import shop.wannab.book_service.book.controller.response.BookListResponse;

public interface AdminBookService {
    Page<BookListResponse> getBookList(String keyword, Pageable pageable);
    void createBook(BookCreateRequest request);
    void updateBook(Long bookId, BookUpdateRequest request);
    void deleteBook(Long bookId);

}
