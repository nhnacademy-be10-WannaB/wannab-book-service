package shop.wannab.book_service.book.dto;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import shop.wannab.book_service.book.controller.request.AladinBookCreateRequest;
import shop.wannab.book_service.book.controller.request.BookCreateRequest;
import shop.wannab.book_service.book.controller.request.BookUpdateRequest;

public record BookIndexDocument(
        String bookId,
        String title,
        List<String> authors,
        List<String> publishers,
        LocalDate publicationDate,
        String description,
        List<String> categories,
        List<String> tags,
        String thumbnailUrl,
        boolean status,
        int originPrice,
        int salesPrice
) {
    public static BookIndexDocument from(AladinBookCreateRequest req, String bookId) {
        List<String> categories = Optional.ofNullable(req.category())
                .map(list -> list.size() > 2 ? list.subList(0, 2) : list)
                .orElse(List.of());

        List<String> tags = List.of();

        return new BookIndexDocument(
                bookId,
                req.title(),
                req.authors(),
                req.publishers(),
                req.publishedDate(),
                req.description(),
                categories,
                tags,
                req.thumbnail(),
                Boolean.TRUE.equals(req.status()),
                req.price(),
                req.price()
        );
    }

    public static BookIndexDocument from(BookCreateRequest req, String bookId) {
        List<String> categories = Optional.ofNullable(req.getCategories())
                .map(list -> list.size() > 2 ? list.subList(0, 2) : list)
                .orElse(List.of());

        return new BookIndexDocument(
                bookId,
                req.getTitle(),
                req.getAuthors(),
                req.getPublishers(),
                req.getPublicationDate(),
                req.getDescription(),
                categories,
                req.getBookTags(),
                req.getBookImages().getFirst(),
                req.isStatus(),
                req.getOriginPrice(),
                req.getSalesPrice()
        );
    }

    public static BookIndexDocument from(BookUpdateRequest req, String bookId) {
        List<String> categories = Optional.ofNullable(req.getCategories())
                .map(list -> list.size() > 2 ? list.subList(0, 2) : list)
                .orElse(List.of());

        return new BookIndexDocument(
                bookId,
                req.getTitle(),
                req.getAuthors(),
                req.getPublishers(),
                req.getPublicationDate(),
                req.getDescription(),
                categories,
                Optional.ofNullable(req.getBookTags()).orElse(List.of()),
                req.getBookImages() != null && !req.getBookImages().isEmpty()
                        ? req.getBookImages().getFirst() : null,
                req.isStatus(),
                req.getOriginPrice(),
                req.getSalesPrice() != null ? req.getSalesPrice() : req.getOriginPrice()
        );
    }
}
