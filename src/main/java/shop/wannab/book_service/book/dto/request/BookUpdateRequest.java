package shop.wannab.book_service.book.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateRequest {
    private String title;
    private String description;
    private LocalDate publicationDate;
    private Integer originPrice;
    private Integer stock;
    private String bookChapter;
    private String isbn;
    private boolean status;

    private List<String> categories;
    private List<String> authors;
    private List<String> publishers;
}
