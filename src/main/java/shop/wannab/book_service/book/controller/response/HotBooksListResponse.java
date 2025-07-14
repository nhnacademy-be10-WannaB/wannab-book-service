package shop.wannab.book_service.book.controller.response;

import java.util.List;

public record HotBooksListResponse (
        Long bookId,
        String title,
        String description,
        List<String> imageUrls
){
}
