package shop.wannab.book_service.book.controller.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.global.deserializer.CommaSeparatedToListDeserializer;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookCreateRequest {
    @NotBlank private String title;
    @NotBlank private String description;
    @NotNull private LocalDate publicationDate;
    @NotNull private Integer originPrice;
    private Integer salesPrice;
    @NotNull private Integer stock;
    private String bookChapter;
    @NotBlank private String isbn;
    @NotNull private boolean status;

    @JsonDeserialize(using = CommaSeparatedToListDeserializer.class)
    private List<String> categories;

    @JsonDeserialize(using = CommaSeparatedToListDeserializer.class)
    private List<String> authors;

    @JsonDeserialize(using = CommaSeparatedToListDeserializer.class)
    private List<String> publishers;

    @JsonDeserialize(using = CommaSeparatedToListDeserializer.class)
    private List<String> bookImages;

    @JsonDeserialize(using = CommaSeparatedToListDeserializer.class)
    private List<String> bookTags;

    public Book toEntityWithOutAuthorAndPublisher(){
        return Book.builder()
                .title(this.title)
                .description(this.description)
                .publicationDate(this.publicationDate)
                .isbn(this.isbn)
                .originPrice(this.originPrice)
                .salesPrice(this.salesPrice)
                .stock(this.stock)
                .status(this.status)
                .bookChapter(this.bookChapter)
                .build();
    }
}
