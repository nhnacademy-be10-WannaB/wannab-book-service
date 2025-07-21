package shop.wannab.book_service.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import shop.wannab.book_service.review.controller.request.ReviewCreateRequest;
import shop.wannab.book_service.review.controller.request.ReviewUpdateRequest;
import shop.wannab.book_service.review.controller.response.BookReviewListResponse;
import shop.wannab.book_service.review.controller.response.ReviewImageResponse;
import shop.wannab.book_service.review.controller.response.UserReviewListResponse;
import shop.wannab.book_service.review.service.ReviewService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@DisplayName("review Controller 단위 테스트")
@ActiveProfiles("ci")
@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("리뷰 등록 성공")
    void createReview() throws Exception {
        long bookId = 1L;
        long userId = 1L;
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .reviewContent("updated content")
                .reviewScore(4)
                .reviewImages(java.util.Collections.emptyList())
                .build();

        doNothing().when(reviewService).createReview(any(ReviewCreateRequest.class), anyLong(), anyLong());

        mockMvc.perform(post("/api/reviews/books/{book-id}", bookId)
                        .header("X-USER-ID", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print())
                .andDo(document("reviews/createReview",
                        pathParameters(
                                parameterWithName("book-id").description("책 ID")
                        ),
                        requestHeaders(
                                headerWithName("X-USER-ID").description("사용자 유저 ID")
                        ),
                        requestFields(
                                fieldWithPath("reviewContent").description("리뷰 내용"),
                                fieldWithPath("reviewScore").description("리뷰 점수 (1-5)"),
                                fieldWithPath("bookName").description("책 이름"),
                                fieldWithPath("reviewCreatedAt").description("리뷰 생성 시간"),
                                fieldWithPath("obId").description("오더 북 ID"),
                                fieldWithPath("reviewImages").description("리뷰 이미지 URL 목록").optional()
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 (SUCCESS 또는 ERROR)"),
                                fieldWithPath("data").description("응답 데이터 (없음)").optional(),
                                fieldWithPath("error").ignored()
                        )
                ));
    }

    @Test
    @DisplayName("회원 기준 리뷰 목록 조회 성공")
    void getUserReviews() throws Exception {
        long userId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 10);
        UserReviewListResponse sampleReview = UserReviewListResponse.builder()
                .reviewId(1L)
                .bookName("Sample Book Title")
                .reviewContent("This is a sample review content.")
                .reviewScore(5)
                .reviewImages(
                        List.of(
                                ReviewImageResponse.builder().reviewImageUrl("image1.jpg").build(),
                                ReviewImageResponse.builder().reviewImageUrl("image2.jpg").build()
                        ))
                .reviewCreatedAt(java.time.LocalDateTime.now())
                .build();
        Page<UserReviewListResponse> response = new PageImpl<>(List.of(sampleReview), pageRequest, 1);

        given(reviewService.getUserReviewList(any(PageRequest.class), anyLong())).willReturn(response);

        mockMvc.perform(get("/api/reviews/me")
                        .header("X-USER-ID", userId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print())
                .andDo(document("review/getUserReviews",
                        queryParameters(
                                parameterWithName("page").description("페이지 번호 (0부터 시작, 기본값: 0)").optional(),
                                parameterWithName("size").description("페이지 당 항목 수 (기본값: 20)").optional(),
                                parameterWithName("sort").description("정렬 기준(형식: '속성,asc|desc')").optional()
                        ),
                        requestHeaders(
                                headerWithName("X-USER-ID").description("요청 사용자 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 (SUCCESS 또는 ERROR)"),
                                fieldWithPath("data.content[].reviewId").description("리뷰 ID"),
                                fieldWithPath("data.content[].bookName").description("도서 제목"),
                                fieldWithPath("data.content[].reviewContent").description("리뷰 내용"),
                                fieldWithPath("data.content[].reviewScore").description("리뷰 점수"),
                                fieldWithPath("data.content[].reviewImages[].reviewImageUrl").description("리뷰 이미지 URL").optional(),
                                fieldWithPath("data.content[].reviewCreatedAt").description("리뷰 생성일시"),
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
    @DisplayName("도서 기준 리뷰 목록 조회 성공")
    void getBookReviews() throws Exception {
        long bookId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 10);
        BookReviewListResponse sampleReview = BookReviewListResponse.builder()
                .reviewContent("Great book!")
                .reviewScore(5)
                .reviewImages(List.of(
                        ReviewImageResponse.builder().reviewImageUrl("image1.jpg").build(),
                        ReviewImageResponse.builder().reviewImageUrl("image2.jpg").build()
                ))
                .username("username")
                .reviewCreatedAt(java.time.LocalDateTime.now())
                .build();
        Page<BookReviewListResponse> response = new PageImpl<>(List.of(sampleReview), pageRequest, 1);
        given(reviewService.getBookReviewList(any(PageRequest.class), anyLong())).willReturn(response);

        mockMvc.perform(get("/api/reviews/books/{book-id}", bookId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print())
                .andDo(document("review/getBookReviews",
                        pathParameters(
                                parameterWithName("book-id").description("도서 ID")
                        ),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호 (0부터 시작, 기본값: 0)").optional(),
                                parameterWithName("size").description("페이지 당 항목 수 (기본값: 20)").optional(),
                                parameterWithName("sort").description("정렬 기준(형식: '속성,asc|desc')").optional()
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 (SUCCESS 또는 ERROR)"),
                                fieldWithPath("data.content[].reviewContent").description("리뷰 내용"),
                                fieldWithPath("data.content[].reviewScore").description("리뷰 점수"),
                                fieldWithPath("data.content[].reviewImages[].reviewImageUrl").description("리뷰 이미지 URL").optional(),
                                fieldWithPath("data.content[].username").description("유저 이름"),
                                fieldWithPath("data.content[].reviewCreatedAt").description("리뷰 생성일시"),
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
    @DisplayName("리뷰 수정 성공")
    void updateReview() throws Exception {
        long reviewId = 1L;
        ReviewUpdateRequest request = ReviewUpdateRequest.builder()
                .reviewContent("updated content")
                .reviewScore(4)
                .reviewUpdatedAt(java.time.LocalDateTime.now())
                .reviewImages(java.util.Collections.emptyList())
                .build();
        doNothing().when(reviewService).updateReview(any(ReviewUpdateRequest.class), anyLong());

        mockMvc.perform(put("/api/reviews/{review-id}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print())
                .andDo(document("review/updateReview",
                        pathParameters(
                                parameterWithName("review-id").description("리뷰 ID")
                        ),
                        requestFields(
                                fieldWithPath("reviewContent").description("리뷰 내용").optional(),
                                fieldWithPath("reviewScore").description("리뷰 점수 (1-5)").optional(),
                                fieldWithPath("reviewUpdatedAt").description("리뷰 수정일시").optional(),
                                fieldWithPath("reviewImages").description("리뷰 이미지 URL 목록").optional()
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 (SUCCESS 또는 ERROR)"),
                                fieldWithPath("data").description("응답 데이터 (없음)").optional(),
                                fieldWithPath("error").ignored()
                        )
                ));
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void deleteReview() throws Exception {
        long reviewId = 1L;
        doNothing().when(reviewService).deleteReview(anyLong());

        mockMvc.perform(delete("/api/reviews/{review-id}", reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print())
                .andDo(document("review/deleteReview",
                        pathParameters(
                                parameterWithName("review-id").description("리뷰 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 (SUCCESS 또는 ERROR)"),
                                fieldWithPath("data").description("응답 데이터 (없음)").optional(),
                                fieldWithPath("error").ignored()
                        )
                ));
    }

    @Test
    @DisplayName("도서 리뷰 평균 점수 조회 성공")
    void getReviewAverage() throws Exception {
        long bookId = 1L;
        given(reviewService.getReviewAverage(anyLong())).willReturn(4.5);

        mockMvc.perform(get("/api/reviews/books/{book-id}/average", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").value(4.5))
                .andDo(print())
                .andDo(document("review/getReviewAverage",
                        pathParameters(
                                parameterWithName("book-id").description("도서 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 (SUCCESS 또는 ERROR)"),
                                fieldWithPath("data").description("도서 리뷰 평균 점수"),
                                fieldWithPath("error").ignored()
                        )
                ));
    }
}