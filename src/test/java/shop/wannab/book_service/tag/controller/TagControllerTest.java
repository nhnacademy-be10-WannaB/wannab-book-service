package shop.wannab.book_service.tag.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import shop.wannab.book_service.tag.dto.request.TagCreateRequest;
import shop.wannab.book_service.tag.dto.response.TagResponse;
import shop.wannab.book_service.tag.service.TagService;

@ActiveProfiles("ci")
@AutoConfigureMockMvc
@DisplayName("TagController 단위 테스트")
@WebMvcTest(TagController.class)
class TagControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private TagService tagService;

    @Test
    @DisplayName("태그 목록 조회 - 태그 목록을 키워드와 페이지 정보로 조회한다")
    void findTags() throws Exception {
        // given
        Page<TagResponse> page = new PageImpl<>(
                List.of(new TagResponse(1L, "AI")),
                PageRequest.of(0, 30),
                1
        );
        given(tagService.searchTags("", PageRequest.of(0, 30))).willReturn(page);

        // when & then
        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("AI"));
    }

    @Test
    @DisplayName("태그 삭제")
    void deleteTag() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/tags/{tagId}", 1L))
                .andExpect(status().isNoContent());

        verify(tagService).deleteTag(1L);
    }

    @Test
    @DisplayName("태그 생성")
    void createTag() throws Exception {
        // given
        TagCreateRequest request = new TagCreateRequest("AI");
        String json = new ObjectMapper().writeValueAsString(request);

        // when & then
        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        verify(tagService).createTag(any(TagCreateRequest.class));
    }
}