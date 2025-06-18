package shop.wannab.book_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.wannab.book_service.entity.dto.OrderItemListDto;
import shop.wannab.book_service.service.BookService;


@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    protected final BookService bookService;

    @PostMapping("/validate-order-items")
    public void validateOrderItems(@RequestBody OrderItemListDto orderItemListDto) {
        bookService.validateOrderItems(orderItemListDto);
    }
}