package shop.wannab.book_service.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.wannab.book_service.entity.dto.OrderItemValidationError;
import shop.wannab.book_service.exception.UnavailableOrderBooksException;

import java.util.List;

@RestControllerAdvice
public class BookControllerAdvice {

    @ExceptionHandler(UnavailableOrderBooksException.class)
    public ResponseEntity<List<OrderItemValidationError>> handleUnavailableOrderBooksException(UnavailableOrderBooksException ex) {
        return ResponseEntity.badRequest().body(ex.getErrors());
    }

}