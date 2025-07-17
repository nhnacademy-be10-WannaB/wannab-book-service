package shop.wannab.book_service.book.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.book_service.book.dto.CartItem;
import shop.wannab.book_service.book.dto.OrderItemListDto;
import shop.wannab.book_service.book.service.impl.BookServiceImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final BookServiceImpl bookService;
    private final Map<Long, Integer> stockBuffer = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    @RabbitListener(queues = "${queue.order-created.book}")
    public void handleOrderCreated(OrderItemListDto dto) throws JsonProcessingException {

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
