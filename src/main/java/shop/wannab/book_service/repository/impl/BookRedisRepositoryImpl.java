package shop.wannab.book_service.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import shop.wannab.book_service.repository.BookRedisRepository;

import static shop.wannab.book_service.constants.Constants.BOOK_STOCK_KEY;

@Repository
@RequiredArgsConstructor
public class BookRedisRepositoryImpl implements BookRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public Integer getBookStock(long bookId) {
        Integer stock = (Integer) redisTemplate.opsForHash().get(BOOK_STOCK_KEY, bookId);
        return stock;
    }
}
