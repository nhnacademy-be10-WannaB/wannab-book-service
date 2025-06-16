##  의존성 추가
```
- Springdoc OpenAPI UI
  xml
  <dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.1.0</version>
  </dependency>



- Reactive WebClient ( WebFlux Starter )
xml
  <dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-webflux</artifactId>
  </dependency>

```



배열 키가 "item"으로 되어 있고, wrappingAvailable / wrappable 중에서는 실제 JSON 스펙 문서(그리고 샘플 응답 예시)상 키 이름이 

wrappingAvailable 이므로, DTO에 이렇게 매핑하기