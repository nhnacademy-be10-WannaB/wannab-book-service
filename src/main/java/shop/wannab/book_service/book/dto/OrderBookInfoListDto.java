package shop.wannab.book_service.book.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class OrderBookInfoListDto {
    private List<OrderBookInfo> orderBookInfos;
}