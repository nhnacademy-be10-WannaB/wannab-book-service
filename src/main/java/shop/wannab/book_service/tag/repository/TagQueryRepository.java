package shop.wannab.book_service.tag.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.wannab.book_service.tag.dto.response.TagResponse;

public interface TagQueryRepository {
    Page<TagResponse> searchTags(String keyword, Pageable pageable);
    void deleteTagWithBookTags(Long tagId);
}
