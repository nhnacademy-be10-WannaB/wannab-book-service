package shop.wannab.book_service.book.repository.query;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import shop.wannab.book_service.book.entity.QBook;

@SuppressWarnings("java:S1452")
public final class BookSorts {

    private static final QBook book = QBook.book;

    /**
     * 정렬에 사용할 컬럼 Path
     */
    private static final Map<String, ComparableExpressionBase<? extends Comparable<?>>> COLUMN_MAP = Map.of(
            "title", book.title,
            "publicationDate", book.publicationDate,
            "originPrice", book.originPrice,
            "bookId", book.bookId
    );

    public static List<OrderSpecifier<? extends Comparable<?>>> from(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            return List.of(book.bookId.asc());
        }

        return pageable.getSort().stream()
                .map(order -> {
                    ComparableExpressionBase<? extends Comparable<?>> expr =
                            COLUMN_MAP.getOrDefault(order.getProperty(), book.bookId);
                    return order.isAscending() ? expr.asc() : expr.desc();
                })
                .toList();
    }

    private BookSorts() {}
}
