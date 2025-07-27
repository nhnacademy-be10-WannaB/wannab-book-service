package shop.wannab.book_service.tag.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.entity.BookTag;
import shop.wannab.book_service.book.entity.QBookTag;
import shop.wannab.book_service.book.repository.BookRepository;
import shop.wannab.book_service.global.config.QuerydslConfig;
import shop.wannab.book_service.tag.dto.response.TagResponse;
import shop.wannab.book_service.tag.entity.Tag;

@DataJpaTest
@ActiveProfiles("ci")
@DisplayName("TagQueryRepositoryImplTest 테스트")
@Import({QuerydslConfig.class, TagQueryRepositoryImpl.class})
class TagQueryRepositoryImplTest {

    @Autowired
    @Qualifier("tagQueryRepositoryImpl")
    private TagQueryRepository tagQueryRepository;

    @Autowired private TagRepository tagRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private JPAQueryFactory queryFactory;
    @Autowired private EntityManager em;

    @MockBean private RedisTemplate<String, Object> redisTemplate;

    @Test
    @DisplayName("searchTags - 키워드가 있을 때는 필터링 + 조회된다")
    void searchTags() {
        // given
        tagRepository.save(Tag.create("Java"));
        tagRepository.save(Tag.create("Spring"));
        tagRepository.save(Tag.create("JPA"));
        tagRepository.save(Tag.create("javascript"));
        em.flush(); em.clear();

        Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());

        // when
        Page<TagResponse> page = tagQueryRepository.searchTags("ja", pageable);

        // then
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getContent())
                .extracting(TagResponse::name)
                .containsExactlyInAnyOrder("Java", "javascript");
    }

    @Test
    @DisplayName("searchTags - 키워드가 없으면 전체를 페이징 조회한다")
    void searchTags_withoutKeyword() {
        // given
        tagRepository.save(new Tag("Java"));
        tagRepository.save(new Tag("Spring"));
        tagRepository.save(new Tag("JPA"));
        em.flush(); em.clear();

        Pageable pageable = PageRequest.of(0, 2);

        // when
        Page<TagResponse> page = tagQueryRepository.searchTags(null, pageable);

        // then
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("deleteTagWithBookTags - 태그에 매핑된 BookTag를 먼저 지우고 마지막에 Tag를 삭제한다")
    void deleteTagWithBookTags() {
        // given
        Tag tag = tagRepository.save(new Tag("Tech"));

        Book b1 = Book.builder().title("book1").description("설명1").publicationDate(LocalDate.now())
                .isbn("isbn1").originPrice(1000).salesPrice(1000).stock(10).status(true).bookChapter("챕터")
                .build();
        Book b2 = Book.builder().title("book2").description("설명2").publicationDate(LocalDate.now())
                .isbn("isbn2").originPrice(1000).salesPrice(1000).stock(10).status(true).bookChapter("챕터")
                .build();
        em.persist(b1);
        em.persist(b2);

        BookTag bt1 = new BookTag(b1, tag);
        BookTag bt2 = new BookTag(b2, tag);
        em.persist(bt1);
        em.persist(bt2);

        Long tagId = tag.getId();
        em.flush();
        em.clear();

        // when
        tagQueryRepository.deleteTagWithBookTags(tagId);
        em.flush();
        em.clear();

        // then
        Long bookTagCnt = queryFactory
                .select(QBookTag.bookTag.count())
                .from(QBookTag.bookTag)
                .where(QBookTag.bookTag.tag.id.eq(tagId))
                .fetchOne();

        assertThat(bookTagCnt).isZero();
        assertThat(tagRepository.findById(tagId)).isEmpty();
        assertThat(bookRepository.count()).isEqualTo(2);
    }
}