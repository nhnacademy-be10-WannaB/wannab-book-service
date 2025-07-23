package shop.wannab.book_service.aladin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import feign.FeignException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.wannab.book_service.aladin.client.AladinClient;
import shop.wannab.book_service.aladin.client.request.SearchRequest;
import shop.wannab.book_service.aladin.client.response.SearchResponse;
import shop.wannab.book_service.aladin.controller.request.BookInfoRequest;
import shop.wannab.book_service.aladin.exception.AladinApiException;
import shop.wannab.book_service.book.controller.request.AladinBookCreateRequest;
import shop.wannab.book_service.book.dto.BookIndexDocument;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.entity.BookImage;
import shop.wannab.book_service.book.exception.BookApiException;
import shop.wannab.book_service.book.exception.BookErrorCode;
import shop.wannab.book_service.book.factory.BookAggregateFactory;
import shop.wannab.book_service.book.factory.BookIndexCommandFactory;
import shop.wannab.book_service.book.repository.BookRepository;
import shop.wannab.book_service.global.properties.AladinKeyProperties;

@ActiveProfiles("ci")
@DisplayName("AladinService 슬라이스 테스트")
@ExtendWith(MockitoExtension.class)
class AladinServiceTest {

    @InjectMocks AladinService aladinService;

    @Mock private AladinClient aladinClient;
    @Mock private BookRepository bookRepository;
    @Mock private BookAggregateFactory bookAggregateFactory;
    @Mock private AladinKeyProperties aladinKeyProperties;
    @Mock private BookIndexCommandFactory bookIndexCommandFactory;


    @Test
    @DisplayName("알라딘 API로 책을 검색할 수 있다")
    void searchBooks() {
        // given
        BookInfoRequest request = BookInfoRequest.builder()
                .query("워너비")
                .build();
        SearchResponse response = new SearchResponse(
                "워너비", 1, 1, 1,
                "워너비", "1", "카테고리", List.of());

        given(aladinKeyProperties.getTtbKey()).willReturn("dummy-Key");
        given(aladinClient.fetchFromAladin(SearchRequest.from(request).toParamMap("dummy-Key"))).willReturn(response);

        // when
        SearchResponse searchResponse = aladinService.searchBooks(request);

        // then
        assertThat(searchResponse).isEqualTo(response);
    }

    @Test
    @DisplayName("알라딘 API 호출 실패 시 예외를 던진다")
    void searchBooks_apiFailure_throwsException() {
        // given
        BookInfoRequest request = BookInfoRequest.builder()
                .query("워너비")
                .build();

        given(aladinKeyProperties.getTtbKey()).willReturn("dummy-key");
        given(aladinClient.fetchFromAladin(anyMap()))
                .willThrow(FeignException.InternalServerError.class);

        // when & then
        assertThrows(AladinApiException.class, () -> aladinService.searchBooks(request));
    }

    @Test
    @DisplayName("알라딘 API로 검색한 데이터로 책을 등록한다")
    void aladinCreateBook() {
        // given
        AladinBookCreateRequest request = new AladinBookCreateRequest(
                "워너비", List.of("작가1", "작가2"), List.of("출판사"), LocalDate.of(2025, 7, 23),
                "isbn-01", 1000, "설명", "이미지", List.of("카테고리"), 0, "챕터", true
        );
        BookImage dummyBookImage = BookImage.builder().imageUrl("이미지").build();
        Book dummyBook = Book.builder()
                .bookId(1L).title("워너비").isbn("isbn-01").description("설명")
                .originPrice(1000).salesPrice(1000).stock(0)
                .bookImages(List.of(dummyBookImage)).publicationDate(LocalDate.of(2025, 7, 23))
                .build();


        given(bookRepository.existsByIsbn(request.isbn())).willReturn(false);
        given(bookAggregateFactory.toAggregate(any(AladinBookCreateRequest.class))).willReturn(dummyBook);

        Book savedBook = Book.builder()
                .bookId(1L)
                .title(dummyBook.getTitle())
                .isbn(dummyBook.getIsbn())
                .stock(dummyBook.getStock())
                .build();
        given(bookRepository.save(any(Book.class))).willReturn(savedBook);

        // when
        aladinService.aladinCreateBook(request);

        // then
        verify(bookRepository).existsByIsbn("isbn-01");
        verify(bookRepository).save(any(Book.class));
        verify(bookRepository).saveOrUpdateBookStock(1L, dummyBook.getStock());
        verify(bookIndexCommandFactory).index(any(BookIndexDocument.class));

    }

    @Test
    @DisplayName("중복된 ISBN이 존재하면 BookApiException을 던진다")
    void aladinCreateBook_duplicateIsbn_throwsException() {
        // given
        AladinBookCreateRequest request = new AladinBookCreateRequest(
                "워너비", List.of("작가1"), List.of("출판사"), LocalDate.of(2025, 7, 23),
                "isbn-01", 1000, "설명", "이미지", List.of("카테고리"), 10, "챕터", true
        );

        given(bookRepository.existsByIsbn("isbn-01")).willReturn(true);

        // when & then
        BookApiException exception = assertThrows(BookApiException.class,
                () -> aladinService.aladinCreateBook(request));

        assertThat(exception.getErrorCode()).isEqualTo(BookErrorCode.DUPLICATE_BOOK);

        verify(bookRepository, never()).save(any());
        verify(bookRepository, never()).saveOrUpdateBookStock(anyLong(), anyInt());
        verify(bookIndexCommandFactory, never()).index(any());
    }
}