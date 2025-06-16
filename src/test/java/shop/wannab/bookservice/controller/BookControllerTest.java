package shop.wannab.bookservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.wannab.bookservice.dto.BookDto;
import shop.wannab.bookservice.exception.BookNotFoundException;
import shop.wannab.bookservice.service.BookService;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {
    @Autowired
    MockMvc mvc;
    @MockBean
    BookService svc;

    @Test
    void getBook_정상() throws Exception {
        BookDto.Response r = new BookDto.Response(
                1L, "T", "D", LocalDate.now(), "ISBN",
                1000, 900, true, 5, "AVAILABLE"
        );
        when(svc.getBook(1L)).thenReturn(r);

        mvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.title").value("T"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void getBook_없으면404() throws Exception {
        when(svc.getBook(2L))
                .thenThrow(new BookNotFoundException(2L));

        mvc.perform(get("/api/books/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("이 도서는 존재하지 않습니다 (id=2)"));
    }
}
