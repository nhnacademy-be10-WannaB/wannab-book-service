package shop.wannab.book_service.book.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
import shop.wannab.book_service.book.service.BookLikeQueryService;
import shop.wannab.book_service.book.service.BookLikeService;

@Service
@RequiredArgsConstructor
@Transactional
public class BookLikeServiceImpl implements BookLikeService {

    private final BookLikeRepository bookLikeRepository;
    private final BookRepository bookRepository;
    private final BookLikeQueryService bookLikeQueryService;


    //도서 좋아요 등록
    @Override
    @Transactional
    public void createBookLike(Long bookId, Long userId){
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()->  new BookApiException(BookErrorCode.BOOK_NOT_FOUND));

        if(bookLikeQueryService.isBookLiked(bookId,userId)){
            throw new BookApiException(BookErrorCode.DUPLICATE_BOOK_LIKE);
        }

        BookLike bookLike = BookLike.builder()
                .book(book)
                .userId(userId)
                .build();
        bookLikeRepository.save(bookLike);
    }

    //도서 좋아요 취소
    @Override
    @Transactional
    public void deleteBookLike(Long bookId, Long userId){
        if(!bookLikeQueryService.isBookLiked(bookId,userId)){
            throw new BookApiException(BookErrorCode.BOOK_LIKE_NOT_FOUND);
        }
        bookLikeRepository.deleteByUserIdAndBook_BookId(userId,bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookLikeListResponse> getLikedBooks(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookLikeRepository.findBooksLikedByUserId(userId, pageable);
        return books.map(BookLikeListResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotBooksListResponse> getHotBooksList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<Book> books = bookLikeRepository.findTop10BooksByLikeCount(pageable);

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
