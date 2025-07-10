package shop.wannab.book_service.book.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.wannab.book_service.book.repository.projection.BookInfoForOrderProjection;

@Getter
@Setter
@NoArgsConstructor
public class OrderBookInfo {
    private long bookId;
    private String title;
    private int originPrice;
    private int salesPrice;
    private int quantity;
    private String thumbnailUrl;

    public OrderBookInfo(BookInfoForOrderProjection info, int quantity) {
        this.bookId = info.getBookId();
        this.title = info.getTitle();
        this.originPrice = info.getOriginPrice();
        this.salesPrice = info.getSalesPrice();
        this.quantity = quantity;
        this.thumbnailUrl = info.getBookImages().get(0).getImageUrl();
    }
}