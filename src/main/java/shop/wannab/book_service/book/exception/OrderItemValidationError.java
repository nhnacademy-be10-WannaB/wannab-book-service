package shop.wannab.book_service.book.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderItemValidationError {
    private long bookId;
    private String message;
}
