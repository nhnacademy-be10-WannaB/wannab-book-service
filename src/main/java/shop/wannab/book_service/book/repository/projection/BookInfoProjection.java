package shop.wannab.book_service.book.repository.projection;

import java.time.LocalDate;

public record BookInfoProjection(
        Long bookId,
        String title,
        String description,
        LocalDate publicationDate,
        Integer originPrice,
        String isbn,
        Integer stock,
        Boolean status,
        String authorName,
        String publisherName,
        String tagName,
        String imageUrl
) {
}
