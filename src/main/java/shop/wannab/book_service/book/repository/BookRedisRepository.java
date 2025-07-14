package shop.wannab.book_service.book.repository;

public interface BookRedisRepository {
    Integer getBookStock(long bookId);
    void saveOrUpdateBookStock(long bookId, int stock);
    void deleteBookStock(long bookId);
    void decreaseBookRedisStock(long bookId, int amount);
    void increaseBookStock(long bookId, int amount);
}