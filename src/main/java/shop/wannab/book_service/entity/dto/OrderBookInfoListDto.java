package shop.wannab.book_service.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderBookInfoListDto {
    private List<OrderBookInfo> orderBookInfos;
}