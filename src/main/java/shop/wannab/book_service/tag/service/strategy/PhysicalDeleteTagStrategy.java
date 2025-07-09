package shop.wannab.book_service.tag.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.book_service.tag.entity.Tag;
import shop.wannab.book_service.tag.repository.TagRepository;

@Component
@RequiredArgsConstructor
public class PhysicalDeleteTagStrategy implements TagDeleteStrategy{

    private final TagRepository tagRepository;

    @Override
    @Transactional
    public void delete(Tag tag) {
        tagRepository.deleteTagWithBookTags(tag.getId());
    }
}
