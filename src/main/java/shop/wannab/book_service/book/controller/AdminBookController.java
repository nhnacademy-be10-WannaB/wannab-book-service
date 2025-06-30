package shop.wannab.book_service.book.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.wannab.book_service.book.dto.request.BookCreateRequest;
import shop.wannab.book_service.book.dto.request.BookUpdateRequest;
import shop.wannab.book_service.book.dto.response.BookListResponse;
import shop.wannab.book_service.book.service.AdminBookService;
import shop.wannab.book_service.global.response.ApiResponse;

@RestController
@RequestMapping("/api/admin/books")
@RequiredArgsConstructor
public class AdminBookController {

    private final AdminBookService adminBookService;

    // 도서 목록 검색(리스트)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookListResponse>>> getBooks(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<BookListResponse> bookList = adminBookService.getBookList(pageable);
        return ResponseEntity.ok(ApiResponse.success(bookList));
    }

    // 도서 등록
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createBook(@RequestBody BookCreateRequest request){
        adminBookService.createBook(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 도서 수정
    @PutMapping("/{book-id}")
    public ResponseEntity<ApiResponse<Void>> updateBook(@PathVariable("book-id") Long bookId,
                                                        @RequestBody BookUpdateRequest request){
        adminBookService.updateBook(bookId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 도서 삭제
    @DeleteMapping("/{book-id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable("book-id") Long bookId){
        adminBookService.deleteBook(bookId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
