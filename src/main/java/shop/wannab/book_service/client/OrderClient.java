package shop.wannab.book_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-payment-service", contextId = "orderPaymentClient")
public interface OrderClient {

    @GetMapping("/api/orders/review-check")
    Boolean isReviewable(@RequestParam Long obId);
}
