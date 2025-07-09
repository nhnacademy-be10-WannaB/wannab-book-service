package shop.wannab.book_service.tag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.book_service.tag.dto.request.TagCreateRequest;
import shop.wannab.book_service.tag.dto.response.TagResponse;
import shop.wannab.book_service.tag.entity.Tag;
import shop.wannab.book_service.tag.repository.TagRepository;
import shop.wannab.book_service.tag.service.strategy.TagDeleteStrategy;
import shop.wannab.book_service.tag.service.strategy.TagDeleteStrategyResolver;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final TagDeleteStrategyResolver strategyResolver;

    @Transactional(readOnly = true)
    public Page<TagResponse> searchTags(String keyword, Pageable pageable) {
        return tagRepository.searchTags(keyword, pageable);
    }

    @Transactional
    public void deleteTag(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 태그입니다."));
        TagDeleteStrategy strategy = strategyResolver.resolve();
        strategy.delete(tag);
    }

    @Transactional
    public void createTag(TagCreateRequest request) {
        Tag tag = Tag.create(request.name());
        tagRepository.save(tag);
    }
}
