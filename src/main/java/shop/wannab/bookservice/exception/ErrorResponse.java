package shop.wannab.bookservice.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(force = true)  // Jackson용 기본 생성자
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;

    @Builder
    public ErrorResponse(LocalDateTime timestamp,
                         int status,
                         String error,
                         String message,
                         String path) {
        this.timestamp = timestamp;
        this.status    = status;
        this.error     = error;
        this.message   = message;
        this.path      = path;
    }
}
