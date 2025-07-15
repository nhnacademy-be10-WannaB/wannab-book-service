
package shop.wannab.book_service.book.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookExceptionTest {

    @Test
    @DisplayName("BookApiException 생성 테스트")
    void bookApiExceptionCreation() {
        BookApiException exception = new BookApiException(BookErrorCode.BOOK_NOT_FOUND);
        assertEquals(BookErrorCode.BOOK_NOT_FOUND, exception.getErrorCode());
        assertEquals("도서를 찾을수 없습니다.", exception.getErrorCode().getMessage());
    }

    @Test
    @DisplayName("BookErrorCode Enum 값 테스트")
    void bookErrorCodeValues() {
        for (BookErrorCode code : BookErrorCode.values()) {
            assertNotNull(code.getMessage());
        }
    }

    @Test
    @DisplayName("OrderItemValidationError 생성 테스트")
    void orderItemValidationErrorCreation() {
        OrderItemValidationError error = new OrderItemValidationError(1L,"test message");
        assertEquals("test message", error.getMessage());
        assertEquals(1L, error.getBookId());
    }
}
