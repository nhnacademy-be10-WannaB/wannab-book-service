package shop.wannab.book_service.review.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.exception.BookNotFoundException;
import shop.wannab.book_service.book.repository.BookRepository;
import shop.wannab.book_service.client.UserClient;
import shop.wannab.book_service.client.dto.response.UserResponseWrapper;
import shop.wannab.book_service.review.dto.request.ReviewCreateRequest;
import shop.wannab.book_service.review.dto.request.ReviewUpdateRequest;
import shop.wannab.book_service.review.dto.response.BookReviewListResponse;
import shop.wannab.book_service.review.dto.response.UserReviewListResponse;
import shop.wannab.book_service.review.entity.Review;
import shop.wannab.book_service.review.exception.ReviewNotFoundException;
import shop.wannab.book_service.review.repository.ReviewRepository;
import shop.wannab.book_service.review.service.ReviewService;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserClient userClient;
    private final BookRepository bookRepository;

    @Override
    public Page<BookReviewListResponse> getBookReviewList(Pageable pageable, Long bookId) {
        Page<Review> reviews = reviewRepository.findByBook_BookId(bookId, pageable);

        return reviews.map(review -> {
            Long userId = review.getUserId();
            String userName = null;

            try {
                UserResponseWrapper userResponseWrapper = userClient.getUserInfo(userId);
                if ("SUCCESS".equals(userResponseWrapper.getStatus())) {
                    userName = userResponseWrapper.getData().getUsername();
                }
            } catch (Exception e) {
                userName = "알 수 없음";
            }

            return BookReviewListResponse.from(review, userName);
        });
    }

    public Page<UserReviewListResponse> getUserReviewList(Pageable pageable, Long userId){
        Page<Review> reviews = reviewRepository.findByUserId(userId,pageable);
        return reviews.map(UserReviewListResponse::from);
    }

    public void createReview(ReviewCreateRequest request, Long bookId, Long userId){
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new BookNotFoundException());

        Review review = Review.builder()
                .userId(userId)
                .book(book)
                .obId(request.getObId())
                .reviewScore(request.getReviewScore())
                .reviewCreatedAt(request.getReviewCreatedAt())
                .reviewContent(request.getReviewContent())
                .build();
        reviewRepository.save(review);
    }

    public void updateReview(ReviewUpdateRequest request, Long reviewId){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()->new ReviewNotFoundException());

        review.updateInfo(request.getReviewContent(), request.getReviewScore(), request.getReviewUpdatedAt());
    }

    public void deleteReview(Long reviewId){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()->new ReviewNotFoundException());
        reviewRepository.delete(review);
    }

    public Double getReviewAverage(Long bookId) {
        Double averageScore = reviewRepository.findAverageScoreByBookId(bookId);
        if (averageScore == null) {
            averageScore = 0.0;
        }
        return averageScore;
    }
}
