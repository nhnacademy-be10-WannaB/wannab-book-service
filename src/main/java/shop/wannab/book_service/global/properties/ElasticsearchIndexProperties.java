package shop.wannab.book_service.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter @Setter
@Component
@ConfigurationProperties(prefix = "elasticsearch.index")
public class ElasticsearchIndexProperties {
    private String book;
}
