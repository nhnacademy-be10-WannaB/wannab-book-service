package shop.wannab.book_service.book.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.wannab.book_service.book.controller.response.BookDetailResponse;
import shop.wannab.book_service.book.dto.BookIdListDto;
import shop.wannab.book_service.book.dto.BookIdTitlePriceListDto;
import shop.wannab.book_service.book.dto.CartItem;
import shop.wannab.book_service.book.dto.OrderItemListDto;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.entity.BookCategory;
import shop.wannab.book_service.book.repository.BookLikeRepository;
import shop.wannab.book_service.book.repository.BookRepository;
import shop.wannab.book_service.book.repository.projection.BookIdTitlePriceProjection;
import shop.wannab.book_service.book.service.impl.BookServiceImpl;
import shop.wannab.book_service.category.entity.Category;
import shop.wannab.book_service.global.exception.UnavailableOrderBooksException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("ci")
class BookServiceImplTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookLikeRepository bookLikeRepository;

    private Book createMockBookWithCategories() {
        Category parent = new Category("소설", null);
        Category child = new Category("스릴러", parent);

        Book book = Book.builder()
                .bookId(1L)
                .title("테스트 도서")
                .description("설명")
                .publicationDate(LocalDate.now())
                .isbn("123456789")
                .originPrice(10000)
                .salesPrice(9000)
                .stock(10)
                .status(true)
                .build();

        BookCategory cat1 = BookCategory.builder()
                .book(book)
                .category(parent)
                .build();

        BookCategory cat2 = BookCategory.builder()
                .book(book)
                .category(child)
                .build();

        book.getBookCategories().add(cat1);
        book.getBookCategories().add(cat2);

        return book;
    }

    @Test
    @DisplayName("도서 상세 조회 - 유효한 도서 ID")
    void getBookDetail_validBookId_returnsBookDetailResponse() {
        Long bookId = 1L;
        Book mockBook = createMockBookWithCategories();

        when(bookRepository.findBookDetail(bookId)).thenReturn(mockBook);

        BookDetailResponse response = bookService.getBookDetail(bookId);

        assertEquals("소설>스릴러", response.getCategories());
    }

    @Test
    @DisplayName("도서 상세 조회 - 부모 카테고리만 있는 경우")
    void getBookDetail_withParentCategoryOnly_returnsCorrectCategoryString() {
        Long bookId = 1L;
        Book mockBook = createMockBookWithCategories();
        mockBook.getBookCategories().remove(1);

        when(bookRepository.findBookDetail(bookId)).thenReturn(mockBook);

        BookDetailResponse response = bookService.getBookDetail(bookId);

        assertEquals("소설", response.getCategories());
    }

    @Test
    @DisplayName("도서 상세 조회 - 자식 카테고리만 있는 경우")
    void getBookDetail_withChildCategoryOnly_returnsCorrectCategoryString() {
        Long bookId = 1L;
        Book mockBook = createMockBookWithCategories();
        mockBook.getBookCategories().remove(0);

        when(bookRepository.findBookDetail(bookId)).thenReturn(mockBook);

        BookDetailResponse response = bookService.getBookDetail(bookId);

        assertEquals("스릴러", response.getCategories());
    }

    @Test
    @DisplayName("도서 상세 조회 - 카테고리가 없는 경우")
    void getBookDetail_withNoCategories_returnsEmptyCategoryString() {
        Long bookId = 1L;
        Book mockBook = createMockBookWithCategories();
        mockBook.getBookCategories().clear(); // 모든 카테고리 제거

        when(bookRepository.findBookDetail(bookId)).thenReturn(mockBook);

        BookDetailResponse response = bookService.getBookDetail(bookId);

        assertEquals("", response.getCategories());
    }


    @Test
    @DisplayName("주문 상품 유효성 검증 - 재고 부족")
    void validateOrderItems_insufficientStock_throwsException() {
        CartItem item = new CartItem(1L, 10);
        OrderItemListDto dto = new OrderItemListDto(List.of(item));

        when(bookRepository.getBookStock(1L)).thenReturn(5);

        assertThrows(UnavailableOrderBooksException.class, () -> {
            bookService.validateOrderItems(dto);
        });
    }

    @Test
    @DisplayName("주문 상품 유효성 검증 - 상품을 찾을 수 없음")
    void validateOrderItems_bookNotFound_throwsException() {
        CartItem item = new CartItem(1L, 1);
        OrderItemListDto dto = new OrderItemListDto(List.of(item));

        when(bookRepository.getBookStock(1L)).thenReturn(null);

        assertThrows(UnavailableOrderBooksException.class, () -> {
            bookService.validateOrderItems(dto);
        });
    }

    @Test
    @DisplayName("주문 상품 유효성 검증 - 판매 중이 아닌 상품")
    void validateOrderItems_bookNotForSale_throwsException() {
        CartItem item = new CartItem(1L, 1);
        OrderItemListDto dto = new OrderItemListDto(List.of(item));

        when(bookRepository.getBookStock(1L)).thenReturn(10);
        when(bookRepository.existsByBookIdAndStatusTrue(1L)).thenReturn(false);

        assertThrows(UnavailableOrderBooksException.class, () -> {
            bookService.validateOrderItems(dto);
        });
    }

    @Test
    @DisplayName("주문 상품 유효성 검증 - 유효한 상품")
    void validateOrderItems_validItems_doesNotThrowException() {
        CartItem item = new CartItem(1L, 1);
        OrderItemListDto dto = new OrderItemListDto(List.of(item));

        when(bookRepository.getBookStock(1L)).thenReturn(10);
        when(bookRepository.existsByBookIdAndStatusTrue(1L)).thenReturn(true);

        assertDoesNotThrow(() -> {
            bookService.validateOrderItems(dto);
        });
    }

    @Test
    @DisplayName("도서 ID, 제목, 가격 목록 조회")
    void getBookIdTitlePriceList_validBookIds_returnsDto() {
        BookIdListDto dto = new BookIdListDto(List.of(1L));
        BookIdTitlePriceProjection proj1 = mock(BookIdTitlePriceProjection.class);
        when(proj1.getBookId()).thenReturn(1L);
        when(proj1.getTitle()).thenReturn("Title 1");
        when(proj1.getSalesPrice()).thenReturn(10000);

        when(bookRepository.queryByBookIdIn(List.of(1L))).thenReturn(List.of(proj1));

        BookIdTitlePriceListDto result = bookService.getBookIdTitlePriceList(dto);

        assertEquals(1, result.getIdTitlePriceDtos().size());
        assertEquals("Title 1", result.getIdTitlePriceDtos().get(0).getTitle());
    }

    @Test
    @DisplayName("Redis 재고 감소")
    void decreaseRedisStock_validItems_callsRepository() {
        CartItem item = new CartItem(1L, 2);
        OrderItemListDto dto = new OrderItemListDto(List.of(item));

        bookService.decreaseRedisStock(dto);

        verify(bookRepository).decreaseBookRedisStock(1L, 2);
    }

    @Test
    @DisplayName("DB 재고 감소")
    void decreaseDbStock_validBook_updatesStock() {
        Book book = Book.builder().bookId(1L).stock(10).build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.decreaseDbStock(1L, 3);

        assertEquals(7, book.getStock());
    }

    @Test
    @DisplayName("재고 증가 - 유효한 상품")
    void increaseStock_validItems_callsRepository() {
        CartItem item = new CartItem(1L, 2);
        OrderItemListDto dto = new OrderItemListDto(List.of(item));
        Book book = Book.builder().bookId(1L).build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.increaseStock(dto);

        verify(bookRepository).increaseBookStock(1L, 2);
    }

    @Test
    @DisplayName("재고 증가 - 상품을 찾을 수 없음")
    void increaseStock_bookNotFound_throwsException() {
        CartItem item = new CartItem(1L, 2);
        OrderItemListDto dto = new OrderItemListDto(List.of(item));

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UnavailableOrderBooksException.class, () -> {
            bookService.increaseStock(dto);
        });
    }

}
