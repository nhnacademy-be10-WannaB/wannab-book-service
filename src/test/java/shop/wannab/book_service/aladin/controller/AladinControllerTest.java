package shop.wannab.book_service.aladin.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import shop.wannab.book_service.aladin.client.response.SearchResponse;
import shop.wannab.book_service.aladin.controller.request.BookInfoRequest;
import shop.wannab.book_service.aladin.domain.AladinEnums.CoverOption;
import shop.wannab.book_service.aladin.domain.AladinEnums.QueryType;
import shop.wannab.book_service.aladin.domain.AladinEnums.SearchTarget;
import shop.wannab.book_service.aladin.domain.AladinEnums.SortOption;
import shop.wannab.book_service.aladin.service.AladinService;
import shop.wannab.book_service.book.controller.request.AladinBookCreateRequest;

@ActiveProfiles("ci")
@WebMvcTest(AladinController.class)
class AladinControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    @MockBean private AladinService aladinService;

    @Test
    @DisplayName("알라딘 도서 검색 요청을 처리한다")
    void getBooksInfo() throws Exception {
        // given
        BookInfoRequest request = BookInfoRequest.builder()
                .query("워너비").queryType(QueryType.Keyword).searchTarget(SearchTarget.Book)
                .start(1).maxResults(10).sort(SortOption.Accuracy).cover(CoverOption.Big).categoryId(0)
                .build();

        SearchResponse dummyResponse = new SearchResponse(
                "워너비", 1, 1, 10,
                "워너비", "1", "카테고리", List.of()
        );

        given(aladinService.searchBooks(any(BookInfoRequest.class)))
                .willReturn(dummyResponse);

        // when & then
        mockMvc.perform(post("/api/admin/aladin/books/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("워너비"))
                .andExpect(jsonPath("$.query").value("워너비"));
    }

    @Test
    @DisplayName("알라딘 도서를 등록한다")
    void createAladinBook() throws Exception {
        // given
        AladinBookCreateRequest request = new AladinBookCreateRequest("워너비", List.of("작가1"), List.of("출판사"),
                LocalDate.of(2025, 7, 23), "isbn-01", 1000, "설명", "이미지",
                List.of("카테고리"), 10, "챕터", true
        );

        // when & then
        mockMvc.perform(post("/api/admin/aladin/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(aladinService).aladinCreateBook(any(AladinBookCreateRequest.class));
    }
}