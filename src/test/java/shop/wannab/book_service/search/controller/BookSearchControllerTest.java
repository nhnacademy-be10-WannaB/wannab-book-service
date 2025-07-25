package shop.wannab.book_service.search.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import shop.wannab.book_service.search.domain.BookSearchField;
import shop.wannab.book_service.search.dto.response.BookSearchResult;
import shop.wannab.book_service.search.dto.response.BookSearchSource;
import shop.wannab.book_service.search.dto.response.SearchResultWithSectionResponse;
import shop.wannab.book_service.search.service.BookSearchService;

@ActiveProfiles("ci")
@WebMvcTest(BookSearchController.class)
class BookSearchControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private BookSearchService bookSearchService;

    @Test
    @DisplayName("도서 검색 API 호출 테스트")
    void search() throws Exception {
        // given
        String keyword = "자바";
        List<BookSearchResult> results = List.of(
                new BookSearchResult(new BookSearchSource(
                        "book1", "자바의 정석", "ㅈㅂㅇㅈㅅ", "설명",
                        List.of("자바"), List.of("남궁성"), List.of("출판사"),
                        List.of("IT"), LocalDate.of(2023,1,1),
                        30000, 27000, true, 120, 50, 4.5, "http://url"
                ), Map.of())
        );

        given(bookSearchService.searchBooksByKeyword(keyword)).willReturn(results);

        // when, then
        mockMvc.perform(get("/api/books/search")
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].source.title").value("자바의 정석"));
    }

    @Test
    @DisplayName("도서 통합 검색 API 호출 테스트")
    void searchMulti() throws Exception {
        // given
        String keyword = "자료구조";
        Set<BookSearchField> fields = Set.of(BookSearchField.TITLE, BookSearchField.AUTHORS);
        List<SearchResultWithSectionResponse> responses = List.of(
                new SearchResultWithSectionResponse(BookSearchField.TITLE, "제목", 1L, List.of()),
                new SearchResultWithSectionResponse(BookSearchField.AUTHORS, "저자", 2L, List.of())
        );

        given(bookSearchService.multiSearchBooks(keyword, fields))
                .willReturn(responses);

        // when, then
        mockMvc.perform(get("/api/books/search/total")
                        .param("keyword", keyword)
                        .param("field", "TITLE")
                        .param("field", "AUTHORS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].field").value("TITLE"))
                .andExpect(jsonPath("$[1].field").value("AUTHORS"));
    }
}