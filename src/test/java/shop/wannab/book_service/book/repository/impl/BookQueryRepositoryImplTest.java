package shop.wannab.book_service.book.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import shop.wannab.book_service.author.entity.Author;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.repository.projection.BookInfoProjection;
import shop.wannab.book_service.global.config.QuerydslConfig;
import shop.wannab.book_service.publisher.entity.Publisher;
import shop.wannab.book_service.tag.entity.Tag;

@DataJpaTest
@ActiveProfiles("ci")
@DisplayName("BookQueryRepositoryImpl 테스트")
@Import({BookQueryRepositoryImpl.class, QuerydslConfig.class})
class BookQueryRepositoryImplTest {

    @Autowired
    @Qualifier("bookQueryRepositoryImpl")
    private BookQueryRepositoryImpl bookQueryRepository;
    @Autowired EntityManager em;

    @MockBean private RedisTemplate<String, Object> redisTemplate;


    @Test
    void findPageIds() {
        // given
        Book b1 = Book.builder().title("제목").description("설명").publicationDate(LocalDate.now())
                .bookChapter("챕터").isbn("isbn").status(true).salesPrice(9000).originPrice(10000).stock(10)
                .build();
        Book b2 = Book.builder().title("제목2").description("설명2").publicationDate(LocalDate.now())
                .bookChapter("챕터2").isbn("isbn2").status(true).salesPrice(9000).originPrice(10000).stock(10)
                .build();

        em.persist(b1);
        em.persist(b2);
        em.flush();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());

        // when
        List<Long> ids = bookQueryRepository.findPageIds("제목", pageable);

        // then
        assertThat(ids).containsExactly(b1.getBookId(), b2.getBookId());
    }

    @Test
    void fetchDetails() {
        // given
        Book b1 = Book.builder().title("자바의 정석").description("설명").publicationDate(LocalDate.now())
                .bookChapter("챕터").isbn("isbn").status(true).salesPrice(9000).originPrice(10000).stock(10)
                .build();
        Author author = new Author("남궁성");
        Publisher publisher = new Publisher("도우출판");
        Tag tag = new Tag("프로그래밍");

        em.persist(author);
        em.persist(publisher);
        em.persist(tag);
        em.persist(b1);

        b1.addAuthor(author);
        b1.addPublisher(publisher);
        b1.addTag(tag);
        b1.addImage("thumb.jpg");

        em.flush();

        // when
        List<BookInfoProjection> result = bookQueryRepository.fetchDetails(List.of(b1.getBookId()));

        // then
        assertThat(result).hasSize(1);
        BookInfoProjection info = result.getFirst();
        assertThat(info.title()).isEqualTo("자바의 정석");
        assertThat(info.authorName()).isEqualTo("남궁성");
        assertThat(info.publisherName()).isEqualTo("도우출판");
        assertThat(info.tagName()).isEqualTo("프로그래밍");
        assertThat(info.imageUrl()).isEqualTo("thumb.jpg");
    }

    @Test
    void countAll() {
        Book b1 = Book.builder().title("자바의 정석").description("설명").publicationDate(LocalDate.now())
                .bookChapter("챕터").isbn("isbn").status(true).salesPrice(9000).originPrice(10000).stock(10)
                .build();
        Book b2 = Book.builder().title("자바 웹 프로그래밍").description("설명2").publicationDate(LocalDate.now())
                .bookChapter("챕터2").isbn("isbn2").status(true).salesPrice(9000).originPrice(10000).stock(10)
                .build();
        Book b3 = Book.builder().title("파이썬 프로그래밍").description("설명2").publicationDate(LocalDate.now())
                .bookChapter("챕터2").isbn("isbn2").status(true).salesPrice(9000).originPrice(10000).stock(10)
                .build();

        // given
        em.persist(b1);
        em.persist(b2);
        em.persist(b3);

        em.flush();

        // when
        long count = bookQueryRepository.countAll("자바");

        // then
        assertThat(count).isEqualTo(2L);
    }
}