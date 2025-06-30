package shop.wannab.book_service.book.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.wannab.book_service.global.deserializer.CommaSeparatedToListDeserializer;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookUpdateRequest {
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
}
