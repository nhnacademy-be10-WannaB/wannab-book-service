package shop.wannab.book_service.tag.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import shop.wannab.book_service.book.entity.QBookTag;
import shop.wannab.book_service.tag.dto.response.TagResponse;
import shop.wannab.book_service.tag.entity.QTag;


@RequiredArgsConstructor
public class TagQueryRepositoryImpl implements TagQueryRepository{

    private final JPAQueryFactory queryFactory;

    private final QTag tag = QTag.tag;
    private final QBookTag bookTag = QBookTag.bookTag;

    @Override
    public Page<TagResponse> searchTags(String keyword, Pageable pageable) {
        BooleanBuilder condition = new BooleanBuilder();
        if (keyword != null && !keyword.isBlank()) {
            condition.and(tag.name.containsIgnoreCase(keyword));
        }

        List<TagResponse> content = queryFactory
                .select(Projections.constructor(TagResponse.class,
                        tag.id,
                        tag.name))
                .from(tag)
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(tag.count())
                .from(tag)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public void deleteTagWithBookTags(Long tagId) {
        queryFactory
                .delete(bookTag)
                .where(bookTag.tag.id.eq(tagId))
                .execute();

        queryFactory
                .delete(tag)
                .where(tag.id.eq(tagId))
                .execute();
    }
}
