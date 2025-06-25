package shop.wannab.book_service.aladin.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AladinErrorCode {
    ALDIN_API_ERROR(HttpStatus.BAD_GATEWAY, "ALD001", "알라딘 API 호출 실패"),
    TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "ALD002", "알라딘 응답 지연");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
