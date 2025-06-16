package shop.wannab.bookservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class BookDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long bookId;
        private String title;
        private String description;
        private LocalDate publicationDate;
        private String isbn;
        private Integer originPrice;
        private Integer salePrice;
        private boolean wrappable;
        private Integer amount;
        private String status;
    }
}
