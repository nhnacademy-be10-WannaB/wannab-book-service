package shop.wannab.book_service.aladin.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDate;
import java.util.List;
import shop.wannab.book_service.global.deserializer.CommaSeparatedToListDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookItem(
        String title,
        @JsonDeserialize(using = CommaSeparatedToListDeserializer.class)
        List<String> author,
        LocalDate pubDate,
        String description,
        String isbn,
        String isbn13,
        String itemId,
        Integer priceSales,
        Integer priceStandard,
        String mallType,
        Integer mileage,
        String cover,
        String categoryId,
        String categoryName,
        @JsonDeserialize(using = CommaSeparatedToListDeserializer.class)
        List<String> publisher
) {
}
