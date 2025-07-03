package shop.wannab.book_service.aladin.service;

import feign.FeignException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.book_service.aladin.client.AladinClient;
import shop.wannab.book_service.aladin.client.request.SearchRequest;
import shop.wannab.book_service.aladin.client.response.SearchResponse;
import shop.wannab.book_service.aladin.controller.request.BookInfoRequest;
import shop.wannab.book_service.aladin.exception.AladinApiException;
import shop.wannab.book_service.aladin.exception.AladinErrorCode;
import shop.wannab.book_service.author.entity.Author;
import shop.wannab.book_service.author.repository.AuthorRepository;
import shop.wannab.book_service.book.controller.request.AladinBookCreateRequest;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.entity.BookAuthor;
import shop.wannab.book_service.book.entity.BookImage;
import shop.wannab.book_service.book.entity.BookPublisher;
import shop.wannab.book_service.book.exception.BookApiException;
import shop.wannab.book_service.book.exception.BookErrorCode;
import shop.wannab.book_service.book.repository.BookRepository;
import shop.wannab.book_service.publisher.entity.Publisher;
import shop.wannab.book_service.publisher.repository.PublisherRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AladinService {

    private final AladinClient aladinClient;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;

    @Value("${aladin.api.ttbkey}")
    private String ttbKey;

    @Transactional(readOnly = true)
    public SearchResponse searchBooks(BookInfoRequest request) {
        SearchRequest params = SearchRequest.from(request);
        try{
            return aladinClient.fetchFromAladin(params.toParamMap(ttbKey));
        } catch (FeignException e) {
            throw new AladinApiException(AladinErrorCode.ALDIN_API_ERROR, e);
        }
    }

    @Transactional
    public void aladinCreateBook(AladinBookCreateRequest request) {

        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new BookApiException(BookErrorCode.DUPLICATE_BOOK);
        }

        Book book = request.toEntity();
        List<BookAuthor> bookAuthors = request.authors().stream()
                .map(authorName ->{
                    Author author = authorRepository.findAuthorsByAuthorName(authorName)
                            .orElseGet(()->authorRepository.save(
                                    Author.builder().authorName(authorName).build()));
                    return BookAuthor.builder()
                            .book(book)
                            .author(author)
                            .build();
                }).toList();

        List<BookPublisher> bookPublishers = request.publishers().stream()
                .map(publisherName -> {
                    Publisher publisher = publisherRepository.findPublisherByPublisherName(publisherName)
                            .orElseGet(() -> publisherRepository.save(
                                    Publisher.builder().publisherName(publisherName).build()));
                    return BookPublisher.builder()
                            .book(book)
                            .publisher(publisher)
                            .build();
                }).toList();

        BookImage bookImage = BookImage.builder()
                .book(book)
                .imageUrl(request.thumbnail())
                .build();

        List<BookImage> bookImages = List.of(bookImage);

        book.getBookImages().addAll(bookImages);
        book.getBookAuthors().addAll(bookAuthors);
        book.getBookPublishers().addAll(bookPublishers);

        // TODO : 카테고리 추가도 필요함

        bookRepository.save(book);
        bookRepository.saveOrUpdateBookStock(book.getBookId(),book.getStock());
    }
}
