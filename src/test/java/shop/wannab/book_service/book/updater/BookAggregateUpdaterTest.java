package shop.wannab.book_service.book.updater;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.wannab.book_service.author.entity.Author;
import shop.wannab.book_service.author.repository.AuthorRepository;
import shop.wannab.book_service.book.controller.request.BookUpdateRequest;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.category.entity.Category;
import shop.wannab.book_service.category.service.CategoryService;
import shop.wannab.book_service.publisher.entity.Publisher;
import shop.wannab.book_service.publisher.repository.PublisherRepository;
import shop.wannab.book_service.tag.entity.Tag;
import shop.wannab.book_service.tag.repository.TagRepository;

@ExtendWith(MockitoExtension.class)
class BookAggregateUpdaterTest {

    @InjectMocks private BookAggregateUpdater bookAggregateUpdater;

    @Mock private AuthorRepository authorRepository;
    @Mock private PublisherRepository publisherRepository;
    @Mock private TagRepository tagRepository;
    @Mock private CategoryService categoryService;

    @Test
    @DisplayName("BookUpdateRequest를 기반으로 Book aggregate를 갱신한다")
    void apply() {
        // given
        Book book = Mockito.spy(new Book());
        List<String> authors = List.of("남궁성");
        List<String> publishers = List.of("도우출판");
        List<String> tags = List.of("자바", "프로그래밍");
        List<String> categories = List.of("IT", "컴퓨터");
        Category cat1 = new Category("IT", null);
        Category cat2 = new Category("컴퓨터", null);

        BookUpdateRequest req = BookUpdateRequest.builder()
                .title("자바의 정석")
                .description("설명")
                .publicationDate(LocalDate.of(2023, 1, 1))
                .originPrice(30000)
                .salesPrice(27000)
                .stock(100)
                .bookChapter("1장~10장")
                .isbn("123456789")
                .status(true)
                .authors(authors)
                .publishers(publishers)
                .bookTags(tags)
                .bookImages(List.of("img1.jpg", "img2.jpg"))
                .categories(categories)
                .build();

        Author author = new Author("남궁성");
        given(authorRepository.findAllByAuthorNameIn(authors)).willReturn(List.of(author));
        given(publisherRepository.findAllByPublisherNameIn(publishers))
                .willReturn(List.of(new Publisher("도우출판")));
        given(tagRepository.findAllByNameIn(tags))
                .willReturn(List.of(new Tag("자바"), new Tag("프로그래밍")));
        given(categoryService.ensureHierarchy(List.of("IT", "컴퓨터")))
                .willReturn(List.of(cat1, cat2));

        // when
        bookAggregateUpdater.apply(book, req);

        // then
        verify(book).updateInfo("자바의 정석", "설명", LocalDate.of(2023, 1, 1), 30000,
                27000, 100, "1장~10장", "123456789", true
        );

        verify(authorRepository).findAllByAuthorNameIn(authors);
        verify(publisherRepository).findAllByPublisherNameIn(publishers);
        verify(tagRepository).findAllByNameIn(tags);
        verify(categoryService).ensureHierarchy(categories);
    }
}