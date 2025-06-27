package shop.wannab.book_service.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.book_service.author.entity.Author;
import shop.wannab.book_service.author.repository.AuthorRepository;
import shop.wannab.book_service.book.dto.request.BookCreateRequest;
import shop.wannab.book_service.book.dto.request.BookUpdateRequest;
import shop.wannab.book_service.book.dto.response.BookListResponse;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.entity.BookAuthor;
import shop.wannab.book_service.book.entity.BookPublisher;
import shop.wannab.book_service.book.exception.BookNotFoundException;
import shop.wannab.book_service.book.repository.BookRepository;
import shop.wannab.book_service.publisher.entity.Publisher;
import shop.wannab.book_service.publisher.repository.PublisherRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminBookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;

    //도서 리스트 조회
    public Page<BookListResponse> getBookList(Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);
        return books.map(BookListResponse::from);
    }

    // 도서 생성
    public void createBook(BookCreateRequest request) {
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

        book.getBookAuthors().addAll(bookAuthors);
        book.getBookPublishers().addAll(bookPublishers);

        bookRepository.save(book);
    }

    //도서 수정
    public void updateBook(Long bookId, BookUpdateRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new BookNotFoundException());

        book.updateInfo(
                request.getTitle(),
                request.getDescription(),
                request.getPublicationDate(),
                request.getOriginPrice(),
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

        book.getBookAuthors().addAll(updatedAuthors);
        book.getBookPublishers().addAll(updatedPublishers);
    }

    public void deleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new BookNotFoundException());
        bookRepository.delete(book);
    }
}