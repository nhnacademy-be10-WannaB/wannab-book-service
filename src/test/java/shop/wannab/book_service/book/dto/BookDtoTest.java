
package shop.wannab.book_service.book.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookDtoTest {

    @Test
    @DisplayName("BookIdListDto 테스트")
    void bookIdListDtoTest() {
        List<Long> ids = List.of(1L, 2L, 3L);
        BookIdListDto dto = new BookIdListDto(ids);
        assertEquals(ids, dto.getBookIds());
    }

    @Test
    @DisplayName("BookIdTitlePriceDto 테스트")
    void bookIdTitlePriceDtoTest() {
        BookIdTitlePriceDto dto = new BookIdTitlePriceDto(1L, "Test Title", 10000);
        assertEquals(1L, dto.getBookId());
        assertEquals("Test Title", dto.getTitle());
        assertEquals(10000, dto.getSalesPrice());
    }

    @Test
    @DisplayName("BookIdTitlePriceListDto 테스트")
    void bookIdTitlePriceListDtoTest() {
        List<BookIdTitlePriceDto> list = Collections.singletonList(new BookIdTitlePriceDto(1L, "Test Title", 10000));
        BookIdTitlePriceListDto dto = new BookIdTitlePriceListDto(list);
        assertEquals(list, dto.getIdTitlePriceDtos());
    }

    @Test
    @DisplayName("CartItem 테스트")
    void cartItemTest() {
        CartItem item = new CartItem(1L, 5);
        assertEquals(1L, item.getBookId());
        assertEquals(5, item.getQuantity());
    }

    @Test
    @DisplayName("OrderBookInfo 테스트")
    void orderBookInfoTest() {
        OrderBookInfo info = new OrderBookInfo(1L, "Test Title", 10000, 10000, 2, "");
        assertEquals(1L, info.getBookId());
        assertEquals("Test Title", info.getTitle());
        assertEquals(10000, info.getOriginPrice());
        assertEquals(2, info.getQuantity());
    }

    @Test
    @DisplayName("OrderBookInfoListDto 테스트")
    void orderBookInfoListDtoTest() {
        List<OrderBookInfo> list = Collections.singletonList(new OrderBookInfo(1L, "Test Title", 10000, 10000, 2, ""));
        OrderBookInfoListDto dto = new OrderBookInfoListDto(list);
        assertEquals(list, dto.getOrderBookInfos());
    }

    @Test
    @DisplayName("OrderItemListDto 테스트")
    void orderItemListDtoTest() {
        List<CartItem> list = Collections.singletonList(new CartItem(1L, 5));
        OrderItemListDto dto = new OrderItemListDto(list);
        assertEquals(list, dto.getOrderItems());
    }
}
