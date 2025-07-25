package shop.wannab.book_service.category.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.wannab.book_service.category.entity.Category;

@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@NoArgsConstructor
public class CategoryHierarchyDto {
    private Long id;
    private String name;
    private List<CategoryHierarchyDto> children;

    public CategoryHierarchyDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        List<CategoryHierarchyDto> childDtoList = new ArrayList<>();

        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            for (Category childEntity : category.getChildren()) {
                CategoryHierarchyDto childDto = new CategoryHierarchyDto(childEntity);
                childDtoList.add(childDto);
            }
        }
        this.children = childDtoList;
    }
}
