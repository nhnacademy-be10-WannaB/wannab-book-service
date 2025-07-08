package shop.wannab.book_service.category.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    public Long getCategoryId(Long bookId){
        List<BookCategory> bookCategories = bookCategoryRepository.findCategoriesByBookIdWithFetchJoin(bookId);

        if (bookCategories.isEmpty()) {
            throw new EntityNotFoundException("해당 책에 대한 카테고리 정보를 찾을 수 없습니다. bookId: " + bookId);
        }

        return bookCategories.get(0).getCategory().getId();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getParentCategory() {
        return categoryRepository.findParentCategories();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findChildCategoriesByParentId(Long parentId) {
        return categoryRepository.findChildCategoriesByParentId(parentId);
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
}
