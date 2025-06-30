package shop.wannab.book_service.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.book_service.book.dto.BookIdListDto;
import shop.wannab.book_service.book.dto.BookIdTitlePriceDto;
import shop.wannab.book_service.book.dto.BookIdTitlePriceListDto;
import shop.wannab.book_service.book.dto.BookIdTitlePriceProjection;
import shop.wannab.book_service.book.dto.BookInfoForOrderProjection;
import shop.wannab.book_service.book.dto.CartItem;
import shop.wannab.book_service.book.dto.OrderBookInfo;
import shop.wannab.book_service.book.dto.OrderBookInfoListDto;
import shop.wannab.book_service.book.dto.OrderItemListDto;
import shop.wannab.book_service.book.dto.response.BookDetailResponse;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.entity.BookLike;
import shop.wannab.book_service.book.exception.BookApiException;
import shop.wannab.book_service.book.exception.BookErrorCode;
import shop.wannab.book_service.book.exception.OrderItemValidationError;
import shop.wannab.book_service.book.repository.BookLikeRepository;
import shop.wannab.book_service.global.exception.UnavailableOrderBooksException;
import shop.wannab.book_service.book.repository.BookRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final BookLikeRepository bookLikeRepository;

    @Transactional(readOnly = true)
    public void validateOrderItems(OrderItemListDto orderItemListDto) {

        List<CartItem> orderItems = orderItemListDto.getOrderItems();
        List<OrderItemValidationError> errors = new ArrayList<>();

        for (CartItem orderItem : orderItems) {
            long bookId = orderItem.getBookId();
            int quantity = orderItem.getQuantity();

            Integer stock = bookRepository.getBookStock(bookId);
            if (stock == null) {
                errors.add(new OrderItemValidationError(bookId, "해당 상품을 찾을 수 없습니다."));
                continue;
            }

            if (quantity > stock) {
                errors.add(new OrderItemValidationError(bookId, "재고가 부족합니다."));
                continue;
            }

            if (!bookRepository.existsByBookIdAndStatusTrue(bookId)) {
                errors.add(new OrderItemValidationError(bookId, "판매중인 상품이 아닙니다."));
            }
        }

        if (!errors.isEmpty()) {
            throw new UnavailableOrderBooksException(errors);
        }
    }

    @Transactional(readOnly = true)
    public OrderBookInfoListDto getOrderBookInfos(OrderItemListDto orderItemListDto) {
        List<CartItem> orderItems = orderItemListDto.getOrderItems();
        List<Long> ids = orderItems.stream().map(CartItem::getBookId).collect(Collectors.toList());

        List<BookInfoForOrderProjection> bookInfoList = bookRepository.findByBookIdIn(ids);

        Map<Long, BookInfoForOrderProjection> bookInfoMap = new HashMap<>();

        for (BookInfoForOrderProjection projection : bookInfoList) {
            bookInfoMap.put(projection.getBookId(), projection);
        }

        List<OrderBookInfo> orderBookInfos = new ArrayList<>();

        for (CartItem item : orderItems) {
            BookInfoForOrderProjection bookInfoForOrderProjection = bookInfoMap.get(item.getBookId());
            orderBookInfos.add(new OrderBookInfo(bookInfoForOrderProjection, item.getQuantity()));
        }
        return new OrderBookInfoListDto(orderBookInfos);
    }



    @Transactional(readOnly = true)
    public BookIdTitlePriceListDto getBookIdTitlePriceList(BookIdListDto bookIdListDto) {
        List<Long> bookIds = bookIdListDto.getBookIds();
        List<BookIdTitlePriceDto> idTitlePriceDtos = new ArrayList<>();
        List<BookIdTitlePriceProjection> projections = bookRepository.queryByBookIdIn(bookIds);

        for (BookIdTitlePriceProjection projection : projections) {
            idTitlePriceDtos.add(new BookIdTitlePriceDto(projection.getBookId(), projection.getTitle(), projection.getSalesPrice()));
        }
        return new BookIdTitlePriceListDto(idTitlePriceDtos);
    }

    //재고감소
    @Transactional
    public void decreaseStock(OrderItemListDto orderItemListDto) {

        List<CartItem> orderItems = orderItemListDto.getOrderItems();
        List<OrderItemValidationError> errors = new ArrayList<>();

        for (CartItem orderItem : orderItems) {
            long bookId = orderItem.getBookId();
            int quantity = orderItem.getQuantity();

            Integer stock = bookRepository.getBookStock(bookId);
            if (stock == null) {
                errors.add(new OrderItemValidationError(bookId, "해당 상품을 찾을 수 없습니다."));
                continue;
            }

            if (quantity > stock) {
                errors.add(new OrderItemValidationError(bookId, "재고가 부족합니다."));
                continue;
            }

            if (!bookRepository.existsByBookIdAndStatusTrue(bookId)) {
                errors.add(new OrderItemValidationError(bookId, "판매중인 상품이 아닙니다."));
                continue;
            }

            bookRepository.decreaseBookStock(bookId, quantity);

        }
        if (!errors.isEmpty()) {
            throw new UnavailableOrderBooksException(errors);
        }

    }

    // 도서 상세 조회
    @Transactional(readOnly = true)
    public BookDetailResponse getBookDetail(Long bookId){
        Book book = bookRepository.findBookDetail(bookId);

        return BookDetailResponse.of(book);
    }

    //도서 좋아요 등록
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
    public void deleteBookLike(Long bookId, Long userId){
        if(!isBookLiked(bookId,userId)){
            throw new BookApiException(BookErrorCode.BOOK_LIKE_NOT_FOUND);
        }
        bookLikeRepository.deleteByUserIdAndBook_BookId(userId,bookId);
    }

    //도서 좋아요 여부 조회
    @Transactional(readOnly = true)
    public Boolean isBookLiked(Long bookId, Long userId){
        boolean bookExists = bookRepository.existsById(bookId);
        if (!bookExists) {
            throw new BookApiException(BookErrorCode.BOOK_NOT_FOUND);
        }
        return bookLikeRepository.existsByUserIdAndBook_BookId(userId,bookId);
    }

    @Transactional
    public void increaseStock(OrderItemListDto orderItemListDto) {
        List<CartItem> orderItems = orderItemListDto.getOrderItems();
        List<OrderItemValidationError> errors = new ArrayList<>();

        for (CartItem orderItem : orderItems) {
            long bookId = orderItem.getBookId();
            int quantity = orderItem.getQuantity();

            Book book = bookRepository.findById(bookId).orElse(null);

            if (book == null) {
                errors.add(new OrderItemValidationError(bookId, "해당 상품을 찾을 수 없습니다."));
                continue;
            }
            bookRepository.increaseBookStock(bookId, quantity);
        }
        if (!errors.isEmpty()) {
            throw new UnavailableOrderBooksException(errors);
        }

    }
}