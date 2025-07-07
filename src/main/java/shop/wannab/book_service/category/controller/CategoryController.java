package shop.wannab.book_service.category.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.wannab.book_service.category.dto.CategoryCreateRequest;
import shop.wannab.book_service.category.dto.CategoryHierarchyDto;
import shop.wannab.book_service.category.dto.ParentCategoryDto;
import shop.wannab.book_service.category.service.CategoryService;

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;


    // front-service가 호출할 API 엔드포인트
    @GetMapping("/api/categories/hierarchy")
    public ResponseEntity<List<CategoryHierarchyDto>> getCategoryHierarchy() {
        return ResponseEntity.ok(categoryService.getCategoryHierarchy());
    }
    @GetMapping("/api/category")
    public ResponseEntity<Long> getCategoryId(@RequestParam Long bookId){
        return ResponseEntity.ok(categoryService.getCategoryId(bookId));
    }

    @GetMapping("/api/categories/parents")
    public ResponseEntity<List<ParentCategoryDto>> getParentCategory(){
        return ResponseEntity.ok(categoryService.getParentCategory());
    }

    @PostMapping("/api/categories/new")
    public ResponseEntity<Void> createCategory(@RequestBody CategoryCreateRequest request) {
        categoryService.createCategory(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
