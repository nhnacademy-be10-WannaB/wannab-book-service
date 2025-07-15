package shop.wannab.book_service.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import shop.wannab.book_service.book.controller.response.BookLikeListResponse;
import shop.wannab.book_service.book.controller.response.BookListResponse;
import shop.wannab.book_service.book.controller.response.HotBooksListResponse;
import shop.wannab.book_service.book.dto.*;
import shop.wannab.book_service.book.service.impl.BookLikeServiceImpl;
import shop.wannab.book_service.book.service.impl.BookServiceImpl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@ActiveProfiles("ci")
class BookControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private BookServiceImpl bookServiceImpl;

    @MockBean
    private BookLikeServiceImpl bookLikeServiceImpl;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    @DisplayName("주문 아이템 유효성 검사")
    void validateOrderItems() throws Exception {
        OrderItemListDto request = new OrderItemListDto(Collections.emptyList());
        doNothing().when(bookServiceImpl).validateOrderItems(any(OrderItemListDto.class));

        mockMvc.perform(post("/api/books/validation/primary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("도서 상세 조회")
    void getBookDetail() throws Exception {
        long bookId = 1L;
        given(bookServiceImpl.getBookDetail(any(Long.class))).willReturn(null); // Response DTO

        mockMvc.perform(get("/api/books/{book-id}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print());
    }

    @Test
    @DisplayName("도서 좋아요 등록")
    void createBookLike() throws Exception {
        long bookId = 1L;
        long userId = 1L;
        doNothing().when(bookLikeServiceImpl).createBookLike(any(Long.class), any(Long.class));

        mockMvc.perform(post("/api/books/{book-id}/likes", bookId)
                        .header("X-USER-ID", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print());
    }

    @Test
    @DisplayName("도서 좋아요 취소")
    void deleteBookLike() throws Exception {
        long bookId = 1L;
        long userId = 1L;
        doNothing().when(bookLikeServiceImpl).deleteBookLike(any(Long.class), any(Long.class));

        mockMvc.perform(delete("/api/books/{book-id}/likes", bookId)
                        .header("X-USER-ID", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print());
    }

    @Test
    @DisplayName("도서 좋아요 여부 조회")
    void isBookLiked() throws Exception {
        long bookId = 1L;
        long userId = 1L;
        given(bookLikeServiceImpl.isBookLiked(any(Long.class), any(Long.class))).willReturn(true);

        mockMvc.perform(get("/api/books/{book-id}/likes", bookId)
                        .header("X-USER-ID", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("주문 도서 정보 조회")
    void getOrderBookInfos() throws Exception {
        OrderItemListDto request = new OrderItemListDto(List.of(new CartItem(1L, 2)));
        OrderBookInfoListDto response = new OrderBookInfoListDto(List.of(new OrderBookInfo(1L, "Test Title", 10000, 10000, 2, "thumbnail.jpg")));
        given(bookServiceImpl.getOrderBookInfos(any(OrderItemListDto.class))).willReturn(response);

        mockMvc.perform(post("/api/books/for-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("재고 감소")
    void decreaseStock() throws Exception {
        OrderItemListDto request = new OrderItemListDto(List.of(new CartItem(1L, 2)));
        doNothing().when(bookServiceImpl).decreaseRedisStock(any(OrderItemListDto.class));

        mockMvc.perform(post("/api/books/decrease-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("재고 증가")
    void increaseStock() throws Exception {
        OrderItemListDto request = new OrderItemListDto(List.of(new CartItem(1L, 2)));
        doNothing().when(bookServiceImpl).increaseStock(any(OrderItemListDto.class));

        mockMvc.perform(post("/api/books/increase-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("도서 간단 정보 조회")
    void getBookSimpleInfos() throws Exception {
        BookIdListDto request = new BookIdListDto(List.of(1L, 2L));
        BookIdTitlePriceListDto response = new BookIdTitlePriceListDto(List.of(
                new BookIdTitlePriceDto(1L, "Book 1", 10000),
                new BookIdTitlePriceDto(2L, "Book 2", 20000)
        ));
        given(bookServiceImpl.getBookIdTitlePriceList(any(BookIdListDto.class))).willReturn(response);

        mockMvc.perform(post("/api/books/simple-info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTitlePriceDtos[0].bookId").value(1L))
                .andDo(print());
    }

    @Test
    @DisplayName("카테고리별 도서 검색")
    void searchBooks() throws Exception {
        long categoryId = 1L;
        Page<BookListResponse> response = new PageImpl<>(List.of(BookListResponse.builder()
                .bookId(1L)
                .title("Book Title")
                .description("Description")
                .publicationDate(LocalDate.now())
                .originPrice(10000)
                .isbn("1234567890")
                .stock(10)
                .status(true)
                .authorNames(List.of("Author"))
                .publisherNames(List.of("Publisher"))
                .imageUrls(List.of("thumbnail.jpg"))
                .tagNames(List.of("Tag"))
                .build()));
        given(bookServiceImpl.searchBooks(any(Long.class), any(Pageable.class))).willReturn(response);

        mockMvc.perform(get("/api/books/{category-id}/search", categoryId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].bookId").value(1L))
                .andDo(print());
    }

    @Test
    @DisplayName("좋아요한 도서 목록 조회")
    void getLikedBooks() throws Exception {
        long userId = 1L;
        Page<BookLikeListResponse> response = new PageImpl<>(List.of(BookLikeListResponse.builder()
                .bookId(1L)
                .title("Book Title")
                .imageUrl(List.of("thumbnail.jpg"))
                .authors(List.of("Author"))
                .liked(true)
                .build()));
        given(bookLikeServiceImpl.getLikedBooks(any(Long.class), any(Integer.class), any(Integer.class))).willReturn(response);

        mockMvc.perform(get("/api/books/liked-books")
                        .header("X-USER-ID", userId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].bookId").value(1L))
                .andDo(print());
    }

    @Test
    @DisplayName("도서 이름 목록 조회")
    void getBookNames() throws Exception {
        List<Long> bookIds = List.of(1L, 2L);
        Map<Long, String> response = Map.of(1L, "Book A", 2L, "Book B");
        given(bookServiceImpl.findBookNamesByIds(any(List.class))).willReturn(response);

        mockMvc.perform(post("/api/books/names")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.1").value("Book A"))
                .andDo(print());
    }

    @Test
    @DisplayName("인기 도서 목록 조회")
    void getHotBooks() throws Exception {
        List<HotBooksListResponse> response = List.of(new HotBooksListResponse(1L, "Hot Book", "Hot Description", List.of("hot_thumbnail.jpg")));
        given(bookLikeServiceImpl.getHotBooksList(any(Integer.class), any(Integer.class))).willReturn(response);

        mockMvc.perform(get("/api/books/hot-books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].bookId").value(1L))
                .andDo(print());
    }
}
