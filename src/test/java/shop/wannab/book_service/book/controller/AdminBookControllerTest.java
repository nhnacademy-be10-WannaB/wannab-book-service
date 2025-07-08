package shop.wannab.book_service.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import shop.wannab.book_service.book.controller.request.BookCreateRequest;
import shop.wannab.book_service.book.controller.request.BookUpdateRequest;
import shop.wannab.book_service.book.service.impl.AdminBookServiceImpl;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminBookController.class)
@ActiveProfiles("ci")
class AdminBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminBookServiceImpl adminBookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("도서 목록 조회")
    void getBooks() throws Exception {
        given(adminBookService.getBookList(any(PageRequest.class)))
                .willReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/api/admin/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print());
    }

    @Test
    @DisplayName("도서 등록")
    void createBook() throws Exception {
        BookCreateRequest request = BookCreateRequest.builder()
                .title("Test Book")
                .description("Test Description")
                .publicationDate(java.time.LocalDate.now())
                .originPrice(10000)
                .stock(10)
                .isbn("1234567890")
                .status(true)
                .categories(java.util.List.of("Category1", "Category2"))
                .authors(java.util.List.of("Author1"))
                .publishers(java.util.List.of("Publisher1"))
                .bookImages(java.util.List.of("image1.jpg"))
                .bookTags(java.util.List.of("Tag1"))
                .build();
        doNothing().when(adminBookService).createBook(any(BookCreateRequest.class));

        mockMvc.perform(post("/api/admin/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print());
    }

    @Test
    @DisplayName("도서 수정")
    void updateBook() throws Exception {
        long bookId = 1L;
        BookUpdateRequest request = BookUpdateRequest.builder()
                .title("Updated Book")
                .description("Updated Description")
                .publicationDate(java.time.LocalDate.now())
                .originPrice(12000)
                .stock(15)
                .isbn("0987654321")
                .status(true)
                .categories(java.util.List.of("Category1", "Category2"))
                .authors(java.util.List.of("Author1"))
                .publishers(java.util.List.of("Publisher1"))
                .bookImages(java.util.List.of("image1.jpg"))
                .bookTags(java.util.List.of("Tag1"))
                .build();
        doNothing().when(adminBookService).updateBook(any(Long.class), any(BookUpdateRequest.class));

        mockMvc.perform(put("/api/admin/books/{book-id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print());
    }

    @Test
    @DisplayName("도서 삭제")
    void deleteBook() throws Exception {
        long bookId = 1L;
        doNothing().when(adminBookService).deleteBook(any(Long.class));

        mockMvc.perform(delete("/api/admin/books/{book-id}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print());
    }
}
