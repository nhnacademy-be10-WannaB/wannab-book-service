package shop.wannab.book_service.book.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.book_service.author.entity.Author;
import shop.wannab.book_service.author.repository.AuthorRepository;
import shop.wannab.book_service.book.controller.request.BookCreateRequest;
import shop.wannab.book_service.book.controller.request.BookUpdateRequest;
import shop.wannab.book_service.book.controller.response.BookListResponse;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.entity.BookAuthor;
import shop.wannab.book_service.book.entity.BookImage;
import shop.wannab.book_service.book.entity.BookPublisher;
import shop.wannab.book_service.book.exception.BookApiException;
import shop.wannab.book_service.book.exception.BookErrorCode;
import shop.wannab.book_service.book.repository.BookRepository;
import shop.wannab.book_service.publisher.entity.Publisher;
import shop.wannab.book_service.publisher.repository.PublisherRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminBookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;

    //도서 리스트 조회
    @Transactional(readOnly = true)
    public Page<BookListResponse> getBookList(Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);
        return books.map(BookListResponse::from);
    }

    // 도서 생성
    public void createBook(BookCreateRequest request) {

        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BookApiException(BookErrorCode.DUPLICATE_BOOK);
        }

        Book book = request.toEntity();
        List<BookAuthor> bookAuthors = request.getAuthors().stream()
                .map(authorName ->{
                    Author author = authorRepository.findAuthorsByAuthorName(authorName)
                            .orElseGet(()->authorRepository.save(
                                    Author.builder().authorName(authorName).build()));
                    return BookAuthor.builder()
                            .book(book)
                            .author(author)
                            .build();
                }).toList();

        List<BookPublisher> bookPublishers = request.getPublishers().stream()
                .map(publisherName -> {
                    Publisher publisher = publisherRepository.findPublisherByPublisherName(publisherName)
                            .orElseGet(() -> publisherRepository.save(
                                    Publisher.builder().publisherName(publisherName).build()));
                    return BookPublisher.builder()
                            .book(book)
                            .publisher(publisher)
                            .build();
                }).toList();

        List<BookImage> bookImages = request.getBookImages().stream()
                .map(imageUrl -> BookImage.builder()
                        .book(book)
                        .imageUrl(imageUrl)
                        .build())
                .toList();

        book.getBookImages().addAll(bookImages);
        book.getBookAuthors().addAll(bookAuthors);
        book.getBookPublishers().addAll(bookPublishers);

        bookRepository.save(book);
        bookRepository.saveOrUpdateBookStock(book.getBookId(),book.getStock());

    }

    //도서 수정
    public void updateBook(Long bookId, BookUpdateRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new BookApiException(BookErrorCode.BOOK_NOT_FOUND));

        book.updateInfo(
                request.getTitle(),
                request.getDescription(),
                request.getPublicationDate(),
                request.getOriginPrice(),
                request.getSalesPrice(),
                request.getStock(),
                request.getBookChapter(),
                request.getIsbn(),
                request.isStatus());

        book.getBookAuthors().clear();
        book.getBookPublishers().clear();

        List<BookAuthor> updatedAuthors = request.getAuthors().stream()
                .map(name -> {
                    Author author = authorRepository.findAuthorsByAuthorName(name)
                            .orElseGet(() -> authorRepository.save(
                                    Author.builder().authorName(name).build()));
                    return BookAuthor.builder()
                            .book(book)
                            .author(author)
                            .build();
                }).toList();

        List<BookPublisher> updatedPublishers = request.getPublishers().stream()
                .map(name -> {
                    Publisher publisher = publisherRepository.findPublisherByPublisherName(name)
                            .orElseGet(() -> publisherRepository.save(
                                    Publisher.builder().publisherName(name).build()));
                    return BookPublisher.builder()
                            .book(book)
                            .publisher(publisher)
                            .build();
                }).toList();

        List<BookImage> bookImages = request.getBookImages().stream()
                .map(imageUrl -> BookImage.builder()
                        .book(book)
                        .imageUrl(imageUrl)
                        .build())
                .toList();

        book.getBookImages().addAll(bookImages);
        book.getBookAuthors().addAll(updatedAuthors);
        book.getBookPublishers().addAll(updatedPublishers);
        bookRepository.saveOrUpdateBookStock(book.getBookId(),book.getStock());
    }

    //도서 삭제
    public void deleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()->  new BookApiException(BookErrorCode.BOOK_NOT_FOUND));
        bookRepository.delete(book);
        bookRepository.deleteBookStock(bookId);
    }

}