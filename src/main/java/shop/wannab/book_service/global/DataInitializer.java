package shop.wannab.book_service.global;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import shop.wannab.book_service.author.entity.Author;
import shop.wannab.book_service.author.repository.AuthorRepository;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.entity.BookAuthor;
import shop.wannab.book_service.book.entity.BookPublisher;
import shop.wannab.book_service.book.repository.BookRepository;
import shop.wannab.book_service.publisher.entity.Publisher;
import shop.wannab.book_service.publisher.repository.PublisherRepository;

import java.time.LocalDate;

import shop.wannab.book_service.review.entity.Review;
import shop.wannab.book_service.review.repository.ReviewRepository;

@RequiredArgsConstructor
@Component
public class DataInitializer implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public void run(String... args) {
        if (authorRepository.count() == 0) {
            Author author = authorRepository.save(Author.builder().authorName("J.K. Rowling").build());
            Publisher publisher = publisherRepository.save(Publisher.builder().publisherName("Bloomsbury").build());

            Book book = Book.builder()
                    .title("Harry Potter")
                    .description("Fantasy novel")
                    .publicationDate(LocalDate.of(2000, 7, 8))
                    .isbn("9780747532743")
                    .originPrice(20000)
                    .stock(100)
                    .status(true)
                    .build();

            BookAuthor bookAuthor = BookAuthor.builder().book(book).author(author).build();
            BookPublisher bookPublisher = BookPublisher.builder().book(book).publisher(publisher).build();

            book.getBookAuthors().add(bookAuthor);
            book.getBookPublishers().add(bookPublisher);

            Book savedBook = bookRepository.save(book);


            Review review = Review.builder()
                    .userId(1L)
                    .obId(101L)
                    .book(savedBook)
                    .reviewContent("정말 재미있게 읽었어요!")
                    .reviewScore(5)
                    .reviewCreatedAt(LocalDate.now().atStartOfDay())
                    .build();

            reviewRepository.save(review);
        }
    }
}
