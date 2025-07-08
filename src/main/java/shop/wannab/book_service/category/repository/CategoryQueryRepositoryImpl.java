package shop.wannab.book_service.category.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    public Page<CategoryResponse> findParentCategories(Pageable pageable) {
        QCategory category = QCategory.category;

        List<CategoryResponse> content = queryFactory
                .select(Projections.constructor(CategoryResponse.class,
                        category.id,
                        category.name))
                .from(category)
                .where(category.parent.isNull())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(category.count())
                .from(category)
                .where(category.parent.isNull())
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public Page<CategoryResponse> findChildCategoriesByParentId(Long parentId, Pageable pageable) {
        QCategory category = QCategory.category;

        List<CategoryResponse> content = queryFactory
                .select(Projections.constructor(CategoryResponse.class,
                        category.id,
                        category.name))
                .from(category)
                .where(category.parent.id.eq(parentId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(category.count())
                .from(category)
                .where(category.parent.id.eq(parentId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
