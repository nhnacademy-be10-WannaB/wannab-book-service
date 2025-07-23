package shop.wannab.book_service.book.repository.impl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.book_service.author.entity.QAuthor;
import shop.wannab.book_service.book.entity.QBook;
import shop.wannab.book_service.book.entity.QBookAuthor;
import shop.wannab.book_service.book.entity.QBookImage;
import shop.wannab.book_service.book.entity.QBookPublisher;
import shop.wannab.book_service.book.entity.QBookTag;
import shop.wannab.book_service.book.repository.BookQueryRepository;
import shop.wannab.book_service.book.repository.projection.BookInfoProjection;
import shop.wannab.book_service.book.repository.query.BookPredicates;
import shop.wannab.book_service.book.repository.query.BookSorts;
import shop.wannab.book_service.publisher.entity.QPublisher;
import shop.wannab.book_service.tag.entity.QTag;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookQueryRepositoryImpl implements BookQueryRepository {

    private final JPAQueryFactory queryFactory;

    private static final QBook book = QBook.book;
    private static final QBookAuthor bookAuthor = QBookAuthor.bookAuthor;
    private static final QAuthor author = QAuthor.author;
    private static final QBookPublisher bookPublisher = QBookPublisher.bookPublisher;
    private static final QPublisher publisher = QPublisher.publisher;
    private static final QBookTag bookTag = QBookTag.bookTag;
    private static final QTag tag = QTag.tag;
    private static final QBookImage bookImage = QBookImage.bookImage;

    @Override
    public List<Long> findPageIds(String keyword, Pageable pageable) {
        return queryFactory.select(book.bookId)
                .from(book)
                .where(BookPredicates.titleLike(keyword))
                .orderBy(BookSorts.from(pageable).toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<BookInfoProjection> fetchDetails(List<Long> ids) {
        if (ids.isEmpty()) return List.of();

        return queryFactory
                .select(Projections.constructor(BookInfoProjection.class,
                        book.bookId, book.title, book.description,
                        book.publicationDate, book.originPrice, book.isbn,
                        book.stock, book.status,
                        author.authorName, publisher.publisherName,
                        tag.name, bookImage.imageUrl))
                .from(book)
                .leftJoin(book.bookAuthors, bookAuthor)
                .leftJoin(bookAuthor.author, author)
                .leftJoin(book.bookPublishers, bookPublisher)
                .leftJoin(bookPublisher.publisher, publisher)
                .leftJoin(book.bookTags, bookTag)
                .leftJoin(bookTag.tag, tag)
                .leftJoin(book.bookImages, bookImage)
                .where(book.bookId.in(ids))
                .fetch();
    }

    @Override
    public long countAll(String kw) {
        Long l = queryFactory.select(book.bookId.countDistinct())
                .from(book)
                .where(BookPredicates.titleLike(kw))
                .fetchOne();
        if(l == null) return 0;
        return l;
    }
}
