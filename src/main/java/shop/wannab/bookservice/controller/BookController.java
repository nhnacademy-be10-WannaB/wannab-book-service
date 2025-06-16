package shop.wannab.bookservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.wannab.bookservice.dto.BookDto;
import shop.wannab.bookservice.exception.ErrorResponse;
import shop.wannab.bookservice.service.BookService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping("/{identifier}")
    public ResponseEntity<?> getBook(@PathVariable String identifier) {
        BookDto.Response resp = bookService.findByIdOrIsbn(identifier);

        if (resp == null) {
            ErrorResponse err = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(404)
                    .error("Not Found")
                    .message("Book not found: " + identifier)
                    .path("/api/books/" + identifier)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }

        return ResponseEntity.ok(resp);
    }
}
