package shop.wannab.book_service.book.controller.response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.entity.BookAuthor;
import shop.wannab.book_service.book.entity.BookImage;
import shop.wannab.book_service.book.entity.BookPublisher;
import shop.wannab.book_service.book.entity.BookTag;

@Data
@Builder
public class BookListResponse {
    private Long bookId;
    private String title;
    private String description;
    private LocalDate publicationDate;
    private Integer originPrice;
    private String isbn;
    private Integer stock;
    private Boolean status;
    private List<String> authorNames;
    private List<String> publisherNames;
    private List<String> imageUrls;
    private List<String> tagNames;

    public static BookListResponse empty(Long id) {
        return BookListResponse.builder()
                .bookId(id)
                .authorNames(new ArrayList<>())
                .publisherNames(new ArrayList<>())
                .tagNames(new ArrayList<>())
                .imageUrls(new ArrayList<>())
                .build();
    }

    public static BookListResponse from(Book book) {
        return BookListResponse.builder()
                .bookId(book.getBookId())
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
                .tagNames(book.getBookTags().stream()
                        .map(BookTag::getTag)
                        .filter(tag -> tag != null)
                        .map(tag -> tag.getName())
                        .toList())
                .imageUrls(book.getBookImages().stream()
                        .map(BookImage::getImageUrl)
                        .filter(url -> url != null && !url.isBlank())
                        .toList())
                .build();
    }

}
