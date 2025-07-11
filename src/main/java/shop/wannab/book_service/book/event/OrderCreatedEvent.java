package shop.wannab.book_service.book.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.wannab.book_service.book.dto.OrderItemListDto;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class OrderCreatedEvent {
    private Long orderId;
    private OrderItemListDto itemListDto;
}
