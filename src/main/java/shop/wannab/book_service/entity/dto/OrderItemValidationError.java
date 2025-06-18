package shop.wannab.book_service.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderItemValidationError {
    private long bookId;
    private String message;
}
