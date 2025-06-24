package shop.wannab.book_service.aladin.client;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shop.wannab.book_service.aladin.client.response.SearchResponse;

@FeignClient(name = "aladinClient", url = "${aladin.api.base-url}")
public interface AladinClient {

    @GetMapping("/ItemSearch.aspx")
    SearchResponse fetchFromAladin(@RequestParam Map<String, Object> params);
}

