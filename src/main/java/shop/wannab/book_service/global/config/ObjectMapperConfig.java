package shop.wannab.book_service.global.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Enum 대소문자 상관없이 매핑될 수 있도록 커스텀 설정하는 클래스
 *
 * @author hunmin
 */
@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper objectMapper(){
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
                .build();
    }
}
