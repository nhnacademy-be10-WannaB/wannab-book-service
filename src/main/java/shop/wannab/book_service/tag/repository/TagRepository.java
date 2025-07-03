package shop.wannab.book_service.tag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.wannab.book_service.tag.entity.Tag;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag,Long> {
    Optional<Tag> findTagByTagName(String tagName);
}
