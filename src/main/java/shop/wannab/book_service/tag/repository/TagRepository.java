package shop.wannab.book_service.tag.repository;

import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.wannab.book_service.tag.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long>, TagQueryRepository {
    Optional<Tag> findTagByName(String name);

    Collection<Tag> findAllByNameIn(Collection<String> names);
}
