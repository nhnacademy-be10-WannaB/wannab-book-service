package shop.wannab.book_service.book.controller;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.wannab.book_service.book.controller.response.BookLikeListResponse;
import shop.wannab.book_service.book.controller.response.BookListResponse;
import shop.wannab.book_service.book.controller.response.HotBooksListResponse;
import shop.wannab.book_service.book.dto.BookIdListDto;
import shop.wannab.book_service.book.dto.BookIdTitlePriceListDto;
import shop.wannab.book_service.book.dto.OrderBookInfoListDto;
import shop.wannab.book_service.book.dto.OrderItemListDto;
import shop.wannab.book_service.book.controller.response.BookDetailResponse;
import shop.wannab.book_service.book.service.impl.BookLikeServiceImpl;
import shop.wannab.book_service.book.service.impl.BookServiceImpl;
import shop.wannab.book_service.global.response.ApiResponse;


@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    protected final BookServiceImpl bookServiceImpl;
    private final BookLikeServiceImpl bookLikeService;

    @PostMapping("/validation/primary")
    public void validateOrderItems(@RequestBody OrderItemListDto orderItemListDto) {
        bookServiceImpl.validateOrderItems(orderItemListDto);
    }
    @PostMapping("/for-order")
    OrderBookInfoListDto getOrderBookInfos(@RequestBody OrderItemListDto orderItemListDto) {
        return bookServiceImpl.getOrderBookInfos(orderItemListDto);
    }

    //도서 상세조회
    @GetMapping("/{book-id}")
    public ResponseEntity<ApiResponse<BookDetailResponse>> getBookDetail(@PathVariable("book-id") Long bookId) {
        BookDetailResponse response = bookServiceImpl.getBookDetail(bookId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/simple-info")
    BookIdTitlePriceListDto getBookSimpleInfos(@RequestBody BookIdListDto bookIdListDto) {
        BookIdTitlePriceListDto dto = bookServiceImpl.getBookIdTitlePriceList(bookIdListDto);
        return dto;
    }

    @PostMapping("/decrease-stock")
    public ResponseEntity<Void> decreaseStock(@RequestBody OrderItemListDto orderItemListDto) {
        bookServiceImpl.decreaseRedisStock(orderItemListDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/increase-stock")
    ResponseEntity<Void> increaseStock(@RequestBody OrderItemListDto orderItemListDto) {
        bookServiceImpl.increaseStock(orderItemListDto);
        return ResponseEntity.ok().build();
    }
    // 도서 좋아요 등록
    @PostMapping("/{book-id}/likes")
    public ResponseEntity<ApiResponse<Void>> createBookLike(@PathVariable("book-id") Long bookId,
                                                            @RequestHeader("X-USER-ID") Long userId) {
        bookLikeService.createBookLike(bookId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 도서 좋아요 취소
    @DeleteMapping("/{book-id}/likes")
    public ResponseEntity<ApiResponse<Void>> deleteBookLike(@PathVariable("book-id") Long bookId,
                                                            @RequestHeader("X-USER-ID") Long userId) {
        bookLikeService.deleteBookLike(bookId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 도서 좋아요 여부 조회
    @GetMapping("/{book-id}/likes")
    public ResponseEntity<ApiResponse<Boolean>> isBookLiked(@PathVariable("book-id") Long bookId,
                                                            @RequestHeader("X-USER-ID") Long userId) {
        Boolean isLiked = bookLikeService.isBookLiked(bookId, userId);
        return ResponseEntity.ok(ApiResponse.success(isLiked));
    }

    @GetMapping("/{category-id}/search")
    public ResponseEntity<ApiResponse<Page<BookListResponse>>> searchBooks(@PathVariable("category-id") Long categoryId,
                            @PageableDefault(size = 20) Pageable pageable){
        Page<BookListResponse> books = bookServiceImpl.searchBooks(categoryId,pageable);
        return ResponseEntity.ok(ApiResponse.success(books));
    }


    @GetMapping("/liked-books")
    public ResponseEntity<ApiResponse<Page<BookLikeListResponse>>> getLikedBooks(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<BookLikeListResponse> bookLikes = bookLikeService.getLikedBooks(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(bookLikes));
    }
  
    @PostMapping("/names")
    public ResponseEntity<Map<Long, String>> getBookNames(@RequestBody List<Long> bookIds) {
        return ResponseEntity.ok(bookServiceImpl.findBookNamesByIds(bookIds));
    }

    @GetMapping("/hot-books")
    public ResponseEntity<ApiResponse<List<HotBooksListResponse>>> getHotBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        List<HotBooksListResponse> hotBooksListResponse = bookLikeService.getHotBooksList(page,size);
        return ResponseEntity.ok(ApiResponse.success(hotBooksListResponse));
    }
}