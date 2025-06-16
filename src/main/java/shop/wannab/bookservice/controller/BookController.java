// src/main/java/shop/wannab/bookservice/controller/BookController.java
package shop.wannab.bookservice.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.wannab.bookservice.dto.BookDto;
import shop.wannab.bookservice.service.BookService;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private static final Logger log = LoggerFactory.getLogger(BookController.class);
    private final BookService bookService;

    @GetMapping("/{id}")
    public ResponseEntity<BookDto.Response> get(@PathVariable Long id) {
        log.info("GET /api/books/{} 호출", id);
        BookDto.Response resp = bookService.getBook(id);
        return ResponseEntity.ok(resp);
    }
}
