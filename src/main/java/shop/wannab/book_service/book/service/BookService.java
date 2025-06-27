package shop.wannab.book_service.book.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.wannab.book_service.book.dto.BookInfoForOrderProjection;
import shop.wannab.book_service.book.dto.CartItem;
import shop.wannab.book_service.book.dto.OrderBookInfo;
import shop.wannab.book_service.book.dto.OrderBookInfoListDto;
import shop.wannab.book_service.book.dto.OrderItemListDto;
import shop.wannab.book_service.book.dto.response.BookDetailResponse;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.exception.OrderItemValidationError;
import shop.wannab.book_service.global.exception.UnavailableOrderBooksException;
import shop.wannab.book_service.book.repository.BookRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final BookRepository bookRepository;

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

    // 도서 상세 조회
    public BookDetailResponse getBookDetail(Long bookId){
        Book book = bookRepository.findBookDetail(bookId);

        return BookDetailResponse.of(book);
    }
}