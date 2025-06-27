package shop.wannab.book_service.aladin.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.book_service.aladin.client.AladinClient;
import shop.wannab.book_service.aladin.client.request.SearchRequest;
import shop.wannab.book_service.aladin.client.response.SearchResponse;
import shop.wannab.book_service.aladin.controller.request.BookInfoRequest;
import shop.wannab.book_service.aladin.exception.AladinApiException;
import shop.wannab.book_service.aladin.exception.AladinErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class AladinService {

    private final AladinClient aladinClient;

    @Value("${aladin.api.ttbkey}")
    private String ttbKey;

    @Transactional(readOnly = true)
    public SearchResponse searchBooks(BookInfoRequest request) {
        SearchRequest params = SearchRequest.from(request);
        try{
            return aladinClient.fetchFromAladin(params.toParamMap(ttbKey));
        } catch (FeignException e) {
            throw new AladinApiException(AladinErrorCode.ALDIN_API_ERROR, e);
        }
    }
}
