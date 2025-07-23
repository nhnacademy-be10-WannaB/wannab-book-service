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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import shop.wannab.book_service.book.controller.response.*;
import shop.wannab.book_service.book.dto.*;
import shop.wannab.book_service.book.service.BookLikeQueryService;
import shop.wannab.book_service.book.service.impl.BookLikeServiceImpl;
import shop.wannab.book_service.book.service.impl.BookServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import shop.wannab.book_service.book.controller.response.BookLikeListResponse;
import shop.wannab.book_service.book.controller.response.BookListResponse;
import shop.wannab.book_service.book.controller.response.HotBooksListResponse;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

@WebMvcTest(BookController.class)
@ActiveProfiles("ci")
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class BookControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private BookServiceImpl bookServiceImpl;

    @MockBean
    private BookLikeServiceImpl bookLikeServiceImpl;

    @MockBean
    private BookLikeQueryService bookLikeQueryService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("주문 아이템 유효성 검사")
    void validateOrderItems() throws Exception {
        OrderItemListDto request = new OrderItemListDto(List.of(new CartItem(1L, 2), new CartItem(2L, 3)));
        doNothing().when(bookServiceImpl).validateOrderItems(any(OrderItemListDto.class));

        mockMvc.perform(post("/api/books/validation/primary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("books/validateOrderItems",
                        requestFields(
                                fieldWithPath("orderItems").description("주문 아이템 목록"),
                                fieldWithPath("orderItems[].bookId").description("도서 ID"),
                                fieldWithPath("orderItems[].quantity").description("수량")
                        )
                ));
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
                .andDo(document("books/getOrderBookInfos",
                        requestFields(
                                fieldWithPath("orderItems").description("주문 아이템 목록"),
                                fieldWithPath("orderItems[].bookId").description("도서 ID"),
                                fieldWithPath("orderItems[].quantity").description("수량")
                        ),
                        responseFields(
                                fieldWithPath("orderBookInfos").description("주문 도서 정보 목록"),
                                fieldWithPath("orderBookInfos[].bookId").description("도서 ID"),
                                fieldWithPath("orderBookInfos[].title").description("도서 제목"),
                                fieldWithPath("orderBookInfos[].originPrice").description("정가"),
                                fieldWithPath("orderBookInfos[].salesPrice").description("판매가"),
                                fieldWithPath("orderBookInfos[].quantity").description("수량"),
                                fieldWithPath("orderBookInfos[].thumbnailUrl").description("썸네일 이미지 URL")
                        )
                        ));
    }

    @Test
    @DisplayName("도서 상세 조회")
    void getBookDetail() throws Exception {
        long bookId = 1L;
        BookDetailResponse response = BookDetailResponse.builder()
                .bookId(1L)
                .title("테스트 도서")
                .description("설명")
                .publicationDate(LocalDate.of(2023, 1, 1))
                .originPrice(15000)
                .salesPrice(12000)
                .stock(100)
                .isbn("1234567890")
                .status(true)
                .bookChapter("1장: 시작하기")
                .authorNames(List.of("작가1", "작가2"))
                .publisherNames(List.of("출판사1"))
                .tagNames(List.of("소설", "베스트셀러"))
                .imageUrls(List.of("img1.jpg", "img2.jpg"))
                .categories("문학 > 소설")
                .build();

        given(bookServiceImpl.getBookDetail(any(Long.class))).willReturn(response);

        mockMvc.perform(get("/api/books/{book-id}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print())
                .andDo(document("books/getBookDetail",
                        pathParameters(
                                parameterWithName("book-id").description("도서 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 (SUCCESS 또는 ERROR)"),
                                fieldWithPath("data.bookId").description("도서 ID"),
                                fieldWithPath("data.title").description("도서 제목"),
                                fieldWithPath("data.description").description("도서 설명"),
                                fieldWithPath("data.publicationDate").description("출판일"),
                                fieldWithPath("data.originPrice").description("정가"),
                                fieldWithPath("data.salesPrice").description("판매가"),
                                fieldWithPath("data.stock").description("재고 수량"),
                                fieldWithPath("data.isbn").description("ISBN"),
                                fieldWithPath("data.status").description("판매 상태 (true: 판매 중, false: 판매 중단)"),
                                fieldWithPath("data.bookChapter").description("도서 목차"),
                                fieldWithPath("data.authorNames").description("저자 이름 목록"),
                                fieldWithPath("data.publisherNames").description("출판사 이름 목록"),
                                fieldWithPath("data.tagNames").description("태그 목록"),
                                fieldWithPath("data.imageUrls").description("도서 이미지 URL 목록"),
                                fieldWithPath("data.categories").description("도서 카테고리"),
                                fieldWithPath("error").ignored()
                        )

                ));
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
                .andDo(document("books/createBookLike",
                        pathParameters(
                                parameterWithName("book-id").description("도서 ID")
                        ),
                        requestHeaders(
                                headerWithName("X-USER-ID").description("요청 사용자 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 (SUCCESS 또는 ERROR)"),
                                fieldWithPath("data").description("응답 데이터 (없음)").optional(),
                                fieldWithPath("error").ignored()

                        )
                ));
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
                .andDo(document("books/deleteBookLike",
                        pathParameters(
                                parameterWithName("book-id").description("도서 ID")
                        ),
                        requestHeaders(
                                headerWithName("X-USER-ID").description("요청 사용자 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 (SUCCESS 또는 ERROR)"),
                                fieldWithPath("data").description("응답 데이터 (없음)").optional(),
                                fieldWithPath("error").ignored()

                        )
                ));
    }

    @Test
    @DisplayName("도서 좋아요 여부 조회")
    void isBookLiked() throws Exception {
        long bookId = 1L;
        long userId = 1L;
        given(bookLikeQueryService.isBookLiked(any(Long.class), any(Long.class))).willReturn(true);

        mockMvc.perform(get("/api/books/{book-id}/likes", bookId)
                        .header("X-USER-ID", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").value(true))
                .andDo(print())
                .andDo(document("books/isBookLiked",
                        pathParameters(
                                parameterWithName("book-id").description("도서 ID")
                        ),
                        requestHeaders(
                                headerWithName("X-USER-ID").description("요청 사용자 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 (SUCCESS 또는 ERROR)"),
                                fieldWithPath("data").description("좋아요 여부 (true: 좋아요 함, false: 좋아요 안 함)"),
                                fieldWithPath("error").ignored()

                        )
                ));
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
                .andDo(print())
                .andDo(document("books/getBookSimpleInfos",
                        requestFields(
                                fieldWithPath("bookIds").description("도서 ID 목록")
                        ),
                        responseFields(
                                fieldWithPath("idTitlePriceDtos").description("도서 정보 목록"),
                                fieldWithPath("idTitlePriceDtos[].bookId").description("도서 ID"),
                                fieldWithPath("idTitlePriceDtos[].title").description("도서 제목"),
                                fieldWithPath("idTitlePriceDtos[].salesPrice").description("도서 가격")
                        )
                ));
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
                .andDo(document("books/increaseStock",
                        requestFields(
                                fieldWithPath("orderItems").description("주문 아이템 목록"),
                                fieldWithPath("orderItems[].bookId").description("도서 ID"),
                                fieldWithPath("orderItems[].quantity").description("수량")
                        )
                ));
    }

    @Test
    @DisplayName("카테고리별 도서 검색")
    void searchBooks() throws Exception {
        long categoryId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<BookListResponse> bookList = List.of(BookListResponse.builder()
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
                .build());
        Page<BookListResponse> response = new PageImpl<>(bookList, pageable, 1);
        given(bookServiceImpl.searchBooks(any(Long.class), any(Pageable.class))).willReturn(response);

        mockMvc.perform(get("/api/books/{category-id}/search", categoryId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].bookId").value(1L))
                .andDo(print())
                .andDo(document("books/searchBooks",
                    pathParameters(
                            parameterWithName("category-id").description("카테고리 ID")
                    ),
                    queryParameters(
                            parameterWithName("page").description("페이지 번호").optional(),
                            parameterWithName("size").description("페이지 크기").optional()
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
    @DisplayName("좋아요한 도서 목록 조회")
    void getLikedBooks() throws Exception {
        long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<BookLikeListResponse> likedBooks = List.of(BookLikeListResponse.builder()
                .bookId(1L)
                .title("Book Title")
                .imageUrl(List.of("thumbnail.jpg"))
                .authors(List.of("Author"))
                .liked(true)
                .build());
        Page<BookLikeListResponse> response = new PageImpl<>(likedBooks, pageable, 1);
        given(bookLikeServiceImpl.getLikedBooks(any(Long.class), any(Integer.class), any(Integer.class))).willReturn(response);

        mockMvc.perform(get("/api/books/liked-books")
                        .header("X-USER-ID", userId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].bookId").value(1L))
                .andDo(print())
                .andDo(document("books/getLikedBooks",
                        requestHeaders(
                                headerWithName("X-USER-ID").description("요청 사용자 ID")
                        ),
                        queryParameters(
                                parameterWithName("page").description("요청 페이지 번호").optional(),
                                parameterWithName("size").description("요청하는 페이지의 사이즈").optional()
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 (SUCCESS 또는 ERROR)"),
                                fieldWithPath("data.content[].bookId").description("도서 ID"),
                                fieldWithPath("data.content[].title").description("도서 제목"),
                                fieldWithPath("data.content[].imageUrl").description("도서 썸네일 이미지 URL 목록"),
                                fieldWithPath("data.content[].authors").description("저자 이름 목록"),
                                fieldWithPath("data.content[].liked").description("좋아요 여부"),
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
                .andDo(print())
                .andDo(document("books/getBookNames",
                        requestFields(
                                fieldWithPath("[]").description("조회할 도서 ID 목록")
                        ),
                        responseFields(
                                fieldWithPath("1").description("도서 ID 1의 제목"),
                                fieldWithPath("2").description("도서 ID 2의 제목")
                        )
                ));
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
                .andDo(print())
                .andDo(document("books/getHotBooks",
                        queryParameters(
                                parameterWithName("page").description("요청 페이지 번호").optional(),
                                parameterWithName("size").description("요청하는 페이지의 사이즈").optional()
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 (SUCCESS 또는 ERROR)"),
                                fieldWithPath("data[].bookId").description("도서 ID"),
                                fieldWithPath("data[].title").description("도서 제목"),
                                fieldWithPath("data[].description").description("도서 설명"),
                                fieldWithPath("data[].imageUrls").description("도서 썸네일 이미지 URL 목록"),
                                fieldWithPath("error").ignored()
                        )
                        ));
    }
}
