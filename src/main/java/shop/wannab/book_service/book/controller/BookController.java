package shop.wannab.book_service.book.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.wannab.book_service.book.dto.BookIdListDto;
import shop.wannab.book_service.book.dto.BookIdTitlePriceListDto;
import shop.wannab.book_service.book.dto.OrderBookInfoListDto;
import shop.wannab.book_service.book.dto.OrderItemListDto;
import shop.wannab.book_service.book.dto.response.BookDetailResponse;
import shop.wannab.book_service.book.service.BookService;
import shop.wannab.book_service.global.response.ApiResponse;


@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    protected final BookService bookService;

    @PostMapping("/validation/primary")
    public void validateOrderItems(@RequestBody OrderItemListDto orderItemListDto) {
        bookService.validateOrderItems(orderItemListDto);
    }
    @PostMapping("/for-order")
    OrderBookInfoListDto getOrderBookInfos(@RequestBody OrderItemListDto orderItemListDto) {
        return bookService.getOrderBookInfos(orderItemListDto);
    }

    //도서 상세조회
    @GetMapping("/{book-id}")
    public ResponseEntity<ApiResponse<BookDetailResponse>> getBookDetail(@PathVariable("book-id") Long bookId) {
        BookDetailResponse response = bookService.getBookDetail(bookId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/simple-info")
    BookIdTitlePriceListDto getBookSimpleInfos(@RequestBody BookIdListDto bookIdListDto) {
        BookIdTitlePriceListDto dto = bookService.getBookIdTitlePriceList(bookIdListDto);
        return dto;
    }

    @PostMapping("/decrease-stock")
    public ResponseEntity<Void> decreaseStock(@RequestBody OrderItemListDto orderItemListDto) {
        bookService.decreaseStock(orderItemListDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/increase-stock")
    ResponseEntity<Void> increaseStock(@RequestBody OrderItemListDto orderItemListDto) {
        bookService.increaseStock(orderItemListDto);
        return ResponseEntity.ok().build();
    }
    // 도서 좋아요 등록
    @PostMapping("/{book-id}/likes")
    public ResponseEntity<ApiResponse<Void>> createBookLike(@PathVariable("book-id") Long bookId,
                                                            @RequestHeader("X-USER-ID") Long userId) {
        bookService.createBookLike(bookId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 도서 좋아요 취소
    @DeleteMapping("/{book-id}/likes")
    public ResponseEntity<ApiResponse<Void>> deleteBookLike(@PathVariable("book-id") Long bookId,
                                                            @RequestHeader("X-USER-ID") Long userId) {
        bookService.deleteBookLike(bookId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 도서 좋아요 여부 조회
    @GetMapping("/{book-id}/likes")
    public ResponseEntity<ApiResponse<Boolean>> isBookLiked(@PathVariable("book-id") Long bookId,
                                                            @RequestHeader("X-USER-ID") Long userId) {
        Boolean isLiked = bookService.isBookLiked(bookId, userId);
        return ResponseEntity.ok(ApiResponse.success(isLiked));
    }
}