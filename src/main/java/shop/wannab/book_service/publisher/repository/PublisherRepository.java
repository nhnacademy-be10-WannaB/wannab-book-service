package shop.wannab.book_service.publisher.repository;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.wannab.book_service.publisher.entity.Publisher;

import java.util.Optional;

public interface PublisherRepository extends JpaRepository<Publisher,Long> {
    Optional<Publisher> findPublisherByPublisherName(String publisherName);

    Collection<Publisher> findAllByPublisherNameIn(Collection<String> publisherNames);
}
