package shop.wannab.book_service.author.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.wannab.book_service.author.entity.Author;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author,Long> {
    Optional<Author> findAuthorsByAuthorName(String authorName);
    List<Author> findAllByAuthorNameIn(Collection<String> authorNames);
}
