package shop.wannab.book_service.review.controller;

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
import shop.wannab.book_service.review.controller.request.ReviewCreateRequest;
import shop.wannab.book_service.review.controller.request.ReviewUpdateRequest;
import shop.wannab.book_service.review.controller.response.BookReviewListResponse;
import shop.wannab.book_service.review.controller.response.UserReviewListResponse;
import shop.wannab.book_service.review.service.ReviewService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        // given
        long bookId = 1L;
        long userId = 1L;
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .reviewContent("updated content")
                .reviewScore(4)
                .reviewImages(java.util.Collections.emptyList())
                .build();

        doNothing().when(reviewService).createReview(any(ReviewCreateRequest.class), anyLong(), anyLong());

        // when & then
        mockMvc.perform(post("/api/reviews/books/{book-id}", bookId)
                        .header("X-USER-ID", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 기준 리뷰 목록 조회 성공")
    void getUserReviews() throws Exception {
        // given
        long userId = 1L;
        Page<UserReviewListResponse> response = new PageImpl<>(Collections.emptyList());
        given(reviewService.getUserReviewList(any(PageRequest.class), anyLong())).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/reviews/me")
                        .header("X-USER-ID", userId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print());
    }

    @Test
    @DisplayName("도서 기준 리뷰 목록 조회 성공")
    void getBookReviews() throws Exception {
        // given
        long bookId = 1L;
        Page<BookReviewListResponse> response = new PageImpl<>(Collections.emptyList());
        given(reviewService.getBookReviewList(any(PageRequest.class), anyLong())).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/reviews/books/{book-id}", bookId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print());
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReview() throws Exception {
        // given
        long reviewId = 1L;
        ReviewUpdateRequest request = ReviewUpdateRequest.builder()
                .reviewContent("updated content")
                .reviewScore(4)
                .reviewUpdatedAt(java.time.LocalDateTime.now())
                .reviewImages(java.util.Collections.emptyList())
                .build();
        doNothing().when(reviewService).updateReview(any(ReviewUpdateRequest.class), anyLong());

        // when & then
        mockMvc.perform(put("/api/reviews/{review-id}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print());
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void deleteReview() throws Exception {
        // given
        long reviewId = 1L;
        doNothing().when(reviewService).deleteReview(anyLong());

        // when & then
        mockMvc.perform(delete("/api/reviews/{review-id}", reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andDo(print());
    }

    @Test
    @DisplayName("도서 리뷰 평균 점수 조회 성공")
    void getReviewAverage() throws Exception {
        // given
        long bookId = 1L;
        given(reviewService.getReviewAverage(anyLong())).willReturn(4.5);

        // when & then
        mockMvc.perform(get("/api/reviews/books/{book-id}/average", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").value(4.5))
                .andDo(print());
    }
}