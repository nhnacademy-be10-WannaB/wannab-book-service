package shop.wannab.book_service.book.dto.request;

import lombok.Builder;
import lombok.Getter;
import shop.wannab.book_service.book.entity.Book;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class BookCreateRequest {
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
