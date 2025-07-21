package shop.wannab.book_service.global.properties;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter @Setter
@Component
@ConfigurationProperties(prefix = "search")
public class ElasticsearchIndexProperties {
    private String index;
    private String pipeline;
    private Boost boost = new Boost();

    @Getter @Setter
    public static class Boost {
        private Map<String, Float> fields = new HashMap<>();
    }
}
