package shop.wannab.book_service.book.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.wannab.book_service.book.entity.Book;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookLikeListResponse {

    private Long bookId;
    private String title;
    private List<String> imageUrl;
    private List<String> authors;
    private boolean liked;

    public static BookLikeListResponse from(Book book) {
        return BookLikeListResponse.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .imageUrl(book.getBookImages().stream()
                        .map(img -> img.getImageUrl())
                        .collect(Collectors.toList()))
                .authors(book.getBookAuthors().stream()
                        .map(bookAuthor -> bookAuthor.getAuthor().getAuthorName())
                        .collect(Collectors.toList()))
                .liked(true)
                .build();
    }
}
