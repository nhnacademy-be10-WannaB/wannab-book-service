package shop.wannab.bookservice.exception;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long bookId) {
        super("이 도서는 존재하지 않습니다 (id=" + bookId + ")");
    }
}
