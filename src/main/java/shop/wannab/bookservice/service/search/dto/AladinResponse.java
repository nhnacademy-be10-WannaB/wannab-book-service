package shop.wannab.bookservice.service.search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AladinResponse {

    /** 실제 JSON의 배열 키가 "item" 이므로 해당 이름으로 매핑 */
    @JsonProperty("item")
    private List<Item> item;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        @JsonProperty("title")
        private String title;

        @JsonProperty("description")
        private String description;

        @JsonProperty("pubDate")
        private String pubDate;

        @JsonProperty("isbn13")
        private String isbn13;

        @JsonProperty("priceStandard")
        private Integer priceStandard;

        @JsonProperty("priceSales")
        private Integer priceSales;

        /** 실제 JSON 필드명이 wrappingAvailable 이므로 이 어노테이션으로 매핑 */
        @JsonProperty("wrappingAvailable")
        private boolean wrappable;
    }
}
