package shop.wannab.book_service.aladin.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * 알라딘에서 검색을 통해 받는 래핑된 응답 객체
 *
 * @author hunmin
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SearchResponse(
    String title,
    Integer totalResults,
    Integer startIndex,
    Integer ItemsPerPage,
    String query,
    String searchCategoryId,
    String searchCategoryName,

    @JsonProperty("item")
    List<BookItem> items
) {
}
