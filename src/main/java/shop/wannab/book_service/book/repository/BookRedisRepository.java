package shop.wannab.book_service.book.repository;

public interface BookRedisRepository {
    Integer getBookStock(long bookId);
    void saveOrUpdateBookStock(long bookId, int stock);
    void deleteBookStock(long bookId);
}