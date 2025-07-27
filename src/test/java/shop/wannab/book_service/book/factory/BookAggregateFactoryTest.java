package shop.wannab.book_service.book.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.wannab.book_service.author.repository.AuthorRepository;
import shop.wannab.book_service.book.controller.request.AladinBookCreateRequest;
import shop.wannab.book_service.book.controller.request.BookCreateRequest;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.entity.BookImage;
import shop.wannab.book_service.category.entity.Category;
import shop.wannab.book_service.category.service.CategoryService;
import shop.wannab.book_service.publisher.repository.PublisherRepository;

@ExtendWith(MockitoExtension.class)
class BookAggregateFactoryTest {

    @InjectMocks
    private BookAggregateFactory factory;

    @Mock private AuthorRepository authorRepository;
    @Mock private PublisherRepository publisherRepository;
    @Mock private CategoryService categoryService;

    @Test
    void toAggregate() {
        // given
        BookCreateRequest request = mock(BookCreateRequest.class);

        Book book = Book.builder().title("제목").description("설명").publicationDate(LocalDate.now())
                .bookChapter("챕터").isbn("isbn").status(true).salesPrice(9000).originPrice(10000).stock(10)
                .build();
        given(request.getAuthors()).willReturn(List.of("작가1", "작가2"));
        given(request.getPublishers()).willReturn(List.of("출판사1"));
        given(request.getCategories()).willReturn(List.of("IT"));
        given(request.getBookImages()).willReturn(List.of("img1.jpg", "img2.jpg"));
        given(request.toEntityWithOutAuthorAndPublisher())
                .willReturn(book);

        // authors
        given(authorRepository.findAllByAuthorNameIn(List.of("작가1", "작가2")))
                .willReturn(List.of());
        // publishers
        given(publisherRepository.findAllByPublisherNameIn(List.of("출판사1")))
                .willReturn(List.of());

        // categories
        Category category = new Category("IT", null);
        given(categoryService.ensureHierarchy(List.of("IT")))
                .willReturn(List.of(category));

        // when
        Book aggregateBook = factory.toAggregate(request);

        // then
        assertThat(aggregateBook.getBookAuthors())
                .extracting(it -> it.getAuthor().getAuthorName())
                .containsExactlyInAnyOrder("작가1", "작가2");

        assertThat(aggregateBook.getBookPublishers())
                .extracting(it -> it.getPublisher().getPublisherName())
                .containsExactly("출판사1");

        assertThat(aggregateBook.getBookCategories())
                .extracting(it -> it.getCategory().getName())
                .containsExactly("IT");

        assertThat(aggregateBook.getBookImages())
                .extracting(BookImage::getImageUrl)
                .containsExactly("img1.jpg", "img2.jpg");

        // verify save
        verify(authorRepository).saveAll(anyList());
        verify(publisherRepository).saveAll(anyList());
    }

    @Test
    void toAggregate_fromAladinRequest() {
        AladinBookCreateRequest req = mock(AladinBookCreateRequest.class);
        Book book = Book.builder().title("제목").description("설명").publicationDate(LocalDate.now())
                .bookChapter("챕터").isbn("isbn").status(true).salesPrice(9000).originPrice(10000).stock(10)
                .build();
        given(req.authors()).willReturn(List.of("남궁성"));
        given(req.publishers()).willReturn(List.of("도우출판"));
        given(req.category()).willReturn(List.of("프로그래밍"));
        given(req.thumbnail()).willReturn("img.jpg");
        given(req.toEntityWithOutAuthorAndPublisher())
                .willReturn(book);

        given(authorRepository.findAllByAuthorNameIn(List.of("남궁성")))
                .willReturn(List.of());
        given(publisherRepository.findAllByPublisherNameIn(List.of("도우출판")))
                .willReturn(List.of());
        given(categoryService.ensureHierarchy(List.of("프로그래밍")))
                .willReturn(List.of(new Category("프로그래밍", null)));

        Book aggregateBook = factory.toAggregate(req);

        assertThat(aggregateBook.getBookAuthors()).hasSize(1);
        assertThat(aggregateBook.getBookPublishers()).hasSize(1);
        assertThat(aggregateBook.getBookCategories()).hasSize(1);
        assertThat(aggregateBook.getBookImages()).extracting(BookImage::getImageUrl).contains("img.jpg");
    }
}