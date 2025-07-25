package shop.wannab.book_service.global.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import shop.wannab.book_service.category.dto.CategoryHierarchyDto;

@Configuration
public class CategoryCacheConfig {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {

        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();

        JavaType listType = mapper.getTypeFactory()
                .constructCollectionType(List.class, CategoryHierarchyDto.class);

        Jackson2JsonRedisSerializer<List<CategoryHierarchyDto>> listJackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(mapper, listType);

        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(listJackson2JsonRedisSerializer))
                .entryTtl(Duration.ofHours(1))
                .computePrefixWith(c -> "category:".concat(c).concat(":"));
    }
}
