package shop.wannab.bookservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    /**
     * Aladin API 호출용 WebClient 빈 등록
     */
    @Bean
    public WebClient aladinWebClient() {
        return WebClient.builder()
                .baseUrl("https://www.aladin.co.kr/ttb/api")
                .build();
    }
}
