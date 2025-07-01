package shop.wannab.book_service.book.controller.response;

import lombok.Builder;
import lombok.Getter;
import shop.wannab.book_service.book.entity.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class BookDetailResponse {
    private Long bookId;
    private String title;
    private String description;
    private LocalDate publicationDate;
    private Integer originPrice;
    private Integer salesPrice;
    private Integer stock;
    private Boolean status;
    private String bookChapter;
    private List<String> authorNames;
    private List<String> publisherNames;
    private List<String> tagNames;
    private List<String> imageUrls;


    public static BookDetailResponse of(Book book) {
        return BookDetailResponse.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .description(book.getDescription())
                .publicationDate(book.getPublicationDate())
                .originPrice(book.getOriginPrice())
                .salesPrice(book.getSalesPrice())
                .stock(book.getStock())
                .status(book.isStatus())
                .bookChapter(book.getBookChapter())
                .authorNames(book.getBookAuthors().stream()
                        .map(ba -> ba.getAuthor().getAuthorName())
                        .toList())
                .publisherNames(book.getBookPublishers().stream()
                        .map(bp -> bp.getPublisher().getPublisherName())
                        .toList())
                .tagNames(book.getBookTags().stream()
                        .map(bt -> bt.getTag().getTagName())
                        .toList())
                .imageUrls(book.getBookImages().stream()
                        .map(BookImage::getImageUrl)
                        .toList())
                .build();
    }

}
