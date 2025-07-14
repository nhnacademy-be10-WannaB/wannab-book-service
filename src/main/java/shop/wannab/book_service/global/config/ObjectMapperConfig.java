package shop.wannab.book_service.global.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ObjectMapper 설정 클래스
 *
 * @author hunmin
 */
@Configuration
public class ObjectMapperConfig {

    /**
     * 커스텀 설정한 Object Mapper
     * 1. MapperFeature : Enum 대소문자 상관없이 매핑
     * 2. JavaTimeModule : LocalDate 등 매핑
     */
    @Bean
    public ObjectMapper objectMapper(){
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
                .build();
    }

    /**
     * Elasticsearch 응답 매핑 위해 커스텀 설정한 Object Mapper
     * 1. 일치하지 않는 Field 무시
     */
    @Bean
    public ObjectMapper elasticsearchObjectMapper() {
        return JsonMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();
    }
}
