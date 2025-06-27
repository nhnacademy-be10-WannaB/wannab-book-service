package shop.wannab.book_service.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import shop.wannab.book_service.book.exception.OrderItemValidationError;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class UnavailableOrderBooksException extends RuntimeException {
    private final List<OrderItemValidationError> errors;

}

