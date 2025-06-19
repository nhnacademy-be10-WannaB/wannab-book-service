package shop.wannab.book_service.repository;

public interface BookRedisRepository {
    Integer getBookStock(long bookId);
}