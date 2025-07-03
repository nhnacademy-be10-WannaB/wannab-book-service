package shop.wannab.book_service.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.wannab.book_service.review.entity.ReviewImage;


public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
}
