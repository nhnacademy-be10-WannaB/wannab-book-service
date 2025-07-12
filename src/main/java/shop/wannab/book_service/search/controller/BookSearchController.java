package shop.wannab.book_service.search.controller;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.wannab.book_service.search.dto.response.BookSearchResponse;
import shop.wannab.book_service.search.service.BookSearchService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookSearchController {

    private final BookSearchService bookSearchService;

    @GetMapping("/search")
    public ResponseEntity<List<BookSearchResponse>> search(@RequestParam String keyword) throws IOException {
        return ResponseEntity.ok(bookSearchService.searchBooksByKeyword(keyword));
    }

    @GetMapping("/search/total")
    public ResponseEntity<List<List<BookSearchResponse>>> searchMulti(@RequestParam String keyword) throws IOException {
        return ResponseEntity.ok(bookSearchService.multiSearchBooks(keyword, List.of()));
    }
}
