package shop.wannab.book_service.category.repository.query;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import shop.wannab.book_service.book.controller.request.BookCreateRequest;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.entity.BookCategory;
import shop.wannab.book_service.book.repository.BookCategoryRepository;
import shop.wannab.book_service.book.repository.BookRepository;
import shop.wannab.book_service.category.dto.response.CategoryResponse;
import shop.wannab.book_service.category.entity.Category;
import shop.wannab.book_service.category.repository.CategoryQueryRepository;
import shop.wannab.book_service.category.repository.CategoryQueryRepositoryImpl;
import shop.wannab.book_service.category.repository.CategoryRepository;
import shop.wannab.book_service.global.config.QuerydslConfig;

@DataJpaTest
@DisplayName("CategoryQueryRepositoryImpl 테스트")
@ActiveProfiles("ci")
@Import({QuerydslConfig.class, CategoryQueryRepositoryImpl.class})
class CategoryQueryRepositoryImplTest {

    @Autowired
    @Qualifier("categoryQueryRepositoryImpl")
    CategoryQueryRepository categoryQueryRepository;

    @Autowired CategoryRepository categoryRepository;
    @Autowired BookCategoryRepository bookCategoryRepository;
    @Autowired BookRepository bookRepository;
    @Autowired EntityManager em;

    @MockBean private RedisTemplate<String, Object> redisTemplate;

    @Test
    @DisplayName("부모카테고리 조회 테스트")
    void testFindParentCategories() {
        // given
        Category parent = categoryRepository.save(new Category("부모", null));
        categoryRepository.save(new Category("자식", parent));

        // when
        List<CategoryResponse> result = categoryQueryRepository.findParentCategories();

        // then
        CategoryResponse first = result.getFirst();
        assertThat(result).hasSize(1);
        assertThat(first.id()).isEqualTo(parent.getId());
        assertThat(first.name()).isEqualTo(parent.getName());
    }

    @Test
    @DisplayName("부모카테고리 페이징 조회 테스트")
    void testFindParentCategoriesWithPaging() {
        // given
        Category parent1 = categoryRepository.save(new Category("부모1", null));
        categoryRepository.save(new Category("부모2", null));
        categoryRepository.save(new Category("자식", parent1));

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<CategoryResponse> result = categoryQueryRepository.findParentCategories(pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);

        List<String> parentNames = result.getContent().stream()
                .map(CategoryResponse::name)
                .toList();

        assertThat(parentNames).containsExactlyInAnyOrder("부모1", "부모2");
    }

    @Test
    @DisplayName("자식카테고리 페이징 조회 테스트")
    void findChildCategoriesByParentId() {
        // given
        Category parent = categoryRepository.save(new Category("부모", null));
        categoryRepository.save(new Category("자식1", parent));
        categoryRepository.save(new Category("자식2", parent));
        Category otherParent = categoryRepository.save(new Category("다른부모", null));
        categoryRepository.save(new Category("다른부모의 자식", otherParent));

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<CategoryResponse> result = categoryQueryRepository.findChildCategoriesByParentId(parent.getId(), pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);

        List<String> names = result.getContent().stream()
                .map(CategoryResponse::name)
                .toList();

        assertThat(names).containsExactlyInAnyOrder("자식1", "자식2");
    }

    @Test
    @DisplayName("부모 카테고리 삭제 시 - 자식 카테고리와 해당 BookCategory 모두 삭제된다")
    void deleteParentWithChildren_and_bookCategories() {
        // given
        Category parent = categoryRepository.save(new Category("부모", null));
        Category child1 = categoryRepository.save(new Category("자식1", parent));
        Category child2 = categoryRepository.save(new Category("자식2", parent));
        BookCreateRequest request1 = BookCreateRequest.builder().title("book1").description("설명")
                .publicationDate(LocalDate.now()).originPrice(1000).stock(10).isbn("book1-isbn").status(true)
                .build();
        BookCreateRequest request2 = BookCreateRequest.builder().title("book2").description("설명")
                .publicationDate(LocalDate.now()).originPrice(1000).stock(10).isbn("book2-isbn").status(true)
                .build();

        Book book1 = bookRepository.save(request1.toEntityWithOutAuthorAndPublisher());
        Book book2 = bookRepository.save(request2.toEntityWithOutAuthorAndPublisher());

        bookCategoryRepository.save(new BookCategory(book1, parent));
        bookCategoryRepository.save(new BookCategory(book1, child1));
        bookCategoryRepository.save(new BookCategory(book2, child2));
        em.flush();
        em.clear();

        // when
        categoryQueryRepository.deleteCategoryWithBookCategories(parent);
        em.flush();
        em.clear();

        // then
        assertThat(categoryRepository.findById(parent.getId())).isEmpty();
        assertThat(categoryRepository.findById(child1.getId())).isEmpty();
        assertThat(categoryRepository.findById(child2.getId())).isEmpty();

        assertThat(bookCategoryRepository.count()).isZero();
        assertThat(bookRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("자식 포함된 최상위 카테고리 조회 테스트")
    void findRootsWithChildren() {
        // given
        Category parent1 = categoryRepository.save(new Category("부모1", null));
        categoryRepository.save(new Category("자식1", parent1));
        categoryRepository.save(new Category("자식2", parent1));
        categoryRepository.save(new Category("자식3", parent1));
        categoryRepository.save(new Category("부모2", null));
        em.flush();
        em.clear();

        // when
        List<Category> rootsWithChildren = categoryQueryRepository.findRootsWithChildren();

        // then
        assertThat(rootsWithChildren).hasSize(2);
        List<String> rootNames = rootsWithChildren.stream()
                .map(Category::getName)
                .toList();
        assertThat(rootNames).containsExactlyInAnyOrder("부모1", "부모2");


        Category foundParent1 = rootsWithChildren.stream()
                .filter(c -> c.getId().equals(parent1.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(foundParent1.getChildren())
                .extracting(Category::getName)
                .containsExactlyInAnyOrder("자식1", "자식2", "자식3");
    }
}