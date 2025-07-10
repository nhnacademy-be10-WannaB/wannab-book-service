package shop.wannab.book_service.book.repository.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import shop.wannab.book_service.book.entity.QBook;

/**
 * Query DSL
 */
public final class BookPredicates {

    private static final QBook b = QBook.book;

    public static BooleanExpression titleLike(String keyword) {
        return (keyword == null || keyword.isBlank())
                ? null
                : b.title.containsIgnoreCase(keyword);
    }

    private BookPredicates() {}
}
