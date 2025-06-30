package shop.wannab.book_service.global.exception;

import org.aspectj.bridge.Message;

public interface ErrorCode {
    int getStatus();
    int getCode();
    String getMessage();
}
