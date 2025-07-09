package shop.wannab.book_service.category.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import shop.wannab.book_service.book.entity.QBookCategory;
import shop.wannab.book_service.category.dto.response.CategoryResponse;
import shop.wannab.book_service.category.entity.Category;
import shop.wannab.book_service.category.entity.QCategory;

@RequiredArgsConstructor
public class CategoryQueryRepositoryImpl implements CategoryQueryRepository{

    private final JPAQueryFactory queryFactory;

    private final QCategory category = QCategory.category;
    private final QBookCategory bookCategory = QBookCategory.bookCategory;

    @Override
    public List<CategoryResponse> findParentCategories() {
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

    @Override
    public void deleteCategoryWithBookCategories(Category targetCategory) {
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
