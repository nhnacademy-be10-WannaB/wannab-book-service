package shop.wannab.bookservice.service.search;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import shop.wannab.bookservice.service.search.dto.AladinBook;
import shop.wannab.bookservice.service.search.dto.AladinResponse;

@Service
@RequiredArgsConstructor
public class AladinSearchServiceImpl implements AladinSearchService {

    private final WebClient aladinWebClient;

    /**
     * application.yml 에
     * aladin:
     *   api:
     *     key: YOUR_REAL_API_KEY
     * 로 설정해 두세요.
     */
    @Value("${aladin.api.key}")
    private String apiKey;

    @Override
    public AladinBook searchByIsbn(String isbn) {
        AladinResponse resp = aladinWebClient.get()
                .uri(uri -> uri
                        .path("/ItemLookUp.aspx")
                        .queryParam("ttbkey", apiKey)
                        .queryParam("itemIdType", "ISBN13")
                        .queryParam("ItemId", isbn)
                        .queryParam("output", "js")
                        .build()
                )
                .retrieve()
                .bodyToMono(AladinResponse.class)
                .onErrorResume(e -> Mono.empty())
                .block();

        if (resp == null
                || resp.getItem() == null
                || resp.getItem().isEmpty()) {
            return null;
        }

        // 첫 번째 검색 결과 사용
        AladinResponse.Item it = resp.getItem().get(0);
        return new AladinBook(
                it.getTitle(),
                it.getDescription(),
                it.getPubDate(),
                it.getIsbn13(),
                it.getPriceStandard(),
                it.getPriceSales(),
                it.isWrappable()
        );
    }

    @Override
    public AladinResponse searchByTitle(String query) {
        AladinResponse resp = aladinWebClient.get()
                .uri(uri -> uri
                        .path("/ItemSearch.aspx")
                        .queryParam("ttbkey", apiKey)
                        .queryParam("Query", query)
                        .queryParam("QueryType", "Title")
                        .queryParam("MaxResults", 10)
                        .queryParam("output", "js")
                        .build()
                )
                .retrieve()
                .bodyToMono(AladinResponse.class)
                .onErrorResume(e -> Mono.empty())
                .block();

        // 호출 결과 그대로 리턴하거나 필요하면 추가 처리
        return resp;
    }
}
