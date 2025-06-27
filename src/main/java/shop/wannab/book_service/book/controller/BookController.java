package shop.wannab.book_service.book.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shop.wannab.book_service.book.dto.OrderBookInfoListDto;
import shop.wannab.book_service.book.dto.OrderItemListDto;
import shop.wannab.book_service.book.dto.response.BookDetailResponse;
import shop.wannab.book_service.book.repository.BookLikeRepository;
import shop.wannab.book_service.book.service.BookService;


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
    public BookDetailResponse getBookDetail(@PathVariable("book-id") Long bookId){
        BookDetailResponse response= bookService.getBookDetail(bookId);
        return response;
    }

    // 도서 좋아요 등록
    // @PostMapping("/{book-id}/likes")
    // public void addBookLike(@PathVariable("book-id") Long bookId, @RequestParam Long userId){}

    // 도서 좋아요 취소
    // @DeleteMapping("/{book-id}/likes")
    // public void removeBookLike(){}

    // 도서 좋아요 여부 조회
    // @GetMapping("/{book-id}/likes")
    //  public void isBookLiked(){}

}