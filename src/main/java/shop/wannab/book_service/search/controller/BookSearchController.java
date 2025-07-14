package shop.wannab.book_service.search.controller;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.wannab.book_service.search.domain.BookSearchField;
import shop.wannab.book_service.search.dto.response.SearchResultWithSectionResponse;
import shop.wannab.book_service.search.dto.response.BookSearchResult;
import shop.wannab.book_service.search.service.BookSearchService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookSearchController {

    private final BookSearchService bookSearchService;

    @GetMapping("/search")
    public ResponseEntity<List<BookSearchResult>> search(@RequestParam String keyword) throws IOException {
        return ResponseEntity.ok(bookSearchService.searchBooksByKeyword(keyword));
    }

    @GetMapping("/search/total")
    public ResponseEntity<List<SearchResultWithSectionResponse>> searchMulti(
            @RequestParam String keyword,
            @RequestParam(name = "field", required = false) Set<BookSearchField> fields) throws IOException {

        Set<BookSearchField> targets = (Objects.isNull(fields) || fields.isEmpty()) ? BookSearchField.basicSet() : fields;
        return ResponseEntity.ok(bookSearchService.multiSearchBooks(keyword, targets));
    }
}
