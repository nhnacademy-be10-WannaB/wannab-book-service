package shop.wannab.book_service.tag.service.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("TagDeleteStrategyResolverTest 단위 테스트")
class TagDeleteStrategyResolverTest {

    @Mock PhysicalDeleteTagStrategy physicalDeleteTagStrategy;
    @InjectMocks TagDeleteStrategyResolver resolver;

    @Test
    @DisplayName("resolve - 현재는 무조건 PhysicalDeleteTagStrategy를 반환한다")
    void resolve() {
        TagDeleteStrategy result = resolver.resolve();
        assertThat(result).isEqualTo(physicalDeleteTagStrategy);
    }
}