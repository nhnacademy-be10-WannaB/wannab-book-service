package shop.wannab.book_service.review.controller;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.wannab.book_service.review.controller.request.ReviewCreateRequest;
import shop.wannab.book_service.review.controller.request.ReviewUpdateRequest;
import shop.wannab.book_service.review.controller.response.BookReviewListResponse;
import shop.wannab.book_service.review.controller.response.UserReviewListResponse;
import shop.wannab.book_service.review.service.ReviewService;
import shop.wannab.book_service.global.response.ApiResponse;

@RestController
@RequestMapping("/api/reviews")
@AllArgsConstructor
@Transactional
public class ReviewController {

    private final ReviewService reviewService;

    // 도서 리뷰 등록
    @PostMapping("/books/{book-id}")
    public ResponseEntity<ApiResponse<Void>> createReview(@PathVariable("book-id") Long bookId,
                                                          @RequestHeader("X-USER-ID") Long userId,
                                                          @RequestBody ReviewCreateRequest request) {
        reviewService.createReview(request, bookId, userId);
        reviewService.createReviewPoint(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 회원 기준 리뷰 목록 조회
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<UserReviewListResponse>>> getUserReviews(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestHeader("X-USER-ID") Long userId) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getUserReviewList(pageable, userId)));
    }

    // 도서 기준 리뷰 목록 조회
    @GetMapping("/books/{book-id}")
    public ResponseEntity<ApiResponse<Page<BookReviewListResponse>>> getBookReviews(
            @PageableDefault(size = 20) Pageable pageable,
            @PathVariable("book-id") Long bookId) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getBookReviewList(pageable, bookId)));
    }

    // 리뷰 수정
    @PutMapping("/{review-id}")
    public ResponseEntity<ApiResponse<Void>> updateReview(@PathVariable("review-id") Long reviewId,
                                                          @RequestBody ReviewUpdateRequest request) {
        reviewService.updateReview(request, reviewId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 리뷰 삭제
    @DeleteMapping("/{review-id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable("review-id") Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 도서 리뷰 평균점수 조회
    @GetMapping("/books/{book-id}/average")
    public ResponseEntity<ApiResponse<Double>> getReviewAverage(@PathVariable("book-id") Long bookId) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getReviewAverage(bookId)));
    }
}
