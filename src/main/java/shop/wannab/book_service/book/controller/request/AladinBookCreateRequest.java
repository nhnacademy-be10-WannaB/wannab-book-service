package shop.wannab.book_service.book.controller.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.global.deserializer.GreaterThanSeparatedToListDeserializer;

public record AladinBookCreateRequest(
        @NotBlank String title,
        List<String> authors,
        List<String> publishers,
        @NotNull LocalDate publishedDate,
        @NotBlank String isbn,
        @NotNull Integer price,
        @NotBlank String description,
        String thumbnail,
        @JsonDeserialize(using = GreaterThanSeparatedToListDeserializer.class)
        List<String> category,
        Integer stock,
        String bookChapter,
        Boolean status
) {

    public Book toEntity(){
        return Book.builder()
                .title(this.title)
                .description(this.description)
                .publicationDate(this.publishedDate)
                .isbn(this.isbn)
                .originPrice(this.price)
                .salesPrice(this.price)
                .stock(this.stock != null ? this.stock : 0)
                .status(this.status != null ? this.status : true)
                .bookChapter(this.bookChapter != null ? this.bookChapter : "")
                .build();
    }
}
