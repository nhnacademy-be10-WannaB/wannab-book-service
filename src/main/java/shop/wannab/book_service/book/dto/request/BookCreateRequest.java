package shop.wannab.book_service.book.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
    private String title;
    private String description;
    private LocalDate publicationDate;
    private Integer originPrice;
    private Integer stock;
    private String bookChapter;
    private String isbn;
    private boolean status;

    @JsonDeserialize(using = CommaSeparatedToListDeserializer.class)
    private List<String> categories;

    @JsonDeserialize(using = CommaSeparatedToListDeserializer.class)
    private List<String> authors;

    @JsonDeserialize(using = CommaSeparatedToListDeserializer.class)
    private List<String> publishers;

    @JsonDeserialize(using = CommaSeparatedToListDeserializer.class)
    private List<String> bookImages;


    public Book toEntity(){
        return Book.builder()
                .title(this.title)
                .description(this.description)
                .publicationDate(this.publicationDate)
                .isbn(this.isbn)
                .originPrice(this.originPrice)
                .stock(this.stock)
                .status(this.status)
                .bookChapter(this.bookChapter)
                .build();
    }
}
