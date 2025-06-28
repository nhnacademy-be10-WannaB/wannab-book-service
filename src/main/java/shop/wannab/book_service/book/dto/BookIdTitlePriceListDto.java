package shop.wannab.book_service.book.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor

public class BookIdTitlePriceListDto {
    private List<BookIdTitlePriceDto> idTitlePriceDtos = new ArrayList<>();


}
