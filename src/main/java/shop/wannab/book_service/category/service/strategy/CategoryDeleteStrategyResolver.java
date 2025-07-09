package shop.wannab.book_service.category.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryDeleteStrategyResolver {

    private final PhysicalCategoryDeleteStrategy physicalCategoryDeleteStrategy;
    private final SoftCategoryDeleteStrategy softCategoryDeleteStrategy;

    public CategoryDeleteStrategy resolve() {
        return physicalCategoryDeleteStrategy;
    }
}
