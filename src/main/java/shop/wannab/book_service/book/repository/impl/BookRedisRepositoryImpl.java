package shop.wannab.book_service.book.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import shop.wannab.book_service.book.repository.BookRedisRepository;

import static shop.wannab.book_service.global.constants.Constants.BOOK_STOCK_KEY;

@Repository
@RequiredArgsConstructor
public class BookRedisRepositoryImpl implements BookRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Integer getBookStock(long bookId) {
        Integer stock = (Integer) redisTemplate.opsForHash().get(BOOK_STOCK_KEY, bookId);
        return stock;
    }

    @Override
    public void saveOrUpdateBookStock(long bookId, int stock) {
        redisTemplate.opsForHash().put(BOOK_STOCK_KEY, bookId, stock);
    }

    @Override
    public void deleteBookStock(long bookId) {
        redisTemplate.opsForHash().delete(BOOK_STOCK_KEY, bookId);
    }

    @Override
    public void decreaseBookStock(long bookId, int amount) {
        redisTemplate.opsForHash().increment(BOOK_STOCK_KEY, bookId, amount);
    }

    @Override
    public void increaseBookStock(long bookId, int amount) {
        redisTemplate.opsForHash().increment(BOOK_STOCK_KEY, bookId, -amount);
    }

}
