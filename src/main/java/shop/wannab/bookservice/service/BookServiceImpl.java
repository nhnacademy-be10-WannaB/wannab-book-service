package shop.wannab.bookservice.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.bookservice.domain.Book;
import shop.wannab.bookservice.dto.BookDto;
import shop.wannab.bookservice.exception.BookNotFoundException;
import shop.wannab.bookservice.repository.BookRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {
    private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);
    private final BookRepository bookRepository;

    @Override
    public BookDto.Response getBook(Long bookId) {
        log.info("조회 요청 bookId={}", bookId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow();
        return toResponse(book);
    }

    private BookDto.Response toResponse(Book b) {
        return new BookDto.Response(
                b.getBookId(),
                b.getTitle(),
                b.getDescription(),
                b.getPublicationDate(),
                b.getIsbn(),
                b.getOriginPrice(),
                b.getSalePrice(),
                b.isWrappable(),
                b.getAmount(),
                b.getStatus().name()
        );
    }
}
