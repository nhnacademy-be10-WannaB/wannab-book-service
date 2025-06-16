package shop.wannab.bookservice.service;

import shop.wannab.bookservice.dto.BookDto;

public interface BookService {
    BookDto.Response getBook(Long bookId);
}
