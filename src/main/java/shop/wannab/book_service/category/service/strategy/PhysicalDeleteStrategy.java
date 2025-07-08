package shop.wannab.book_service.category.service.strategy;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.book_service.book.entity.QBookCategory;
import shop.wannab.book_service.category.entity.Category;
import shop.wannab.book_service.category.entity.QCategory;

/**
 * 해당 카테고리 데이터를 물리적으로 삭제하는 전략
 * 추후 논리적 삭제로 변경 예정
 */
@Component
@RequiredArgsConstructor
public class PhysicalDeleteStrategy implements CategoryDeleteStrategy {

    private final JPAQueryFactory queryFactory;

    private final QCategory category = QCategory.category;
    private final QBookCategory bookCategory = QBookCategory.bookCategory;

    @Override
    @Transactional
    public void delete(Category targetCategory) {
        Long categoryId = targetCategory.getId();

        List<Long> childCategoryIds = queryFactory
                .select(category.id)
                .from(category)
                .where(category.parent.id.eq(categoryId))
                .fetch();

        if (!childCategoryIds.isEmpty()) {
            queryFactory
                    .delete(bookCategory)
                    .where(bookCategory.category.id.in(childCategoryIds))
                    .execute();

            queryFactory
                    .delete(category)
                    .where(category.id.in(childCategoryIds))
                    .execute();
        }

        queryFactory
                .delete(bookCategory)
                .where(bookCategory.category.id.eq(categoryId))
                .execute();

        queryFactory
                .delete(category)
                .where(category.id.eq(categoryId))
                .execute();
    }
}

