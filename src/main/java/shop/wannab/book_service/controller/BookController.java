package shop.wannab.book_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import shop.wannab.book_service.entity.dto.OrderItemListDto;
import shop.wannab.book_service.service.BookService;


@RestController
@RequiredArgsConstructor
public class BookController {
    protected final BookService bookService;

    @PostMapping("/order-books")
    public void validateOrderItems(@RequestBody OrderItemListDto orderItemListDto) {
        bookService.validateOrderItems(orderItemListDto);
    }
}