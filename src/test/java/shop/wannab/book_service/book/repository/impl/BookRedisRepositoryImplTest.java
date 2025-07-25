package shop.wannab.book_service.book.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static shop.wannab.book_service.global.constants.Constants.BOOK_STOCK_KEY;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

@ExtendWith(MockitoExtension.class)
class BookRedisRepositoryImplTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @InjectMocks
    private BookRedisRepositoryImpl bookRedisRepository;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    @DisplayName("도서 재고 저장 및 조회 - 성공")
    void saveAndGetBookStock_success() {
        long bookId = 1L;
        int stock = 100;

        bookRedisRepository.saveOrUpdateBookStock(bookId, stock);
        verify(hashOperations).put(BOOK_STOCK_KEY, bookId, stock);

        when(hashOperations.get(BOOK_STOCK_KEY, bookId)).thenReturn(stock);
        Integer retrievedStock = bookRedisRepository.getBookStock(bookId);
        assertThat(retrievedStock).isEqualTo(stock);
    }

    @Test
    @DisplayName("도서 재고 조회 - 존재하지 않는 도서")
    void getBookStock_notFound() {
        long bookId = 999L;
        when(hashOperations.get(BOOK_STOCK_KEY, bookId)).thenReturn(null);

        Integer retrievedStock = bookRedisRepository.getBookStock(bookId);
        assertThat(retrievedStock).isNull();
    }

    @Test
    @DisplayName("도서 재고 감소 - 성공")
    void decreaseBookRedisStock_success() {
        long bookId = 1L;
        int initialStock = 100;
        when(hashOperations.increment(BOOK_STOCK_KEY, bookId, -10L)).thenReturn((long) (initialStock - 10));

        bookRedisRepository.decreaseBookRedisStock(bookId, 10);
        verify(hashOperations).increment(BOOK_STOCK_KEY, bookId, -10L);
    }

    @Test
    @DisplayName("도서 재고 증가 - 성공")
    void increaseBookStock_success() {
        long bookId = 1L;
        int initialStock = 100;
        when(hashOperations.increment(BOOK_STOCK_KEY, bookId, 10L)).thenReturn((long) (initialStock + 10));

        bookRedisRepository.increaseBookStock(bookId, 10);
        verify(hashOperations).increment(BOOK_STOCK_KEY, bookId, 10L);
    }

    @Test
    @DisplayName("도서 재고 삭제 - 성공")
    void deleteBookStock_success() {
        long bookId = 1L;
        bookRedisRepository.deleteBookStock(bookId);
        verify(hashOperations).delete(BOOK_STOCK_KEY, bookId);
    }
}
