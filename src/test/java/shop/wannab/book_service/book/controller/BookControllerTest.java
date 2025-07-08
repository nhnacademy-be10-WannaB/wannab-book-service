package shop.wannab.book_service.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import shop.wannab.book_service.book.dto.BookIdListDto;
import shop.wannab.book_service.book.dto.OrderItemListDto;
import shop.wannab.book_service.book.service.impl.BookServiceImpl;

import java.util.Collections;

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

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookServiceImpl bookServiceImpl;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("주문 아이템 유효성 검사")
    void validateOrderItems() throws Exception {
        // given
        OrderItemListDto request = new OrderItemListDto(Collections.emptyList());
        doNothing().when(bookServiceImpl).validateOrderItems(any(OrderItemListDto.class));

        // when & then
        mockMvc.perform(post("/api/books/validation/primary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("도서 상세 조회")
    void getBookDetail() throws Exception {
        // given
        long bookId = 1L;
        given(bookServiceImpl.getBookDetail(any(Long.class))).willReturn(null); // Response DTO

        // when & then
        mockMvc.perform(get("/api/books/{book-id}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print());
    }

    @Test
    @DisplayName("도서 좋아요 등록")
    void createBookLike() throws Exception {
        // given
        long bookId = 1L;
        long userId = 1L;
        doNothing().when(bookServiceImpl).createBookLike(any(Long.class), any(Long.class));

        // when & then
        mockMvc.perform(post("/api/books/{book-id}/likes", bookId)
                        .header("X-USER-ID", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print());
    }

    @Test
    @DisplayName("도서 좋아요 취소")
    void deleteBookLike() throws Exception {
        // given
        long bookId = 1L;
        long userId = 1L;
        doNothing().when(bookServiceImpl).deleteBookLike(any(Long.class), any(Long.class));

        // when & then
        mockMvc.perform(delete("/api/books/{book-id}/likes", bookId)
                        .header("X-USER-ID", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print());
    }

    @Test
    @DisplayName("도서 좋아요 여부 조회")
    void isBookLiked() throws Exception {
        // given
        long bookId = 1L;
        long userId = 1L;
        given(bookServiceImpl.isBookLiked(any(Long.class), any(Long.class))).willReturn(true);

        // when & then
        mockMvc.perform(get("/api/books/{book-id}/likes", bookId)
                        .header("X-USER-ID", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").value(true))
                .andDo(print());
    }
}
