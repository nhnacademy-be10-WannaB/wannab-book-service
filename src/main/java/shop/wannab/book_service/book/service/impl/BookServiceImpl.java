package shop.wannab.book_service.book.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.book_service.book.controller.response.BookListResponse;
import shop.wannab.book_service.book.dto.BookIdListDto;
import shop.wannab.book_service.book.dto.BookIdTitlePriceDto;
import shop.wannab.book_service.book.dto.BookIdTitlePriceListDto;
import shop.wannab.book_service.book.repository.projection.BookIdTitlePriceProjection;
import shop.wannab.book_service.book.repository.projection.BookInfoForOrderProjection;
import shop.wannab.book_service.book.dto.CartItem;
import shop.wannab.book_service.book.dto.OrderBookInfo;
import shop.wannab.book_service.book.dto.OrderBookInfoListDto;
import shop.wannab.book_service.book.dto.OrderItemListDto;
import shop.wannab.book_service.book.controller.response.BookDetailResponse;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.entity.BookCategory;
import shop.wannab.book_service.book.entity.BookLike;
import shop.wannab.book_service.book.exception.BookApiException;
import shop.wannab.book_service.book.exception.BookErrorCode;
import shop.wannab.book_service.book.exception.OrderItemValidationError;
import shop.wannab.book_service.book.repository.BookLikeRepository;
import shop.wannab.book_service.book.service.BookService;
import shop.wannab.book_service.category.entity.Category;
import shop.wannab.book_service.global.exception.UnavailableOrderBooksException;
import shop.wannab.book_service.book.repository.BookRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

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
        List<Long> existingBookIds = bookRepository.findBookIdByBookIdIn(ids);
        orderItems.removeIf(cartItem -> !existingBookIds.contains(cartItem.getBookId()));

        List<BookInfoForOrderProjection> bookInfoList = bookRepository.findByBookIdIn(existingBookIds);

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
    public void decreaseRedisStock(OrderItemListDto orderItemListDto) {

        List<CartItem> orderItems = orderItemListDto.getOrderItems();

        for (CartItem orderItem : orderItems) {
            long bookId = orderItem.getBookId();
            int quantity = orderItem.getQuantity();
            bookRepository.decreaseBookRedisStock(bookId, quantity);
        }
    }

    @Transactional
    public void decreaseDbStock(long bookId, int quantity) {
        Book book = bookRepository.findById(bookId).orElseThrow();
        book.setStock(book.getStock() - quantity);
    }

    // 도서 상세 조회
    @Override
    @Transactional(readOnly = true)
    public BookDetailResponse getBookDetail(Long bookId) {
        Book book = bookRepository.findBookDetail(bookId);

        List<BookCategory> bookCategories = book.getBookCategories();

        String parentCategory = null;
        String childCategory = null;

        for (BookCategory bookCategory : bookCategories) {
            Category category = bookCategory.getCategory();

            if (category.getParent() == null) {
                parentCategory = category.getName();
            } else {
                childCategory = category.getName();
            }
        }

        String categories;

        if (parentCategory != null && childCategory != null) {
            categories = parentCategory + ">" + childCategory;
        } else if (parentCategory != null) {
            categories = parentCategory;
        } else if (childCategory != null) {
            categories = childCategory;
        } else {
            categories = "";
        }
        return BookDetailResponse.of(book, categories);
    }

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

    @Override
    @Transactional(readOnly = true)
    public Page<BookListResponse> searchBooks(Long categoryId, Pageable pageable){
        Page<Book> books = bookRepository.findByCategoryId(categoryId,pageable);
        return books.map(BookListResponse::from);
    }
}