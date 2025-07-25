package shop.wannab.book_service.category.service.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.wannab.book_service.category.entity.Category;
import shop.wannab.book_service.category.repository.CategoryRepository;

@ActiveProfiles("ci")
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryDeleteStrategy 단위 테스트")
class CategoryDeleteStrategyTest {

    @InjectMocks private PhysicalCategoryDeleteStrategy physicalStrategy;
    @InjectMocks private SoftCategoryDeleteStrategy softStrategy;

    @Mock private CategoryRepository categoryRepository;

    @Test
    @DisplayName("물리 삭제 전략은 deleteCategoryWithBookCategories()를 호출해야 한다")
    void physicalDelete_callsRepositoryDelete() {
        // given
        Category category = new Category("부모", null);

        // when
        physicalStrategy.delete(category);

        // then
        verify(categoryRepository).deleteCategoryWithBookCategories(category);
    }

    @Test
    @DisplayName("소프트 삭제 전략은 deletedAt을 설정하고 save()를 호출해야 한다")
    void softDelete_setsDeletedAt_and_callsSave() {
        // given
        Category category = new Category("소프트", null);
        assertThat(category.isDeleted()).isFalse();

        // when
        softStrategy.delete(category);

        // then
        assertThat(category.isDeleted()).isTrue();
        verify(categoryRepository).save(category);
    }

}