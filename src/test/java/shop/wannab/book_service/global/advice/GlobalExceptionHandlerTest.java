package shop.wannab.book_service.global.advice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import shop.wannab.book_service.aladin.exception.AladinApiException;
import shop.wannab.book_service.aladin.exception.AladinErrorCode;
import shop.wannab.book_service.aladin.exception.response.AladinErrorResponse;
import shop.wannab.book_service.book.exception.BookErrorCode;
import shop.wannab.book_service.global.exception.BaseException;
import shop.wannab.book_service.global.response.ApiResponse;
import shop.wannab.book_service.global.response.ErrorResponse;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("AladinApiException 처리")
    void handleExternalApiException() {
        Throwable throwable = new Throwable();
        AladinApiException ex = new AladinApiException(AladinErrorCode.ALDIN_API_ERROR,throwable);
        ResponseEntity<AladinErrorResponse> response = globalExceptionHandler.handleExternalApiException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(AladinErrorCode.ALDIN_API_ERROR.getCode());
        assertThat(response.getBody().message()).isEqualTo(AladinErrorCode.ALDIN_API_ERROR.getMessage());
    }

    @Test
    @DisplayName("BaseException 처리")
    void handleBaseException() {
        BaseException ex = new BaseException(BookErrorCode.BOOK_NOT_FOUND);
        ResponseEntity<ApiResponse<?>> response = globalExceptionHandler.handleBaseException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(shop.wannab.book_service.global.response.ResponseStatus.ERROR);
        assertThat(response.getBody().getError()).isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = response.getBody().getError();
        assertThat(errorResponse.getCode()).isEqualTo(BookErrorCode.BOOK_NOT_FOUND.getCode());
        assertThat(errorResponse.getMessage()).isEqualTo(BookErrorCode.BOOK_NOT_FOUND.getMessage());
    }
}
