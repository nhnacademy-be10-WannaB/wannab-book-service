package shop.wannab.book_service.aladin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.wannab.book_service.aladin.controller.request.BookInfoRequest;
import shop.wannab.book_service.aladin.controller.response.BookInfoResponse;
import shop.wannab.book_service.aladin.service.AladinService;

@RestController
@RequestMapping("/api/aladin")
@RequiredArgsConstructor
public class AladinController {

    private final AladinService aladinService;

    @PostMapping
    public BookInfoResponse getBooksInfo(@RequestBody @Valid BookInfoRequest request){
        aladinService.searchBooks(request);
        return null;
    }

}
