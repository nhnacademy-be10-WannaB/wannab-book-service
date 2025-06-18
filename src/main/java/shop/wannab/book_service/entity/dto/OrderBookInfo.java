package shop.wannab.book_service.entity.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderBookInfo {
    private long id;
    private String title;
    private int originPrice;
    private int salesPrice;
    private int quantity;
}