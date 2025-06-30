package shop.wannab.book_service.book.repository;

public interface BookRedisRepository {
    Integer getBookStock(long bookId);

    void decreaseBookStock(long bookId, int amount);

    void increaseBookStock(long bookId, int amount);
}