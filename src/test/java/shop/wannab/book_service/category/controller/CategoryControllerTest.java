package shop.wannab.book_service.category.controller;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
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
    @DisplayName("카테고리 조회 테스트 : 상위 카테고리 전체 조회")
    void findCategories_parent() throws Exception {
        List<CategoryResponse> mockResponse = List.of(
                new CategoryResponse(1L, "소설"),
                new CategoryResponse(2L, "자기계발")
        );

        given(categoryService.getParentCategory()).willReturn(mockResponse);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("소설"))
                .andDo(print())
                .andDo(document("categories/find-parent",
                        queryParameters(
                                parameterWithName("parentId").optional().description("부모 카테고리 ID가 없어야 상위 카테고리 조회)")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("카테고리 ID"),
                                fieldWithPath("[].name").description("카테고리 이름")
                        )
                ));;

        verify(categoryService).getParentCategory();

    }

    @Test
    @DisplayName("카테고리 조회 테스트 : 특정 부모 ID의 하위 카테고리 조회")
    void findCategories_child() throws Exception {
        Long parentId = 1L;
        List<CategoryResponse> mockResponse = List.of(
                new CategoryResponse(3L, "고전"),
                new CategoryResponse(4L, "현대소설")
        );

        given(categoryService.findChildCategoriesByParentId(parentId)).willReturn(mockResponse);

        mockMvc.perform(get("/api/categories").param("parentId", parentId.toString()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("categories/find-child",
                        queryParameters(
                                parameterWithName("parentId").description("부모 카테고리 ID (필수)")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("카테고리 ID"),
                                fieldWithPath("[].name").description("카테고리 이름")
                        )
                ));

        verify(categoryService).findChildCategoriesByParentId(parentId);
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
}