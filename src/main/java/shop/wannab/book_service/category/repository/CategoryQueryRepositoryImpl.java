package shop.wannab.book_service.category.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import shop.wannab.book_service.category.dto.response.CategoryResponse;
import shop.wannab.book_service.category.entity.QCategory;

@RequiredArgsConstructor
public class CategoryQueryRepositoryImpl implements CategoryQueryRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CategoryResponse> findParentCategories() {
        QCategory category = QCategory.category;

        return queryFactory
                .select(Projections.constructor(CategoryResponse.class,
                        category.id,
                        category.name))
                .from(category)
                .where(category.parent.isNull())
                .fetch();
    }

    @Override
    public List<CategoryResponse> findChildCategoriesByParentId(Long parentId) {
        QCategory category = QCategory.category;

        return queryFactory
                .select(Projections.constructor(CategoryResponse.class,
                        category.id,
                        category.name))
                .from(category)
                .where(category.parent.id.eq(parentId))
                .fetch();
    }
}
