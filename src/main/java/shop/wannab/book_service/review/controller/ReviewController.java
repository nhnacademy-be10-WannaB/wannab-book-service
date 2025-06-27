package shop.wannab.book_service.review.controller;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.HeaderParam;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.wannab.book_service.review.dto.request.ReviewCreateRequest;
import shop.wannab.book_service.review.dto.request.ReviewUpdateRequest;
import shop.wannab.book_service.review.dto.response.BookReviewListResponse;
import shop.wannab.book_service.review.dto.response.UserReviewListResponse;
import shop.wannab.book_service.review.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
@AllArgsConstructor
@Transactional
public class ReviewController {
    private final ReviewService reviewService;

    // 도서 리뷰 등록
    @PostMapping("/books/{book-id}")
    public ResponseEntity<Void> createReview(@PathVariable("book-id") Long bookId,
                                             @RequestHeader("X-USER-ID") Long userId,
                                             @RequestBody ReviewCreateRequest request){
        reviewService.createReview(request,bookId,userId);
        return ResponseEntity.ok().build();
    }

    // 회원 기준 리뷰 목록 조회
    @GetMapping("/me")
    public Page<UserReviewListResponse> getUserReviews(@PageableDefault(size = 20) Pageable pageable,
                                                       @RequestHeader("X-USER-ID") Long userId){
        return reviewService.getUserReviewList(pageable,userId);
    }

    // 도서 기준 리뷰 목록 조회
    @GetMapping("/books/{book-id}")
    public Page<BookReviewListResponse> getBookReviews(@PageableDefault(size = 20) Pageable pageable,
                                                       @PathVariable("book-id") Long bookId){
        return reviewService.getBookReviewList(pageable, bookId);
    }

    // 리뷰 수정
    @PutMapping("/{review-id}")
    public ResponseEntity<Void> updateReview(@PathVariable("review-id") Long reviewId,
                                             @RequestBody ReviewUpdateRequest request){
        reviewService.updateReview(request,reviewId);
        return ResponseEntity.ok().build();
    }

    //리뷰 삭제
    @DeleteMapping("/{review-id}")
    public ResponseEntity<Void> deleteReview(@PathVariable("review-id") Long reviewId){
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }

    //도서 리뷰 평균점수 조회
    @GetMapping("/books/{book-id}/average")
    public Double getReviewAverage(@PathVariable("book-id") Long bookId){
        return reviewService.getReviewAverage(bookId);
    }
}
