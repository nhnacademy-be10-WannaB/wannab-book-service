package shop.wannab.book_service.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.wannab.book_service.book.controller.request.BookCreateRequest;
import shop.wannab.book_service.book.controller.request.BookUpdateRequest;
import shop.wannab.book_service.book.controller.response.BookListResponse;
import shop.wannab.book_service.book.service.AdminBookService;
import shop.wannab.book_service.global.response.ApiResponse;

@RestController
@RequestMapping("/api/admin/books")
@RequiredArgsConstructor
public class AdminBookController {

    private final AdminBookService adminBookService;

    /**
     * 도서 목록 리스트
     * @param pageable 페이징 객체
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookListResponse>>> getBooks(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<BookListResponse> bookList = adminBookService.getBookList(pageable);
        return ResponseEntity.ok(ApiResponse.success(bookList));
    }

    /**
     * 도서 등록
     * @param request 도서 등록 정보
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createBook(@RequestBody @Valid BookCreateRequest request){
        adminBookService.createBook(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 도서 수정
     * @param bookId 도서 PK
     * @param request 도서 수정 정보
     */
    @PutMapping("/{book-id}")
    public ResponseEntity<ApiResponse<Void>> updateBook(@PathVariable("book-id") Long bookId,
                                                        @RequestBody @Valid BookUpdateRequest request){
        adminBookService.updateBook(bookId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 도서 삭제
     * @param bookId 도서 PK
     */
    @DeleteMapping("/{book-id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable("book-id") Long bookId){
        adminBookService.deleteBook(bookId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
