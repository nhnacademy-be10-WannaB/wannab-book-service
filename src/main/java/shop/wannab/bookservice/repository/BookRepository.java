package shop.wannab.bookservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.wannab.bookservice.domain.Book;

/**
 * Book 엔티티의 CRUD 및 페이징·정렬 기능을 제공하는 레포지토리.
 */
public interface BookRepository extends JpaRepository<Book, Long> {
    // 필요하다면 커스텀 쿼리 메서드를 여기에 추가하세요.
}
