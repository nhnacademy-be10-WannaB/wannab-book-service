package shop.wannab.book_service.entity.category.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import shop.wannab.book_service.entity.category.Category;

@Getter
@Setter
public class CategoryHierarchyDto {
    private Long id;
    private String name;
    private List<CategoryHierarchyDto> children;

    // Entity를 DTO로 변환하는 재귀적 생성자
    // 최상위 카테고리 예시 : IT/모바일
    public CategoryHierarchyDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        List<CategoryHierarchyDto> childDtoList = new ArrayList<>();

        // IT/모바일에 하위 카테고리인 [네트워크,데이터베이스,자료구조,알고리즘,정보통신,...]
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            // IT/모바일에 하위 카테고리들을 for each문으로 순차적 순회
            for (Category childEntity : category.getChildren()) {
                // IT/모바일에 하위 카테고리 또한 생성자 호출 -> 재귀적 구조 완성
                CategoryHierarchyDto childDto = new CategoryHierarchyDto(childEntity);
                childDtoList.add(childDto);
            }
        }
        this.children = childDtoList;
    }
}
