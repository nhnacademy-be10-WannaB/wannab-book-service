package shop.wannab.book_service.category.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import shop.wannab.book_service.category.dto.CategoryHierarchyDto;
import shop.wannab.book_service.category.dto.request.CategoryCreateRequest;
import shop.wannab.book_service.category.dto.response.CategoryResponse;
import shop.wannab.book_service.category.service.CategoryService;

@ActiveProfiles("ci")
@AutoConfigureRestDocs
@DisplayName("Category Controller 단위 테스트")
@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @DisplayName("카테고리 조회 테스트 : 카테고리 계층 구조를 반환한다")
    void getCategoryHierarchy() throws Exception {
        // given
        CategoryHierarchyDto child = new CategoryHierarchyDto();
        child.setId(2L);
        child.setName("자식");

        CategoryHierarchyDto parent = new CategoryHierarchyDto();
        parent.setId(1L);
        parent.setName("부모");
        parent.setChildren(List.of(child));

        given(categoryService.getCategoryHierarchy()).willReturn(List.of(parent));

        // when & then
        mockMvc.perform(get("/api/categories/hierarchy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("부모"))
                .andExpect(jsonPath("$[0].children[0].name").value("자식"));
    }

    @Test
    @DisplayName("카테고리 조회 테스트 : 부모 카테고리 리스트를 반환한다")
    void getParentCategory() throws Exception {
        // given
        CategoryResponse c1 = new CategoryResponse(1L, "문학");
        CategoryResponse c2 = new CategoryResponse(2L, "IT");

        given(categoryService.getAllParentCategory()).willReturn(List.of(c1, c2));

        // when & then
        mockMvc.perform(get("/api/categories/parents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("문학"))
                .andExpect(jsonPath("$[1].name").value("IT"));
    }


    @Test
    @DisplayName("카테고리 조회 테스트 : 상위 카테고리 페이징 조회")
    void findCategories_parent_with_paging() throws Exception {
        Page<CategoryResponse> mockResponse = new PageImpl<>(
                List.of(
                        new CategoryResponse(1L, "소설"),
                        new CategoryResponse(2L, "자기계발")
                ),
                PageRequest.of(0, 10),
                2
        );

        given(categoryService.getParentCategory(any())).willReturn(mockResponse);

        mockMvc.perform(get("/api/categories")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("소설"))
                .andDo(print())
                .andDo(document("categories/find-parent",
                        queryParameters(
                                parameterWithName("parentId").optional().description("부모 카테고리 ID (없으면 상위 카테고리 조회)"),
                                parameterWithName("page").optional().description("페이지 번호 (기본값: 0)"),
                                parameterWithName("size").optional().description("페이지 크기 (기본값: 10)")
                        ),
                        responseFields(
                                fieldWithPath("content[].id").description("카테고리 ID"),
                                fieldWithPath("content[].name").description("카테고리 이름"),
                                fieldWithPath("number").description("현재 페이지 번호"),
                                fieldWithPath("size").description("페이지 크기"),
                                fieldWithPath("totalPages").description("전체 페이지 수"),
                                fieldWithPath("totalElements").description("전체 요소 수"),
                                fieldWithPath("hasNext").description("다음 페이지 존재 여부"),
                                fieldWithPath("hasPrevious").description("이전 페이지 존재 여부")
                        )
                ));

        verify(categoryService).getParentCategory(any());
    }

    @Test
    @DisplayName("카테고리 조회 테스트 : 특정 부모 ID의 하위 카테고리 페이징 조회")
    void findCategories_child_with_paging() throws Exception {
        Long parentId = 1L;
        Page<CategoryResponse> mockResponse = new PageImpl<>(
                List.of(
                        new CategoryResponse(3L, "고전"),
                        new CategoryResponse(4L, "현대소설")
                ),
                PageRequest.of(0, 10),
                2
        );

        given(categoryService.findChildCategoriesByParentId(eq(parentId), any())).willReturn(mockResponse);

        mockMvc.perform(get("/api/categories")
                        .param("parentId", parentId.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andDo(print())
                .andDo(document("categories/find-child",
                        queryParameters(
                                parameterWithName("parentId").description("부모 카테고리 ID (필수)"),
                                parameterWithName("page").optional().description("페이지 번호 (기본값: 0)"),
                                parameterWithName("size").optional().description("페이지 크기 (기본값: 10)")
                        ),
                        responseFields(
                                fieldWithPath("content[].id").description("카테고리 ID"),
                                fieldWithPath("content[].name").description("카테고리 이름"),
                                fieldWithPath("number").description("현재 페이지 번호"),
                                fieldWithPath("size").description("페이지 크기"),
                                fieldWithPath("totalPages").description("전체 페이지 수"),
                                fieldWithPath("totalElements").description("전체 요소 수"),
                                fieldWithPath("hasNext").description("다음 페이지 존재 여부"),
                                fieldWithPath("hasPrevious").description("이전 페이지 존재 여부")
                        )
                ));

        verify(categoryService).findChildCategoriesByParentId(eq(parentId), any());
    }

    @Test
    @DisplayName("상위 카테고리 생성 테스트 : 성공")
    void createParentCategory() throws Exception {
        CategoryCreateRequest request = new CategoryCreateRequest("소설");

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(document("categories/create-parent",
                        requestFields(
                                fieldWithPath("name").description("카테고리 이름")
                        )
                ));

        verify(categoryService).createParentCategory(request);
    }

    @Test
    @DisplayName("하위 카테고리 생성 테스트 : 성공")
    void createChildCategory() throws Exception {
        Long parentId = 1L;
        CategoryCreateRequest request = new CategoryCreateRequest("고전");

        mockMvc.perform(post("/api/categories/{parentId}/children", parentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(document("categories/create-child",
                        pathParameters(
                                parameterWithName("parentId").description("부모 카테고리 ID")
                        ),
                        requestFields(
                                fieldWithPath("name").description("카테고리 이름")
                        )
                ));

        verify(categoryService).createChildCategory(request, parentId);
    }

    @Test
    @DisplayName("카테고리 삭제 테스트")
    void deleteCategory() throws Exception {
        Long categoryId = 10L;

        mockMvc.perform(delete("/api/categories/{categoryId}", categoryId))
                .andExpect(status().isNoContent())
                .andDo(document("categories/delete",
                        pathParameters(
                                parameterWithName("categoryId").description("삭제할 카테고리 ID")
                        )
                ));

        verify(categoryService).deleteCategory(categoryId);
    }

    @Test
    @DisplayName("카테고리 ID 목록으로 이름 매핑을 반환한다")
    void getCategoryNames() throws Exception {
        // given
        List<Long> ids = List.of(1L, 2L);
        Map<Long, String> mockResult = Map.of(1L, "문학", 2L, "IT");

        given(categoryService.getCategoriesNames(ids)).willReturn(mockResult);

        // when & then
        mockMvc.perform(post("/api/categories/names")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1, 2]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['1']").value("문학"))
                .andExpect(jsonPath("$['2']").value("IT"));
    }

    @Test
    @DisplayName("도서 ID 목록으로 모든 상위 카테고리 ID 매핑을 반환한다")
    void getCategoryIds() throws Exception {
        // given
        List<Long> bookIds = List.of(10L, 20L);
        Map<Long, Set<Long>> mockResult = Map.of(
                10L, Set.of(1L, 2L),
                20L, Set.of(3L)
        );

        given(categoryService.findAllCategoryIdsWithHierarchy(bookIds)).willReturn(mockResult);

        // when & then
        mockMvc.perform(post("/api/categories/ids-map")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[10, 20]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['10']").value(org.hamcrest.Matchers.containsInAnyOrder(1, 2)))
                .andExpect(jsonPath("$['20']").value(org.hamcrest.Matchers.containsInAnyOrder(3)));
    }

    @Test
    @DisplayName("도서 ID로 조상 카테고리 ID 목록을 반환한다")
    void getAncestorCategoryIds() throws Exception {
        // given
        List<Long> ancestorIds = List.of(1L, 2L, 3L);
        given(categoryService.getAncestorCategoryIdsForBook(42L)).willReturn(ancestorIds);

        // when & then
        mockMvc.perform(get("/api/categories/42/ancestor-category-ids"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0]").value(1))
                .andExpect(jsonPath("$[1]").value(2))
                .andExpect(jsonPath("$[2]").value(3));
    }
}