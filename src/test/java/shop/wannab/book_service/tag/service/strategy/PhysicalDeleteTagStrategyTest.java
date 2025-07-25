package shop.wannab.book_service.tag.service.strategy;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import shop.wannab.book_service.tag.entity.Tag;
import shop.wannab.book_service.tag.repository.TagRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("PhysicalDeleteTagStrategyTest 단위 테스트")
class PhysicalDeleteTagStrategyTest {

    @Mock TagRepository tagRepository;
    @InjectMocks PhysicalDeleteTagStrategy strategy;

    @Test
    @DisplayName("delete - 태그 ID를 기반으로 bookTag와 tag를 삭제한다")
    void delete() {
        // given
        Tag tag = Tag.create("딥러닝");
        ReflectionTestUtils.setField(tag, "id", 5L);

        // when
        strategy.delete(tag);

        // then
        verify(tagRepository).deleteTagWithBookTags(5L);
    }
}