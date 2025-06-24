package shop.wannab.book_service.book.repository;

public interface BookRedisRepository {
    Integer getBookStock(long bookId);
}