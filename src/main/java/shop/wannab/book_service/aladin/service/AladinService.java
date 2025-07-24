package shop.wannab.book_service.aladin.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.book_service.aladin.client.AladinClient;
import shop.wannab.book_service.aladin.client.request.SearchRequest;
import shop.wannab.book_service.aladin.client.response.SearchResponse;
import shop.wannab.book_service.aladin.controller.request.BookInfoRequest;
import shop.wannab.book_service.aladin.exception.AladinApiException;
import shop.wannab.book_service.aladin.exception.AladinErrorCode;
import shop.wannab.book_service.book.controller.request.AladinBookCreateRequest;
import shop.wannab.book_service.book.dto.BookIndexDocument;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.exception.BookApiException;
import shop.wannab.book_service.book.exception.BookErrorCode;
import shop.wannab.book_service.book.factory.BookAggregateFactory;
import shop.wannab.book_service.book.factory.BookIndexCommandFactory;
import shop.wannab.book_service.book.repository.BookRepository;
import shop.wannab.book_service.global.properties.AladinKeyProperties;

@Slf4j
@Service
@RequiredArgsConstructor
public class AladinService {

    private final AladinClient aladinClient;
    private final BookRepository bookRepository;
    private final BookAggregateFactory bookAggregateFactory;
    private final AladinKeyProperties aladinKeyProperties;
    private final BookIndexCommandFactory bookIndexCommandFactory;

    @Transactional(readOnly = true)
    public SearchResponse searchBooks(BookInfoRequest request) {
        SearchRequest params = SearchRequest.from(request);
        try{
            log.error("request : {} error", request);
            log.info("request : {}", request);
            return aladinClient.fetchFromAladin(params.toParamMap(aladinKeyProperties.getTtbKey()));
        } catch (FeignException e) {
            throw new AladinApiException(AladinErrorCode.ALDIN_API_ERROR, e);
        }

    }

    @Transactional
    public void aladinCreateBook(AladinBookCreateRequest request) {
        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new BookApiException(BookErrorCode.DUPLICATE_BOOK);
        }

        Book book = bookAggregateFactory.toAggregate(request);

        Book save = bookRepository.save(book);
        bookRepository.saveOrUpdateBookStock(book.getBookId(), book.getStock());

        bookIndexCommandFactory.index(BookIndexDocument.from(request, String.valueOf(save.getBookId())));
    }
}
