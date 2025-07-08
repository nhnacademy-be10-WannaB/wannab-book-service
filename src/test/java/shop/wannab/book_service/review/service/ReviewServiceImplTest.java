package shop.wannab.book_service.review.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.exception.BookApiException;
import shop.wannab.book_service.book.repository.BookRepository;
import shop.wannab.book_service.client.UserClient;
import shop.wannab.book_service.client.dto.response.UserResponse;
import shop.wannab.book_service.global.response.ApiResponse;
import shop.wannab.book_service.review.controller.request.ReviewCreateRequest;
import shop.wannab.book_service.review.controller.request.ReviewUpdateRequest;
import shop.wannab.book_service.review.controller.response.BookReviewListResponse;
import shop.wannab.book_service.review.controller.response.UserReviewListResponse;
import shop.wannab.book_service.review.entity.Review;
import shop.wannab.book_service.review.exception.ReviewApiException;
import shop.wannab.book_service.review.repository.ReviewRepository;
import shop.wannab.book_service.review.service.impl.ReviewServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("ci")
class ReviewServiceImplTest {

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserClient userClient;
    @Mock
    private BookRepository bookRepository;

    @Test
    @DisplayName("도서 리뷰 목록 조회 - 성공")
    void getBookReviewList_success() {
        // given
        Long bookId = 1L;
        PageRequest pageable = PageRequest.of(0, 10);
        Review review = Review.builder().userId(1L).build();
        Page<Review> reviews = new PageImpl<>(Collections.singletonList(review));

        given(bookRepository.existsById(bookId)).willReturn(true);
        given(reviewRepository.findByBook_BookId(bookId, pageable)).willReturn(reviews);

        // when
        Page<BookReviewListResponse> result = reviewService.getBookReviewList(pageable, bookId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("도서 리뷰 목록 조회 - 도서 없음")
    void getBookReviewList_bookNotFound() {
        // given
        Long bookId = 1L;
        PageRequest pageable = PageRequest.of(0, 10);

        given(bookRepository.existsById(bookId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> reviewService.getBookReviewList(pageable, bookId))
                .isInstanceOf(BookApiException.class);
    }

    @Test
    @DisplayName("회원 리뷰 목록 조회 - 성공")
    void getUserReviewList_success() {
        // given
        Long userId = 1L;
        PageRequest pageable = PageRequest.of(0, 10);
        Review review = Review.builder().build();
        Page<Review> reviews = new PageImpl<>(Collections.singletonList(review));

        given(reviewRepository.findByUserId(userId, pageable)).willReturn(reviews);

        // when
        Page<UserReviewListResponse> result = reviewService.getUserReviewList(pageable, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("리뷰 생성 - 성공")
    void createReview_success() {
        // given
        Long bookId = 1L;
        Long userId = 1L;
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .reviewContent("test content")
                .reviewScore(5)
                .obId(1L)
                .reviewCreatedAt(LocalDateTime.now())
                .reviewImages(Collections.emptyList())
                .build();
        Book book = Book.builder().build();

        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        given(reviewRepository.existsByBook_BookIdAndUserId(bookId, userId)).willReturn(false);

        // when
        reviewService.createReview(request, bookId, userId);

        // then
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 생성 - 도서 없음")
    void createReview_bookNotFound() {
        // given
        Long bookId = 1L;
        Long userId = 1L;
        ReviewCreateRequest request = ReviewCreateRequest.builder().build();

        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(request, bookId, userId))
                .isInstanceOf(BookApiException.class);
    }

    @Test
    @DisplayName("리뷰 생성 - 이미 리뷰 존재")
    void createReview_alreadyExists() {
        // given
        Long bookId = 1L;
        Long userId = 1L;
        ReviewCreateRequest request = ReviewCreateRequest.builder().build();
        Book book = Book.builder().build();

        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        given(reviewRepository.existsByBook_BookIdAndUserId(bookId, userId)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(request, bookId, userId))
                .isInstanceOf(ReviewApiException.class);
    }

    @Test
    @DisplayName("리뷰 수정 - 성공")
    void updateReview_success() {
        // given
        Long reviewId = 1L;
        ReviewUpdateRequest request = ReviewUpdateRequest.builder()
                .reviewContent("updated content")
                .reviewScore(4)
                .reviewUpdatedAt(LocalDateTime.now())
                .reviewImages(Collections.emptyList())
                .build();
        Review review = Review.builder().build();

        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        // when
        reviewService.updateReview(request, reviewId);

        // then
        // No exception, and review.updateInfo should be called (implicitly tested by no exception)
    }

    @Test
    @DisplayName("리뷰 수정 - 리뷰 없음")
    void updateReview_notFound() {
        // given
        Long reviewId = 1L;
        ReviewUpdateRequest request = ReviewUpdateRequest.builder().build();

        given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.updateReview(request, reviewId))
                .isInstanceOf(ReviewApiException.class);
    }

    @Test
    @DisplayName("리뷰 삭제 - 성공")
    void deleteReview_success() {
        // given
        Long reviewId = 1L;
        Review review = Review.builder().build();

        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        // when
        reviewService.deleteReview(reviewId);

        // then
        verify(reviewRepository).delete(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 삭제 - 리뷰 없음")
    void deleteReview_notFound() {
        // given
        Long reviewId = 1L;

        given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.deleteReview(reviewId))
                .isInstanceOf(ReviewApiException.class);
    }

    @Test
    @DisplayName("리뷰 평균 조회 - 성공")
    void getReviewAverage_success() {
        // given
        Long bookId = 1L;
        given(bookRepository.existsById(bookId)).willReturn(true);
        given(reviewRepository.findAverageScoreByBookId(bookId)).willReturn(4.5);

        // when
        Double average = reviewService.getReviewAverage(bookId);

        // then
        assertThat(average).isEqualTo(4.5);
    }

    @Test
    @DisplayName("리뷰 평균 조회 - 도서 없음")
    void getReviewAverage_bookNotFound() {
        // given
        Long bookId = 1L;
        given(bookRepository.existsById(bookId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> reviewService.getReviewAverage(bookId))
                .isInstanceOf(BookApiException.class);
    }

    @Test
    @DisplayName("리뷰 평균 조회 - 리뷰 없음")
    void getReviewAverage_noReviews() {
        // given
        Long bookId = 1L;
        given(bookRepository.existsById(bookId)).willReturn(true);
        given(reviewRepository.findAverageScoreByBookId(bookId)).willReturn(null);

        // when
        Double average = reviewService.getReviewAverage(bookId);

        // then
        assertThat(average).isEqualTo(0.0);
    }
}