package shop.wannab.book_service.review.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.exception.BookApiException;
import shop.wannab.book_service.book.exception.BookErrorCode;
import shop.wannab.book_service.book.repository.BookRepository;
import shop.wannab.book_service.client.UserClient;
import shop.wannab.book_service.client.dto.response.UserResponseWrapper;
import shop.wannab.book_service.review.controller.request.ReviewCreateRequest;
import shop.wannab.book_service.review.controller.request.ReviewUpdateRequest;
import shop.wannab.book_service.review.controller.response.BookReviewListResponse;
import shop.wannab.book_service.review.controller.response.UserReviewListResponse;
import shop.wannab.book_service.review.entity.Review;
import shop.wannab.book_service.review.entity.ReviewImage;
import shop.wannab.book_service.review.exception.ReviewApiException;
import shop.wannab.book_service.review.exception.ReviewErrorCode;
import shop.wannab.book_service.review.repository.ReviewRepository;
import shop.wannab.book_service.review.service.ReviewService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserClient userClient;
    private final BookRepository bookRepository;

    // 도서 리뷰 리스트 조회
    @Override
    @Transactional(readOnly = true)
    public Page<BookReviewListResponse> getBookReviewList(Pageable pageable, Long bookId) {

        if (!bookRepository.existsById(bookId)){
            throw new BookApiException(BookErrorCode.BOOK_NOT_FOUND);
        }

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

    //회원 리뷰 리스트 조회
    @Transactional(readOnly = true)
    public Page<UserReviewListResponse> getUserReviewList(Pageable pageable, Long userId){
        Page<Review> reviews = reviewRepository.findByUserId(userId,pageable);
        return reviews.map(UserReviewListResponse::from);
    }

    //리뷰 생성
    public void createReview(ReviewCreateRequest request, Long bookId, Long userId){
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new BookApiException(BookErrorCode.BOOK_NOT_FOUND));

        if (reviewRepository.existsByBook_BookIdAndUserId(bookId,userId)){
            throw new ReviewApiException(ReviewErrorCode.REVIEW_ALREADY_EXISTS);
        }


        Review review = Review.builder()
                .userId(userId)
                .book(book)
                .obId(request.getObId())
                .reviewScore(request.getReviewScore())
                .reviewCreatedAt(request.getReviewCreatedAt())
                .reviewContent(request.getReviewContent())
                .build();

        List<ReviewImage> reviewImages = request.getReviewImages().stream()
                .map(url -> {
                    ReviewImage image = ReviewImage.builder()
                            .reviewImageUrl(url)
                            .build();
                    image.setReview(review);
                    return image;
                })
                .toList();

        review.setReviewImages(reviewImages);
        reviewRepository.save(review);
    }

    //리뷰 수정
    public void updateReview(ReviewUpdateRequest request, Long reviewId){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()->new ReviewApiException(ReviewErrorCode.REVIEW_NOT_FOUND));

        review.updateInfo(request.getReviewContent(), request.getReviewScore(), request.getReviewUpdatedAt());
    }

    //리뷰 삭제
    public void deleteReview(Long reviewId){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()->new ReviewApiException(ReviewErrorCode.REVIEW_NOT_FOUND));
        reviewRepository.delete(review);
    }

    // 리뷰 평균 조회
    @Transactional(readOnly = true)
    public Double getReviewAverage(Long bookId) {

        if (!bookRepository.existsById(bookId)){
            throw new BookApiException(BookErrorCode.BOOK_NOT_FOUND);
        }

        Double averageScore = reviewRepository.findAverageScoreByBookId(bookId);
        if (averageScore == null) {
            averageScore = 0.0;
        }
        return averageScore;
    }
}
