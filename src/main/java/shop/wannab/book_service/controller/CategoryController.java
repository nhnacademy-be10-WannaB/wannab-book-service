package shop.wannab.book_service.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.wannab.book_service.entity.category.dto.CategoryHierarchyDto;
import shop.wannab.book_service.service.CategoryService;

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;


    // front-service가 호출할 API 엔드포인트
    @GetMapping("/api/categories/hierarchy")
    public ResponseEntity<List<CategoryHierarchyDto>> getCategoryHierarchy() {
        return ResponseEntity.ok(categoryService.getCategoryHierarchy());
    }

}
