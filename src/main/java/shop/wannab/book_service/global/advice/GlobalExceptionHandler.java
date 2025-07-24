package shop.wannab.book_service.global.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.wannab.book_service.aladin.exception.AladinApiException;
import shop.wannab.book_service.aladin.exception.AladinErrorCode;
import shop.wannab.book_service.aladin.exception.response.AladinErrorResponse;
import shop.wannab.book_service.global.exception.BaseException;
import shop.wannab.book_service.global.exception.ErrorCode;
import shop.wannab.book_service.global.response.ApiResponse;
import shop.wannab.book_service.global.response.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AladinApiException.class)
    public ResponseEntity<AladinErrorResponse> handleExternalApiException(AladinApiException ex) {
        AladinErrorCode code = ex.getErrorCode();
        log.warn("statusCode: {}, errorCode : {}, message : {}", code.getHttpStatus(), ex.getErrorCode(), ex.getMessage());
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(new AladinErrorResponse(code));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<?>> handleBaseException(BaseException ex) {
        ErrorCode code = ex.getErrorCode();
        log.warn("statusCode: {}, errorCode : {}, message : {}", code.getStatus(), ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.error(new ErrorResponse(code.getStatus(), code.getCode(), code.getMessage())));
    }
}
