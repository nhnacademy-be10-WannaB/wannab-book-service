package shop.wannab.book_service.tag.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TagDeleteStrategyResolver {

    private final PhysicalDeleteTagStrategy physicalDeleteTagStrategy;

    public TagDeleteStrategy resolve() {
        return physicalDeleteTagStrategy;
    }
}
