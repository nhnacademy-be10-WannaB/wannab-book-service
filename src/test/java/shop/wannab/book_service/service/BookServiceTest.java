package shop.wannab.book_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import shop.wannab.book_service.book.service.BookService;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.dto.CartItem;
import shop.wannab.book_service.book.dto.OrderItemListDto;
import shop.wannab.book_service.exception.UnavailableOrderBooksException;
import shop.wannab.book_service.book.repository.BookRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookServiceTest {

//    private OrderItemListDto orderItemListDto;
//
//    @MockBean
//    private BookRepository bookRepository;
//
//    @Autowired
//    private BookService bookService;
//
//    @BeforeEach()
//    void setup() {
//        Book abcBook = new Book(1L, "abcBook", 10000, 7000, 3, true); //재고부족
//        Book dinoBook = new Book(2L, "dinoBook", 50000, 40000, 20, true); // 구매 가능
//        Book suspendedBook = new Book(3L, "suspendedBook", 3000, 2000, 7, false); // 판매중단
//
//        // Book 1L: 재고 부족
//        when(bookRepository.getBookStock(abcBook.getBookId())).thenReturn(abcBook.getStock());
//        when(bookRepository.existsByBookIdAndIsOnSaleTrue(abcBook.getBookId())).thenReturn(abcBook.isStatus());
//
//        // Book 2L: 구매 가능
//        when(bookRepository.getBookStock(dinoBook.getBookId())).thenReturn(dinoBook.getStock());
//        when(bookRepository.existsByBookIdAndIsOnSaleTrue(dinoBook.getBookId())).thenReturn(dinoBook.isStatus());
//
//        // Book 3L: 판매중단
//        when(bookRepository.getBookStock(suspendedBook.getBookId())).thenReturn(suspendedBook.getStock());
//        when(bookRepository.existsByBookIdAndIsOnSaleTrue(suspendedBook.getBookId())).thenReturn(suspendedBook.isStatus());
//
//        List<CartItem> cartItems = List.of(
//                new CartItem(abcBook.getBookId(), 5),
//                new CartItem(dinoBook.getBookId(), 10),
//                new CartItem(suspendedBook.getBookId(), 1));
//
//        orderItemListDto = new OrderItemListDto(cartItems);
//    }
//
//
//    @Test
//    void validateOrderItems() {
//        UnavailableOrderBooksException ex = assertThrows(UnavailableOrderBooksException.class, () -> {
//            bookService.validateOrderItems(orderItemListDto);
//        });
//        assertEquals(2, ex.getErrors().size());
//        assertEquals("재고가 부족합니다.", ex.getErrors().get(0).getMessage());
//
//    }

}