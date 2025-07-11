package shop.wannab.book_service.book.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.book_service.book.dto.CartItem;
import shop.wannab.book_service.book.dto.OrderItemListDto;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.repository.BookRepository;
import shop.wannab.book_service.book.service.impl.BookServiceImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static shop.wannab.book_service.global.config.RabbitConfig.ORDER_CREATED_QUEUE;

@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final BookServiceImpl bookService;
    private final Map<Long, Integer> stockBuffer = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = ORDER_CREATED_QUEUE)
    public void handleOrderCreated(String message) throws JsonProcessingException {
        OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);

        OrderItemListDto dto = event.getItemListDto();
        bookService.decreaseRedisStock(dto);
        for (CartItem item : dto.getOrderItems()) {
            stockBuffer.merge(item.getBookId(), item.getQuantity(), Integer::sum);
        }
        scheduler.schedule(this::decreaseDbBookStock, 3, TimeUnit.SECONDS);
    }

    @Transactional
    public void decreaseDbBookStock() {
        for (Map.Entry<Long, Integer> entry : new HashMap<>(stockBuffer).entrySet()) {
            Long bookId = entry.getKey();
            Integer quantity = entry.getValue();
            bookService.decreaseDbStock(bookId, quantity);
        }
        stockBuffer.clear();
    }


}
