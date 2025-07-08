package shop.wannab.book_service.category.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.wannab.book_service.category.dto.CategoryHierarchyDto;
import shop.wannab.book_service.category.dto.request.CategoryCreateRequest;
import shop.wannab.book_service.category.dto.response.CategoryResponse;
import shop.wannab.book_service.category.service.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/hierarchy")
    public ResponseEntity<List<CategoryHierarchyDto>> getCategoryHierarchy() {
        return ResponseEntity.ok(categoryService.getCategoryHierarchy());
    }

    @GetMapping("/parents")
    public ResponseEntity<List<CategoryResponse>> getParentCategory(){
        return ResponseEntity.ok(categoryService.getParentCategory());
    }

    /**
     * 카테고리 정보를 조회하는 API
     *
     * @param parentId 부모 카테고리 ID (nullable)
     * @return 부모 또는 자식 카테고리 목록
     * @apiNote
     * parentId가 null 경우, 최상위(부모) 카테고리 목록을 반환
     * parentId가 지정된 경우, 해당 ID에 속한 자식 카테고리 목록을 반환
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findCategories(@RequestParam(required = false) Long parentId) {
        if (parentId == null) {
            List<CategoryResponse> parentCategories = categoryService.getParentCategory();
            return ResponseEntity.ok(parentCategories);
        }
        List<CategoryResponse> childCategories = categoryService.findChildCategoriesByParentId(parentId);
        return ResponseEntity.ok(childCategories);
    }

    /**
     * 부모 카테고리 생성 API
     * @param request 카테고리 이름
     */
    @PostMapping
    public ResponseEntity<Void> createParentCategory(@RequestBody @Valid CategoryCreateRequest request) {
        categoryService.createParentCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 자식 카테고리 생성 API
     * @param request 카테고리 이름
     * @param parentId 부모 카테고리 PK
     */
    @PostMapping("/{parentId}/children")
    public ResponseEntity<Void> createChildCategory(@RequestBody @Valid CategoryCreateRequest request,
                                                    @PathVariable Long parentId) {
        categoryService.createChildCategory(request, parentId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 카테고리 삭제 API
     * @param categoryId 카테고리 PK
     */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
