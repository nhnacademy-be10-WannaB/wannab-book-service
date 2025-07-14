package shop.wannab.book_service.book.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.book_service.book.controller.response.BookLikeListResponse;
import shop.wannab.book_service.book.controller.response.HotBooksListResponse;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.entity.BookImage;
import shop.wannab.book_service.book.entity.BookLike;
import shop.wannab.book_service.book.exception.BookApiException;
import shop.wannab.book_service.book.exception.BookErrorCode;
import shop.wannab.book_service.book.repository.BookLikeRepository;
import shop.wannab.book_service.book.repository.BookRepository;
import shop.wannab.book_service.book.service.BookLikeService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookLikeServiceImpl implements BookLikeService {

    private final BookLikeRepository bookLikeRepository;
    private final BookRepository bookRepository;

    //도서 좋아요 등록
    @Override
    public void createBookLike(Long bookId, Long userId){
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()->  new BookApiException(BookErrorCode.BOOK_NOT_FOUND));

        if(isBookLiked(bookId,userId)){
            throw new BookApiException(BookErrorCode.DUPLICATE_BOOK_LIKE);
        }

        BookLike bookLike = BookLike.builder()
                .book(book)
                .userId(userId)
                .build();
        bookLikeRepository.save(bookLike);;
    }
    //도서 좋아요 취소
    @Override
    public void deleteBookLike(Long bookId, Long userId){
        if(!isBookLiked(bookId,userId)){
            throw new BookApiException(BookErrorCode.BOOK_LIKE_NOT_FOUND);
        }
        bookLikeRepository.deleteByUserIdAndBook_BookId(userId,bookId);
    }

    //도서 좋아요 여부 조회
    @Override
    @Transactional(readOnly = true)
    public Boolean isBookLiked(Long bookId, Long userId){
        boolean bookExists = bookRepository.existsById(bookId);
        if (!bookExists) {
            throw new BookApiException(BookErrorCode.BOOK_NOT_FOUND);
        }
        return bookLikeRepository.existsByUserIdAndBook_BookId(userId,bookId);
    }

    @Override
    public Page<BookLikeListResponse> getLikedBooks(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookLikeRepository.findBooksLikedByUserId(userId, pageable);
        return books.map(BookLikeListResponse::from);
    }

    @Override
    public List<HotBooksListResponse> getHotBooksList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<Book> books = bookLikeRepository.findTop10BooksByLikeCount(pageable); // 여전히 List<Book>

        return books.stream()
                .map(book -> new HotBooksListResponse(
                        book.getBookId(),
                        book.getTitle(),
                        book.getDescription(),
                        book.getBookImages().stream()
                                .map(BookImage::getImageUrl)
                                .collect(Collectors.toList())
                ))
                .toList();
    }
}
