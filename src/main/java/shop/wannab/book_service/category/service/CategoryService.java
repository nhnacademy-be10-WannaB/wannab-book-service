package shop.wannab.book_service.category.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.book_service.book.entity.BookCategory;
import shop.wannab.book_service.book.repository.BookCategoryRepository;
import shop.wannab.book_service.category.dto.ParentCategoryDto;
import shop.wannab.book_service.category.entity.Category;
import shop.wannab.book_service.category.dto.CategoryHierarchyDto;
import shop.wannab.book_service.category.repository.CategoryRepository;


@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;

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

    @Transactional
    public Long getCategoryId(Long bookId){
        List<BookCategory> bookCategories = bookCategoryRepository.findCategoriesByBookIdWithFetchJoin(bookId);

        if (bookCategories.isEmpty()) {
            throw new EntityNotFoundException("해당 책에 대한 카테고리 정보를 찾을 수 없습니다. bookId: " + bookId);
        }

        return bookCategories.get(0).getCategory().getId();
    }

    @Transactional(readOnly = true)
    public List<ParentCategoryDto> getParentCategory() {
        List<Category> rootCategories = categoryRepository.findByParentIsNull();
        List<ParentCategoryDto> parentCategoryDtoList = new ArrayList<>();
        for (Category rootCategory : rootCategories) {
            ParentCategoryDto parentCategoryDto = new ParentCategoryDto(
                    rootCategory.getId(),
                    rootCategory.getName());
            parentCategoryDtoList.add(parentCategoryDto);
        }
        return parentCategoryDtoList;
    }
}
