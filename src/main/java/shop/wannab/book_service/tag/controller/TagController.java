package shop.wannab.book_service.tag.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.wannab.book_service.global.response.PageResponse;
import shop.wannab.book_service.tag.dto.request.TagCreateRequest;
import shop.wannab.book_service.tag.dto.response.TagResponse;
import shop.wannab.book_service.tag.service.TagService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<PageResponse<TagResponse>> findTags(@RequestParam(defaultValue = "") String keyword,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "30") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TagResponse> result = tagService.searchTags(keyword, pageable);
        return ResponseEntity.ok(PageResponse.from(result));
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long tagId) {
        tagService.deleteTag(tagId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Void> createTag(@RequestBody @Valid TagCreateRequest request) {
        tagService.createTag(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
