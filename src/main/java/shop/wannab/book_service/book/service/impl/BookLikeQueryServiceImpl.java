package shop.wannab.book_service.book.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.book_service.book.exception.BookApiException;
import shop.wannab.book_service.book.exception.BookErrorCode;
import shop.wannab.book_service.book.repository.BookLikeRepository;
import shop.wannab.book_service.book.repository.BookRepository;
import shop.wannab.book_service.book.service.BookLikeQueryService;

@Service
@RequiredArgsConstructor
public class BookLikeQueryServiceImpl implements BookLikeQueryService {

    private final BookRepository bookRepository;
    private final BookLikeRepository bookLikeRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean isBookLiked(Long bookId, Long userId){
        boolean bookExists = bookRepository.existsById(bookId);
        if (!bookExists) {
            throw new BookApiException(BookErrorCode.BOOK_NOT_FOUND);
        }
        return bookLikeRepository.existsByUserIdAndBook_BookId(userId,bookId);
    }
}
