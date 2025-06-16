// AladinBook.java
package shop.wannab.bookservice.service.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
public class AladinBook {
    private String title;
    private String description;
    private LocalDate publicationDate;
    private String isbn;
    private Integer originPrice;
    private Integer salePrice;
    private boolean wrappable;

    public AladinBook(String title,
                      String description,
                      String pubDate,
                      String isbn,
                      Integer originPrice,
                      Integer salePrice,
                      boolean wrappable) {
        this.title = title;
        this.description = description;
        this.publicationDate = LocalDate.parse(pubDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
        this.isbn = isbn;
        this.originPrice = originPrice;
        this.salePrice = salePrice;
        this.wrappable = wrappable;
    }
}
