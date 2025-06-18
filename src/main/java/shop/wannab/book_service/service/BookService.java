package shop.wannab.book_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.wannab.book_service.entity.dto.CartItem;
import shop.wannab.book_service.entity.dto.OrderItemListDto;
import shop.wannab.book_service.entity.dto.OrderItemValidationError;
import shop.wannab.book_service.exception.UnavailableOrderBooksException;
import shop.wannab.book_service.repository.BookRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
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

            if (!bookRepository.existsByBookIdAndIsOnSaleTrue(bookId)) {
                errors.add(new OrderItemValidationError(bookId, "판매중인 상품이 아닙니다."));
            }
        }

        if (!errors.isEmpty()) {
            throw new UnavailableOrderBooksException(errors);
        }
    }
}