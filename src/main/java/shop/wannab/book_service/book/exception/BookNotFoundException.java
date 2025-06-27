package shop.wannab.book_service.book.exception;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException() {
        super("도서를 찾을 수 없습니다.");
    }

    public BookNotFoundException(String message) {
        super(message);
    }
}
