package shop.wannab.book_service.book.dto.response;

import lombok.Builder;
import lombok.Getter;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.entity.BookAuthor;
import shop.wannab.book_service.book.entity.BookImage;
import shop.wannab.book_service.book.entity.BookPublisher;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class BookListResponse {
    private String title;
    private String description;
    private LocalDate publicationDate;
    private Integer originPrice;
    private String isbn;
    private Integer stock;
    private boolean status;
    private List<String> authorNames;
    private List<String> publisherNames;
    private List<String> imageUrls;

    public static BookListResponse from(Book book) {
        return BookListResponse.builder()
                .title(book.getTitle())
                .description(book.getDescription())
                .publicationDate(book.getPublicationDate())
                .originPrice(book.getOriginPrice())
                .isbn(book.getIsbn())
                .stock(book.getStock())
                .status(book.isStatus())
                .authorNames(book.getBookAuthors().stream()
                        .map(BookAuthor::getAuthor)
                        .filter(author -> author != null)
                        .map(author -> author.getAuthorName())
                        .toList())
                .publisherNames(book.getBookPublishers().stream()
                        .map(BookPublisher::getPublisher)
                        .filter(publisher -> publisher != null)
                        .map(publisher -> publisher.getPublisherName())
                        .toList())
                .imageUrls(book.getBookImages().stream()
                        .map(BookImage::getImageUrl)
                        .filter(url -> url != null && !url.isBlank())
                        .toList())
                .build();
    }

}
