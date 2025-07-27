package shop.wannab.book_service.tag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import shop.wannab.book_service.tag.dto.request.TagCreateRequest;
import shop.wannab.book_service.tag.dto.response.TagResponse;
import shop.wannab.book_service.tag.entity.Tag;
import shop.wannab.book_service.tag.repository.TagRepository;
import shop.wannab.book_service.tag.service.strategy.TagDeleteStrategy;
import shop.wannab.book_service.tag.service.strategy.TagDeleteStrategyResolver;

@ActiveProfiles("ci")
@ExtendWith(MockitoExtension.class)
@DisplayName("TagServiceTest 단위 테스트")
class TagServiceTest {

    @InjectMocks TagService tagService;

    @Mock TagRepository tagRepository;
    @Mock TagDeleteStrategyResolver strategyResolver;
    @Mock TagDeleteStrategy strategy;

    @Test
    @DisplayName("searchTags - 키워드와 페이지 정보로 태그 페이지를 조회한다")
    void searchTags() {
        // given
        String keyword = "자바";
        Pageable pageable = PageRequest.of(0, 10);
        Page<TagResponse> page = new PageImpl<>(List.of(new TagResponse(1L, "자바")));
        given(tagRepository.searchTags(keyword, pageable)).willReturn(page);

        // when
        Page<TagResponse> result = tagService.searchTags(keyword, pageable);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().name()).isEqualTo("자바");
    }

    @Test
    @DisplayName("deleteTag - 존재하는 태그를 삭제 전략을 통해 삭제한다")
    void deleteTag() {
        // given
        Tag tag = Tag.create("스프링");
        ReflectionTestUtils.setField(tag, "id", 1L);

        given(tagRepository.findById(1L)).willReturn(Optional.of(tag));
        given(strategyResolver.resolve()).willReturn(strategy);

        // when
        tagService.deleteTag(1L);

        // then
        verify(strategy).delete(tag);
    }

    @Test
    @DisplayName("deleteTag - 존재하지 않는 태그는 예외를 발생시킨다")
    void deleteTag_notFound() {
        given(tagRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.deleteTag(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 태그입니다.");
    }

    @Test
    @DisplayName("createTag - 요청된 이름으로 태그를 생성하고 저장한다")
    void createTag() {
        // given
        TagCreateRequest request = new TagCreateRequest("AI");

        // when
        tagService.createTag(request);

        // then
        verify(tagRepository).save(any(Tag.class));
    }
}