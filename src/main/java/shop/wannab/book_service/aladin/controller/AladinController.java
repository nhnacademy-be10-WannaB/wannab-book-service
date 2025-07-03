package shop.wannab.book_service.aladin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.wannab.book_service.aladin.client.response.SearchResponse;
import shop.wannab.book_service.aladin.controller.request.BookInfoRequest;
import shop.wannab.book_service.aladin.service.AladinService;
import shop.wannab.book_service.book.controller.request.AladinBookCreateRequest;
import shop.wannab.book_service.global.response.ApiResponse;

@RestController
@RequestMapping("/api/admin/aladin")
@RequiredArgsConstructor
public class AladinController {

    private final AladinService aladinService;

    /**
     * 알라딘 도서 검색
     * @param request 검색하고 싶은 책 정보 객체
     * @return 도서 검색 결과
     */
    @PostMapping("/books/search")
    public SearchResponse getBooksInfo(@RequestBody @Valid BookInfoRequest request){
        return aladinService.searchBooks(request);
    }

    /**
     * 알라딘 도서 등록
     * @param request 알라딘 도서 등록 객체
     */
    @PostMapping("/books")
    public ResponseEntity<ApiResponse<Void>> createAladinBook(@RequestBody @Valid AladinBookCreateRequest request){
        aladinService.aladinCreateBook(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
