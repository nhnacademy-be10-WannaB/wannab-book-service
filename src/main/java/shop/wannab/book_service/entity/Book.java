package shop.wannab.book_service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private long bookId;
    private int stock;
    private boolean isOnSale;
}
