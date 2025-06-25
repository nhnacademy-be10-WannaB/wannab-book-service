package shop.wannab.book_service.global.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.wannab.book_service.aladin.exception.AladinApiException;
import shop.wannab.book_service.aladin.exception.AladinErrorCode;
import shop.wannab.book_service.aladin.exception.response.AladinErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AladinApiException.class)
    public ResponseEntity<AladinErrorResponse> handleExternalApiException(AladinApiException ex) {
        AladinErrorCode code = ex.getErrorCode();
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(new AladinErrorResponse(code));
    }
}
