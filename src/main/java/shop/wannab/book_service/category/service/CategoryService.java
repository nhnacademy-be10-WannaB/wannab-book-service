package shop.wannab.book_service.category.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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


@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final CategoryDeleteStrategyResolver categoryDeleteStrategyResolver;

    @Transactional(readOnly = true)
    public List<CategoryHierarchyDto> getCategoryHierarchy() {
        List<Category> rootCategories = categoryRepository.findByParentIsNull();

        List<CategoryHierarchyDto> categoryHierarchyDtoList = new ArrayList<>();

        for (Category rootCategory : rootCategories) {
            CategoryHierarchyDto dto = new CategoryHierarchyDto(rootCategory);
            categoryHierarchyDtoList.add(dto);
        }
        return categoryHierarchyDtoList;
    }

    @Transactional(readOnly = true)
    public List<Long> getAncestorCategoryIdsForBook(Long bookId) {
        List<BookCategory> bookCategories = bookCategoryRepository.findCategoriesByBookIdWithFetchJoin(bookId);

        Set<Long> allCategoryIds = new HashSet<>();

        for (BookCategory bookCategory : bookCategories) {
            Category currentCategory = bookCategory.getCategory();
            while (currentCategory != null) {
                allCategoryIds.add(currentCategory.getId());
                currentCategory = currentCategory.getParent();
            }
        }
        return new ArrayList<>(allCategoryIds);
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> getParentCategory(Pageable pageable) {
        return categoryRepository.findParentCategories(pageable);
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> findChildCategoriesByParentId(Long parentId, Pageable pageable) {
        return categoryRepository.findChildCategoriesByParentId(parentId, pageable);
    }

    @Transactional
    public void createParentCategory(CategoryCreateRequest request) {
        Category category = Category.create(request.name(), null);
        categoryRepository.save(category);
    }

    @Transactional
    public void createChildCategory(CategoryCreateRequest request, Long parentId) {
        Category parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new CategoryApiException(CategoryErrorCode.PARENTS_CATEGORY_NOT_FOUND));
        Category child = Category.create(request.name(), parent);
        categoryRepository.save(child);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        CategoryDeleteStrategy strategy = categoryDeleteStrategyResolver.resolve();
        strategy.delete(category);
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getCategoriesNames(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new HashMap<>();
        }

        Map<Long, String> categoryNames = new HashMap<>();
        for (Long categoryId : categoryIds) {
            categoryRepository.findById(categoryId)
                    .ifPresent(category -> categoryNames.put(category.getId(), category.getName()));
        }

        return categoryNames;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllParentCategory() {
        return categoryRepository.findParentCategories();
    }


    @Transactional
    public Map<Long, Set<Long>> findAllCategoryIdsWithHierarchy(List<Long> bookIds) {
        List<BookCategory> bookCategories = bookCategoryRepository.findBookCategoriesByBookIds(bookIds);

        Map<Long, Set<Long>> bookToAllCategoryIdsMap = new HashMap<>();

        for (BookCategory bc : bookCategories) {
            Long bookId = bc.getBook().getBookId();
            Category currentCategory = bc.getCategory();

            Set<Long> allCategoryIds = bookToAllCategoryIdsMap.computeIfAbsent(bookId, k -> new HashSet<>());

            while (currentCategory != null) {
                allCategoryIds.add(currentCategory.getId());
                currentCategory = currentCategory.getParent();
            }
        }
        return bookToAllCategoryIdsMap;
    }

    /**
     * depth-2 hierarchy 까지 지원
     * 입력된 카테고리가 1개면, 1개만 조회 및 없으면 저장
     * 2개면, 2개 조회 및 저장
     * 3개면, 1번, 2번 인덱스 조회 및 저장
     */
    @Transactional
    public List<Category> ensureHierarchy(List<String> names) {
        if (names == null || names.isEmpty()) {
            throw new CategoryApiException(CategoryErrorCode.INVALID_CATEGORY_HIERARCHY);
        }

        List<String> categoryNames = switch (names.size()) {
            case 1 -> List.of(names.get(0));
            case 2 -> List.of(names.get(0), names.get(1));
            default -> List.of(names.get(1), names.get(2));
        };

        List<Category> found = categoryRepository.findAllByNameIn(categoryNames);
        Map<String, Category> map = found.stream()
                .collect(Collectors.toMap(Category::getName, Function.identity()));

        if (names.size() == 1) {
            String parentName = names.getFirst();
            Category parent = map.computeIfAbsent(parentName, n -> {
                Category c = new Category();
                c.setName(n);
                return categoryRepository.save(c);
            });
            return List.of(parent);
        } else {
            String parentName = names.size() == 2 ? names.get(0) : names.get(1);
            String childName  = names.size() == 2 ? names.get(1) : names.get(2);

            Category parent = map.computeIfAbsent(parentName, n -> {
                Category c = new Category();
                c.setName(n);
                return categoryRepository.save(c);
            });

            Category child = map.computeIfAbsent(childName, n -> {
                Category c = new Category();
                c.setName(n);
                c.setParent(parent);
                return categoryRepository.save(c);
            });

            return List.of(parent, child);
        }
    }
}
