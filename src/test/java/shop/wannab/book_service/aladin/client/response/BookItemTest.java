package shop.wannab.book_service.aladin.client.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("BookItem DTO 역직렬화 테스트")
class BookItemTest {

    ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
            .build();

    @Test
    @DisplayName("BookItem은 JSON에서 올바르게 역직렬화된다")
    void bookItemDeserializationTest() throws Exception {
        String json = """
                    {
                        "title": "테스트 도서",
                        "author": "작가1,작가2",
                        "pubDate": "2025-07-23",
                        "description": "설명입니다.",
                        "isbn": "123456",
                        "isbn13": "1234567890123",
                        "itemId": "999",
                        "priceSales": 9000,
                        "priceStandard": 10000,
                        "mallType": "BOOK",
                        "mileage": 500,
                        "cover": "http://image.com/image.jpg",
                        "categoryId": "123",
                        "categoryName": "개발",
                        "publisher": "출판사1,출판사2",
                        "unknownField": "이건 무시되어야 함"
                    }
                """;

        BookItem item = mapper.readValue(json, BookItem.class);

        assertThat(item.title()).isEqualTo("테스트 도서");
        assertThat(item.author()).containsExactly("작가1", "작가2");
        assertThat(item.publisher()).containsExactly("출판사1", "출판사2");
        assertThat(item.mileage()).isEqualTo(500);
        assertThat(item.isbn13()).isEqualTo("1234567890123");
    }
}