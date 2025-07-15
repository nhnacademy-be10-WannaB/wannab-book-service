package shop.wannab.book_service.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import shop.wannab.book_service.book.dto.BookIdListDto;
import shop.wannab.book_service.book.dto.OrderBookInfo;
import shop.wannab.book_service.book.dto.OrderBookInfoListDto;
import shop.wannab.book_service.book.dto.OrderItemListDto;
import shop.wannab.book_service.book.service.impl.BookLikeServiceImpl;
import shop.wannab.book_service.book.service.impl.BookServiceImpl;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@ActiveProfiles("ci")
@ExtendWith(org.springframework.restdocs.jupiter.RestDocumentationExtension.class)
class BookControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private BookServiceImpl bookServiceImpl;

    @MockBean
    private BookLikeServiceImpl bookLikeServiceImpl;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, org.springframework.restdocs.RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
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
                .andDo(print())
                .andDo(document("validate-order-items",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("도서 상세 조회")
    void getBookDetail() throws Exception {
        long bookId = 1L;
        given(bookServiceImpl.getBookDetail(any(Long.class))).willReturn(null); // Response DTO

        mockMvc.perform(get("/api/books/{book-id}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print())
                .andDo(document("get-book-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
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
                .andDo(print())
                .andDo(document("create-book-like",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
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
                .andDo(print())
                .andDo(document("delete-book-like",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
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
                .andDo(print())
                .andDo(document("is-book-liked",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
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
                .andDo(print())
                .andDo(document("get-order-book-infos",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
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
                .andDo(print())
                .andDo(document("decrease-stock",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
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
                .andDo(print())
                .andDo(document("increase-stock",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

}
