package shop.wannab.book_service.review.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.wannab.book_service.review.controller.request.ReviewCreateRequest;
import shop.wannab.book_service.review.controller.request.ReviewUpdateRequest;
import shop.wannab.book_service.review.controller.response.BookReviewListResponse;
import shop.wannab.book_service.review.controller.response.UserReviewListResponse;

public interface ReviewService {
    Page<BookReviewListResponse> getBookReviewList(Pageable pageable, Long bookId);

    Page<UserReviewListResponse> getUserReviewList(Pageable pageable, Long userId);

    void createReview(ReviewCreateRequest request, Long bookId, Long userId);

    void updateReview(ReviewUpdateRequest request, Long reviewId);

    void deleteReview(Long reviewId);

    Double getReviewAverage(Long bookId);

}
