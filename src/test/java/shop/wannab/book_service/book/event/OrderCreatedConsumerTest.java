package shop.wannab.book_service.book.event;

import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import shop.wannab.book_service.book.dto.CartItem;
import shop.wannab.book_service.book.dto.OrderItemListDto;
import shop.wannab.book_service.book.service.impl.BookServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderCreatedConsumer 단위 테스트")
class OrderCreatedConsumerTest {

    @InjectMocks private OrderCreatedConsumer consumer;

    @Mock private BookServiceImpl bookService;


    @Test
    void handleOrderCreated() throws JsonProcessingException {
        // given
        CartItem item1 = new CartItem(1L, 2);
        CartItem item2 = new CartItem(2L, 3);
        OrderItemListDto dto = new OrderItemListDto(List.of(item1, item2));

        // when
        consumer.handleOrderCreated(dto);

        // then
        verify(bookService).decreaseRedisStock(dto);
    }

    @Test
    void decreaseDbBookStock() {
        ReflectionTestUtils.setField(consumer, "stockBuffer",
                new ConcurrentHashMap<>(Map.of(1L, 2, 2L, 5)));

        // when
        consumer.decreaseDbBookStock();

        // then
        verify(bookService).decreaseDbStock(1L, 2);
        verify(bookService).decreaseDbStock(2L, 5);
    }
}