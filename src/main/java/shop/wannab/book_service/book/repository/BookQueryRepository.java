package shop.wannab.book_service.book.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import shop.wannab.book_service.book.repository.projection.BookInfoProjection;

public interface BookQueryRepository {
    List<Long> findPageIds(String keyword, Pageable pageable);
    List<BookInfoProjection> fetchDetails(List<Long> ids);
    long countAll(String keyword);
}
