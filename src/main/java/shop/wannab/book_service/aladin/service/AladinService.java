package shop.wannab.book_service.aladin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import shop.wannab.book_service.aladin.client.AladinClient;
import shop.wannab.book_service.aladin.client.request.SearchRequest;
import shop.wannab.book_service.aladin.client.response.SearchResponse;
import shop.wannab.book_service.aladin.controller.request.BookInfoRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class AladinService {

    private final AladinClient aladinClient;

    @Value("${aladin.api.ttbkey}")
    private String ttbKey;

    public String searchBooks(BookInfoRequest request) {
        SearchRequest params = SearchRequest.from(request);
        SearchResponse searchResponse = aladinClient.fetchFromAladin(params.toParamMap(ttbKey));
        log.info("bookInfoResponse : {}", searchResponse);
        return null;
    }

}
