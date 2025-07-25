package shop.wannab.book_service.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.entity.BookCategory;
import shop.wannab.book_service.book.repository.BookCategoryRepository;
import shop.wannab.book_service.category.dto.CategoryHierarchyDto;
import shop.wannab.book_service.category.dto.request.CategoryCreateRequest;
import shop.wannab.book_service.category.dto.response.CategoryResponse;
import shop.wannab.book_service.category.entity.Category;
import shop.wannab.book_service.category.exception.CategoryApiException;
import shop.wannab.book_service.category.exception.CategoryErrorCode;
import shop.wannab.book_service.category.repository.CategoryRepository;
import shop.wannab.book_service.category.service.strategy.CategoryDeleteStrategy;
import shop.wannab.book_service.category.service.strategy.CategoryDeleteStrategyResolver;


@ActiveProfiles("ci")
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryServiceTest 단위 테스트")
class CategoryServiceTest {

    @InjectMocks private CategoryService categoryService;

    @Mock private CategoryRepository categoryRepository;
    @Mock private BookCategoryRepository bookCategoryRepository;
    @Mock private CategoryDeleteStrategyResolver categoryDeleteStrategyResolver;
    @Mock private CategoryDeleteStrategy categoryDeleteStrategy;

    @Test
    @DisplayName("getCategoryHierarchy는 루트 카테고리 및 자식 계층까지 DTO로 변환한다")
    void getCategoryHierarchy() {
        // given
        Category parent = new Category("부모", null);
        Category child1 = new Category("자식1", parent);
        Category child2 = new Category("자식2", parent);

        parent.setChildren(List.of(child1, child2));

        Category otherParent = new Category("부모2", null);

        given(categoryRepository.findRootsWithChildren())
                .willReturn(List.of(parent, otherParent));

        // when
        List<CategoryHierarchyDto> result = categoryService.getCategoryHierarchy();

        // then
        assertThat(result).hasSize(2);

        assertThat(result).extracting(CategoryHierarchyDto::getName)
                .containsExactlyInAnyOrder("부모", "부모2");

        CategoryHierarchyDto parentDto = result.stream()
                .filter(dto -> dto.getName().equals("부모"))
                .findFirst()
                .orElseThrow();

        assertThat(parentDto.getChildren()).hasSize(2);
        assertThat(parentDto.getChildren()).extracting(CategoryHierarchyDto::getName)
                .containsExactlyInAnyOrder("자식1", "자식2");

        CategoryHierarchyDto otherParentDto = result.stream()
                .filter(dto -> dto.getName().equals("부모2"))
                .findFirst()
                .orElseThrow();

        assertThat(otherParentDto.getChildren()).isEmpty();
    }

    @Test
    @DisplayName("getAncestorCategoryIdsForBook은 Book이 가진 모든 상위 카테고리 ID를 반환한다")
    void getAncestorCategoryIdsForBook() {
        // given
        Category grandParent = mock(Category.class);
        Category parent = mock(Category.class);
        Category child = mock(Category.class);

        given(grandParent.getId()).willReturn(1L);
        given(parent.getId()).willReturn(2L);
        given(child.getId()).willReturn(3L);

        given(child.getParent()).willReturn(parent);
        given(parent.getParent()).willReturn(grandParent);
        given(grandParent.getParent()).willReturn(null);

        BookCategory bc = mock(BookCategory.class);
        given(bc.getCategory()).willReturn(child);

        given(bookCategoryRepository.findCategoriesByBookIdWithFetchJoin(1L))
                .willReturn(List.of(bc));

        // when
        List<Long> result = categoryService.getAncestorCategoryIdsForBook(1L);

        // then
        assertThat(result).containsExactlyInAnyOrder(
                grandParent.getId(), parent.getId(), child.getId()
        );
    }

    @Test
    @DisplayName("getParentCategory는 부모 카테고리를 페이징해서 반환한다")
    void getParentCategory() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        CategoryResponse c1 = new CategoryResponse(1L, "부모1");
        Page<CategoryResponse> mockPage = new PageImpl<>(List.of(c1), pageable, 1);

        given(categoryRepository.findParentCategories(pageable)).willReturn(mockPage);

        // when
        Page<CategoryResponse> result = categoryService.getParentCategory(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().name()).isEqualTo("부모1");
    }

    @Test
    @DisplayName("findChildCategoriesByParentId는 자식 카테고리를 페이징해서 반환한다")
    void findChildCategoriesByParentId() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        CategoryResponse c1 = new CategoryResponse(2L, "자식1");
        Page<CategoryResponse> mockPage = new PageImpl<>(List.of(c1), pageable, 1);

        given(categoryRepository.findChildCategoriesByParentId(1L, pageable)).willReturn(mockPage);

        // when
        Page<CategoryResponse> result = categoryService.findChildCategoriesByParentId(1L, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().name()).isEqualTo("자식1");
    }

    @Test
    @DisplayName("createParentCategory는 부모 카테고리를 생성하고 저장한다")
    void createParentCategory() {
        // given
        CategoryCreateRequest request = new CategoryCreateRequest("컴퓨터");

        // when
        categoryService.createParentCategory(request);

        // then
        verify(categoryRepository).save(argThat(category ->
                category.getName().equals("컴퓨터") &&
                        category.getParent() == null
        ));
    }

    @Test
    @DisplayName("createChildCategory는 부모 ID를 기반으로 자식 카테고리를 생성하고 저장한다")
    void createChildCategory() {
        // given
        Category parent = new Category("부모", null);
        ReflectionTestUtils.setField(parent, "id", 1L);
        given(categoryRepository.findById(1L)).willReturn(Optional.of(parent));

        CategoryCreateRequest request = new CategoryCreateRequest("자식");

        // when
        categoryService.createChildCategory(request, 1L);

        // then
        verify(categoryRepository).save(argThat(child ->
                child.getName().equals("자식") &&
                        child.getParent().equals(parent)
        ));
    }

    @Test
    @DisplayName("deleteCategory는 삭제 전략을 통해 삭제를 수행한다")
    void deleteCategory() {
        // given
        Category category = new Category("삭제할 카테고리", null);
        ReflectionTestUtils.setField(category, "id", 1L);
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        given(categoryDeleteStrategyResolver.resolve()).willReturn(categoryDeleteStrategy);

        // when
        categoryService.deleteCategory(1L);

        // then
        verify(categoryDeleteStrategy).delete(category);
    }

    @Test
    @DisplayName("getCategoriesNames는 ID 리스트에 해당하는 카테고리명을 Map으로 반환한다")
    void getCategoriesNames() {
        // given
        Category c1 = new Category("컴퓨터", null);
        Category c2 = new Category("모바일", null);
        ReflectionTestUtils.setField(c1, "id", 1L);
        ReflectionTestUtils.setField(c2, "id", 2L);

        given(categoryRepository.findById(1L)).willReturn(Optional.of(c1));
        given(categoryRepository.findById(2L)).willReturn(Optional.of(c2));

        // when
        Map<Long, String> result = categoryService.getCategoriesNames(List.of(1L, 2L));

        // then
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
                1L, "컴퓨터",
                2L, "모바일"
        ));
    }

    @Test
    @DisplayName("getAllParentCategory는 모든 부모 카테고리를 반환한다")
    void getAllParentCategory() {
        // given
        List<CategoryResponse> mockList = List.of(
                new CategoryResponse(1L, "부모1"),
                new CategoryResponse(2L, "부모2")
        );
        given(categoryRepository.findParentCategories()).willReturn(mockList);

        // when
        List<CategoryResponse> result = categoryService.getAllParentCategory();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(CategoryResponse::name)
                .containsExactlyInAnyOrder("부모1", "부모2");
    }

    @Test
    @DisplayName("findAllCategoryIdsWithHierarchy는 책 ID에 해당하는 모든 상위 카테고리 ID를 맵으로 반환한다")
    void findAllCategoryIdsWithHierarchy() {
        // given
        Book book1 = mock(Book.class);
        given(book1.getBookId()).willReturn(1L);

        Category grandParent = new Category("조부모", null);
        Category parent = new Category("부모", grandParent);
        Category child = new Category("자식", parent);

        ReflectionTestUtils.setField(grandParent, "id", 10L);
        ReflectionTestUtils.setField(parent, "id", 20L);
        ReflectionTestUtils.setField(child, "id", 30L);

        BookCategory bc = mock(BookCategory.class);
        given(bc.getBook()).willReturn(book1);
        given(bc.getCategory()).willReturn(child);

        given(bookCategoryRepository.findBookCategoriesByBookIds(List.of(1L)))
                .willReturn(List.of(bc));

        // when
        Map<Long, Set<Long>> result = categoryService.findAllCategoryIdsWithHierarchy(List.of(1L));

        // then
        assertThat(result).containsKey(1L);
        assertThat(result.get(1L)).containsExactlyInAnyOrder(10L, 20L, 30L);
    }

    @Test
    @DisplayName("ensureHierarchy - 카테고리 이름이 1개일 경우, 해당 카테고리가 없으면 저장하고 반환")
    void ensureHierarchy_single() {
        // given
        String name = "컴퓨터";
        List<String> names = List.of(name);
        given(categoryRepository.findAllByNameIn(List.of(name))).willReturn(List.of());

        Category saved = new Category();
        saved.setName(name);
        ReflectionTestUtils.setField(saved, "id", 1L);
        given(categoryRepository.save(any(Category.class))).willReturn(saved);

        // when
        List<Category> result = categoryService.ensureHierarchy(names);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo(name);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("ensureHierarchy - 이름이 2개일 경우, 부모/자식 순서로 조회/저장 후 반환")
    void ensureHierarchy_twoLevels() {
        // given
        String parentName = "IT";
        String childName = "개발";

        List<String> names = List.of(parentName, childName);

        Category parent = new Category();
        parent.setName(parentName);
        ReflectionTestUtils.setField(parent, "id", 1L);

        given(categoryRepository.findAllByNameIn(List.of(parentName, childName)))
                .willReturn(List.of(parent));

        Category savedChild = new Category();
        savedChild.setName(childName);
        savedChild.setParent(parent);
        ReflectionTestUtils.setField(savedChild, "id", 2L);
        given(categoryRepository.save(any(Category.class))).willReturn(savedChild);

        // when
        List<Category> result = categoryService.ensureHierarchy(names);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo(parentName);
        assertThat(result.get(1).getName()).isEqualTo(childName);
        assertThat(result.get(1).getParent()).isEqualTo(parent);
    }

    @Test
    @DisplayName("ensureHierarchy - 이름이 3개일 경우, 1번,2번 인덱스 이름으로 계층을 구성하고 없으면 저장")
    void ensureHierarchy_threeLevels() {
        // given
        List<String> names = List.of("전자", "노트북", "게이밍");

        Category parent = new Category();
        parent.setName("노트북");
        ReflectionTestUtils.setField(parent, "id", 1L);

        Category child = new Category();
        child.setName("게이밍");
        child.setParent(parent);
        ReflectionTestUtils.setField(child, "id", 2L);

        given(categoryRepository.findAllByNameIn(List.of("노트북", "게이밍")))
                .willReturn(List.of());

        given(categoryRepository.save(argThat(c -> c != null && "노트북".equals(c.getName())))).willReturn(parent);
        given(categoryRepository.save(argThat(c -> c != null && "게이밍".equals(c.getName())))).willReturn(child);

        // when
        List<Category> result = categoryService.ensureHierarchy(names);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("노트북");
        assertThat(result.get(1).getName()).isEqualTo("게이밍");
        assertThat(result.get(1).getParent()).isEqualTo(parent);
    }

    @Test
    @DisplayName("ensureHierarchy - 빈 리스트가 입력되면 예외가 발생한다")
    void ensureHierarchy_emptyList() {
        // expect
        assertThatThrownBy(() -> categoryService.ensureHierarchy(List.of()))
                .isInstanceOf(CategoryApiException.class)
                .hasMessageContaining(CategoryErrorCode.INVALID_CATEGORY_HIERARCHY.getMessage());
    }
}