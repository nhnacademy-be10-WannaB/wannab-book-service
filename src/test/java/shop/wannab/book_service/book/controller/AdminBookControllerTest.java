package shop.wannab.book_service.book.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import shop.wannab.book_service.book.controller.request.BookCreateRequest;
import shop.wannab.book_service.book.controller.request.BookUpdateRequest;
import shop.wannab.book_service.book.controller.response.BookListResponse;
import shop.wannab.book_service.book.service.impl.AdminBookServiceImpl;

@WebMvcTest(AdminBookController.class)
@AutoConfigureRestDocs
@DisplayName("AdminBook Controller 단위 테스트")
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
        List<BookListResponse> bookList = List.of(
                BookListResponse.builder()
                        .bookId(1L)
                        .title("Test Book")
                        .description("Test Description")
                        .publicationDate(java.time.LocalDate.now())
                        .originPrice(10000)
                        .isbn("1234567890")
                        .stock(10)
                        .status(true)
                        .authorNames(List.of("Author1"))
                        .publisherNames(List.of("Publisher1"))
                        .imageUrls(List.of("image1.jpg"))
                        .tagNames(List.of("Tag1"))
                        .build()
        );
        Page<BookListResponse> responsePage = new PageImpl<>(bookList, PageRequest.of(0, 10), 1);

        given(adminBookService.getBookList(anyString(), any(Pageable.class)))
                .willReturn(responsePage);

        mockMvc.perform(get("/api/admin/books")
                        .param("page", "0")
                        .param("size", "10")
                        .param("keyword", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print())
                .andDo(document("admin/books/getBooks",
                        queryParameters(
                                parameterWithName("page").description("페이지 번호 (0부터 시작, 기본값: 0)").optional(),
                                parameterWithName("size").description("페이지 당 항목 수 (기본값: 20)").optional(),
                                parameterWithName("sort").description("정렬 기준(형식: '속성,asc|desc')").optional(),
                                parameterWithName("keyword").description("검색할 키워드(기본값:'')").optional()
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 (SUCCESS 또는 ERROR)"),
                                fieldWithPath("data.content[].bookId").description("도서 ID"),
                                fieldWithPath("data.content[].title").description("도서 제목"),
                                fieldWithPath("data.content[].description").description("도서 설명"),
                                fieldWithPath("data.content[].publicationDate").description("출판일"),
                                fieldWithPath("data.content[].originPrice").description("정가"),
                                fieldWithPath("data.content[].isbn").description("ISBN"),
                                fieldWithPath("data.content[].stock").description("재고 수량"),
                                fieldWithPath("data.content[].status").description("판매 상태 (true: 판매 중, false: 판매 중단)"),
                                fieldWithPath("data.content[].authorNames").description("저자 이름 목록"),
                                fieldWithPath("data.content[].publisherNames").description("출판사 이름 목록"),
                                fieldWithPath("data.content[].imageUrls").description("도서 이미지 URL 목록"),
                                fieldWithPath("data.content[].tagNames").description("태그 목록"),
                                fieldWithPath("data.pageable.pageNumber").description("페이지 번호"),
                                fieldWithPath("data.pageable.pageSize").description("페이지 크기"),
                                fieldWithPath("data.pageable.sort.sorted").description("정렬 여부"),
                                fieldWithPath("data.pageable.sort.unsorted").description("비정렬 여부"),
                                fieldWithPath("data.pageable.sort.empty").description("정렬 정보 비어있는지 여부"),
                                fieldWithPath("data.pageable.offset").description("오프셋"),
                                fieldWithPath("data.pageable.paged").description("페이징 여부"),
                                fieldWithPath("data.pageable.unpaged").description("비페이징 여부"),
                                fieldWithPath("data.last").description("마지막 페이지 여부"),
                                fieldWithPath("data.totalPages").description("총 페이지 수"),
                                fieldWithPath("data.totalElements").description("총 요소 수"),
                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.number").description("현재 페이지 번호"),
                                fieldWithPath("data.sort.sorted").description("정렬 여부"),
                                fieldWithPath("data.sort.unsorted").description("비정렬 여부"),
                                fieldWithPath("data.sort.empty").description("정렬 정보 비어있는지 여부"),
                                fieldWithPath("data.first").description("첫 페이지 여부"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지의 요소 수"),
                                fieldWithPath("data.empty").description("비어있는지 여부"),
                                fieldWithPath("error").ignored()
                        )
                ));
    }

    @Test
    @DisplayName("도서 등록")
    void createBook() throws Exception {
        BookCreateRequest request = BookCreateRequest.builder()
                .title("Test Book")
                .description("Test Description")
                .publicationDate(java.time.LocalDate.now())
                .originPrice(10000)
                .salesPrice(8000)
                .stock(10)
                .bookChapter("bookChapter")
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
                .andDo(print())
                .andDo(document("admin/books/createBook",
                        requestFields(
                                fieldWithPath("title").description("도서 제목"),
                                fieldWithPath("description").description("도서 설명"),
                                fieldWithPath("publicationDate").description("출판일 (YYYY-MM-DD 형식)"),
                                fieldWithPath("originPrice").description("정가"),
                                fieldWithPath("salesPrice").description("할인가"),
                                fieldWithPath("stock").description("재고 수량"),
                                fieldWithPath("bookChapter").description("목차"),
                                fieldWithPath("isbn").description("ISBN"),
                                fieldWithPath("status").description("판매 상태 (true: 판매 중, false: 판매 중단)"),
                                fieldWithPath("categories").description("카테고리 이름 목록"),
                                fieldWithPath("authors").description("저자 이름 목록"),
                                fieldWithPath("publishers").description("출판사 이름 목록"),
                                fieldWithPath("bookImages").description("도서 이미지 URL 목록"),
                                fieldWithPath("bookTags").description("도서 태그 이름 목록")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 (SUCCESS 또는 ERROR)"),
                                fieldWithPath("data").description("응답 데이터 (없음)").optional(),
                                fieldWithPath("error").ignored()
                        )
                        ));
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
                .andDo(print())
                .andDo(document("admin/books/updateBook",
                        pathParameters(
                                parameterWithName("book-id").description("책 ID")
                        ),
                        requestFields(
                                fieldWithPath("title").description("도서 제목").optional(),
                                fieldWithPath("description").description("도서 설명").optional(),
                                fieldWithPath("publicationDate").description("출판일 (YYYY-MM-DD 형식)").optional(),
                                fieldWithPath("originPrice").description("정가").optional(),
                                fieldWithPath("salesPrice").description("할인가").optional(),
                                fieldWithPath("stock").description("재고 수량").optional(),
                                fieldWithPath("isbn").description("ISBN").optional(),
                                fieldWithPath("status").description("판매 상태 (true: 판매 중, false: 판매 중단)").optional(),
                                fieldWithPath("bookChapter").description("목차").optional(),
                                fieldWithPath("categories").description("카테고리 이름 목록").optional(),
                                fieldWithPath("authors").description("저자 이름 목록").optional(),
                                fieldWithPath("publishers").description("출판사 이름 목록").optional(),
                                fieldWithPath("bookImages").description("도서 이미지 URL 목록").optional(),
                                fieldWithPath("bookTags").description("도서 태그 이름 목록").optional()
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 (SUCCESS 또는 ERROR)"),
                                fieldWithPath("data").description("응답 데이터 (없음)").optional(),
                                fieldWithPath("error").ignored()
                        )
                ));
    }

    @Test
    @DisplayName("도서 삭제")
    void deleteBook() throws Exception {
        long bookId = 1L;
        doNothing().when(adminBookService).deleteBook(any(Long.class));

        mockMvc.perform(delete("/api/admin/books/{book-id}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print())
                .andDo(document("admin/books/deleteBook",
                        pathParameters(
                                parameterWithName("book-id").description("책 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 (SUCCESS 또는 ERROR)"),
                                fieldWithPath("data").description("응답 데이터 (없음)").optional(),
                                fieldWithPath("error").ignored()
                        )
                ));
    }
}
