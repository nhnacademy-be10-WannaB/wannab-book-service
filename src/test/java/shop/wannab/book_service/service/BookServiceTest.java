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
import shop.wannab.book_service.global.exception.UnavailableOrderBooksException;
import shop.wannab.book_service.book.repository.BookRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookServiceTest {

    private OrderItemListDto orderItemListDto;

    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    @BeforeEach()
    void setup() {
        Book abcBook = Book.builder()
                .bookId(1L)
                .title("abcBook")
                .description("abc 설명")
                .publicationDate(LocalDate.now())
                .isbn("ISBN-ABC123")
                .originPrice(10000)
                .salesPrice(7000)
                .stock(3)
                .status(true)
                .build();

        Book dinoBook = Book.builder()
                .bookId(2L)
                .title("dinoBook")
                .description("dino 설명")
                .publicationDate(LocalDate.now())
                .isbn("ISBN-DINO456")
                .originPrice(50000)
                .salesPrice(40000)
                .stock(20)
                .status(true)
                .build();

        Book suspendedBook = Book.builder()
                .bookId(3L)
                .title("suspendedBook")
                .description("suspended 설명")
                .publicationDate(LocalDate.now())
                .isbn("ISBN-SUSP789")
                .originPrice(3000)
                .salesPrice(2000)
                .stock(7)
                .status(false)
                .build();


        // Book 1L: 재고 부족
        when(bookRepository.getBookStock(abcBook.getBookId())).thenReturn(abcBook.getStock());
        when(bookRepository.existsByBookIdAndStatusTrue(abcBook.getBookId())).thenReturn(abcBook.isStatus());

        // Book 2L: 구매 가능
        when(bookRepository.getBookStock(dinoBook.getBookId())).thenReturn(dinoBook.getStock());
        when(bookRepository.existsByBookIdAndStatusTrue(dinoBook.getBookId())).thenReturn(dinoBook.isStatus());

        // Book 3L: 판매중단
        when(bookRepository.getBookStock(suspendedBook.getBookId())).thenReturn(suspendedBook.getStock());
        when(bookRepository.existsByBookIdAndStatusTrue(suspendedBook.getBookId())).thenReturn(suspendedBook.isStatus());

        List<CartItem> cartItems = List.of(
                new CartItem(abcBook.getBookId(), 5),
                new CartItem(dinoBook.getBookId(), 10),
                new CartItem(suspendedBook.getBookId(), 1));

        orderItemListDto = new OrderItemListDto(cartItems);
    }


    @Test
    void validateOrderItems() {
        UnavailableOrderBooksException ex = assertThrows(UnavailableOrderBooksException.class, () -> {
            bookService.validateOrderItems(orderItemListDto);
        });
        assertEquals(2, ex.getErrors().size());
        assertEquals("재고가 부족합니다.", ex.getErrors().get(0).getMessage());

    }

}