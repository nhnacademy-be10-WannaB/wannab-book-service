package shop.wannab.book_service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import shop.wannab.book_service.author.entity.Author;
import shop.wannab.book_service.author.repository.AuthorRepository;
import shop.wannab.book_service.book.entity.*;
import shop.wannab.book_service.book.repository.BookLikeRepository;
import shop.wannab.book_service.book.repository.BookRepository;
import shop.wannab.book_service.publisher.entity.Publisher;
import shop.wannab.book_service.publisher.repository.PublisherRepository;
import shop.wannab.book_service.review.entity.Review;
import shop.wannab.book_service.review.entity.ReviewImage;
import shop.wannab.book_service.review.repository.ReviewRepository;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class DataInitializer implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final BookLikeRepository bookLikeRepository;

    @Override
    public void run(String... args) {
        if (authorRepository.count() == 0) {
            // 작가, 출판사 저장
            Author author = authorRepository.save(
                    Author.builder().authorName("J.K. Rowling").build());
            Publisher publisher = publisherRepository.save(
                    Publisher.builder().publisherName("Bloomsbury").build());

            // 도서 생성
            Book book = Book.builder()
                    .title("Harry Potter")
                    .description("Fantasy novel")
                    .publicationDate(LocalDate.of(2000, 7, 8))
                    .isbn("9780747532743")
                    .originPrice(20000)
                    .stock(100)
                    .status(true)
                    .build();

            // 도서 - 작가, 출판사 관계 설정
            book.getBookAuthors().add(BookAuthor.builder().book(book).author(author).build());
            book.getBookPublishers().add(BookPublisher.builder().book(book).publisher(publisher).build());

            // 도서 이미지 추가
            book.getBookImages().add(BookImage.builder()
                    .book(book)
                    .imageUrl("https://shopping-phinf.pstatic.net/main_3249140/32491401626.20231004072435.jpg?type=w300")
                    .build());

            // 도서 저장
            Book savedBook = bookRepository.save(book);

            // 리뷰 생성
            Review review = Review.builder()
                    .userId(1L)
                    .obId(101L)
                    .book(savedBook)
                    .reviewContent("정말 재미있게 읽었어요!")
                    .reviewScore(5)
                    .reviewCreatedAt(LocalDate.now().atStartOfDay())
                    .build();

            // 리뷰 이미지 추가
            review.getReviewImages().add(ReviewImage.builder()
                    .review(review)
                    .reviewImageUrl("https://shopping-phinf.pstatic.net/main_3249140/32491401626.20231004072435.jpg?type=w300")
                    .build());

            // 리뷰 저장
            reviewRepository.save(review);

            // 도서 좋아요
            bookLikeRepository.save(BookLike.builder()
                    .book(savedBook)
                    .userId(1L)
                    .build());
        }
    }
}
