package shop.wannab.book_service.book.dto;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import shop.wannab.book_service.book.controller.request.AladinBookCreateRequest;

public record BookIndexDocument(
        String bookId,
        String title,
        List<String> authors,
        List<String> publishers,
        LocalDate publicationDate,
        String description,
        List<String> categories,
        String thumbnailUrl,
        boolean status,
        int originPrice,
        int salesPrice
) {
    public static BookIndexDocument from(AladinBookCreateRequest req, String bookId) {
        List<String> categories = Optional.ofNullable(req.category())
                .map(list -> list.size() > 2 ? list.subList(0, 2) : list)
                .orElse(List.of());

        return new BookIndexDocument(
                bookId,
                req.title(),
                req.authors(),
                req.publishers(),
                req.publishedDate(),
                req.description(),
                req.category(),
                req.thumbnail(),
                req.status() != null ? req.status() : true,
                req.price(),
                req.price()
        );
    }
}
