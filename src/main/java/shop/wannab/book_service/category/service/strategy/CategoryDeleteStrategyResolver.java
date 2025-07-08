package shop.wannab.book_service.category.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryDeleteStrategyResolver {

    private final PhysicalDeleteStrategy physicalDeleteStrategy;
    private final SoftDeleteStrategy softDeleteStrategy;

    public CategoryDeleteStrategy resolve() {
        return physicalDeleteStrategy;
    }
}
