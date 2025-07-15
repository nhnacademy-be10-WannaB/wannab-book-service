
package shop.wannab.book_service.book.advice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import shop.wannab.book_service.book.exception.OrderItemValidationError;
import shop.wannab.book_service.global.exception.UnavailableOrderBooksException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookControllerAdviceTest {

    private BookControllerAdvice bookControllerAdvice;

    @BeforeEach
    void setUp() {
        bookControllerAdvice = new BookControllerAdvice();
    }

    @Test
    @DisplayName("UnavailableOrderBooksException 처리 테스트")
    void handleUnavailableOrderBooksException() {
        List<OrderItemValidationError> errors = Collections.singletonList(new OrderItemValidationError(1L,"재고 부족" ));
        UnavailableOrderBooksException exception = new UnavailableOrderBooksException(errors);

        ResponseEntity<List<OrderItemValidationError>> responseEntity = bookControllerAdvice.handleUnavailableOrderBooksException(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(1, responseEntity.getBody().size());
        assertEquals("재고 부족", responseEntity.getBody().get(0).getMessage());
        assertEquals(1L, responseEntity.getBody().get(0).getBookId());
    }
}
