package shop.wannab.book_service.search.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.MsearchRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.wannab.book_service.global.properties.ElasticsearchIndexProperties;
import shop.wannab.book_service.search.domain.BookSearchField;

@ActiveProfiles("ci")
@ExtendWith(MockitoExtension.class)
@DisplayName("BookSearchQueryFactory 단위 테스트")
class BookSearchQueryFactoryTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ElasticsearchIndexProperties indexProps;

    private BookSearchQueryFactory factory;

    @BeforeEach
    void setUp() {
        Map<String, Float> boosts = Map.of(
                "title", 3.0f,
                "author", 1.5f,
                "chosungTitle", 2.0f,
                "chosungWord", 2.5f
        );

        given(indexProps.getBoost().getFields()).willReturn(boosts);
        given(indexProps.getChosungTitle()).willReturn("chosungTitle");
        given(indexProps.getChosungWord()).willReturn("chosungWord");
        given(indexProps.getIndex()).willReturn("book_index");

        factory = new BookSearchQueryFactory(indexProps);
    }

    @Test
    @DisplayName("targets의 Field에 따라, _msearch 요청은 동적으로 변경된다 (ALL + TITLE + AUTHOR)")
    void buildMultiSearch_all_title_author() {
        // given
        String keyword = "자바";
        List<BookSearchField> targets = List.of(
                BookSearchField.ALL,
                BookSearchField.TITLE,
                BookSearchField.AUTHORS
        );

        // when
        MsearchRequest req = factory.buildMultiSearch(keyword, targets);

        // then
        assertThat(req.searches()).hasSize(3);

        var first = req.searches().getFirst();
        assertThat(first.header().index()).containsExactly("book_index");
        assertThat(first.body().size()).isEqualTo(BookSearchField.ALL.getDefaultSize());

        Query q0 = first.body().query();
        assertThat(Objects.requireNonNull(q0).isBool()).isTrue();
        BoolQuery bq0 = q0.bool();
        assertThat(bq0.minimumShouldMatch()).isEqualTo("1");
        assertThat(bq0.should()).isNotEmpty();

        var second = req.searches().get(1);
        assertThat(second.header().index()).containsExactly("book_index");
        assertThat(second.body().size()).isEqualTo(BookSearchField.TITLE.getDefaultSize());
        Query q1 = second.body().query();
        assertThat(Objects.requireNonNull(q1).isBool()).isTrue();
        assertThat(q1.bool().should()).isNotEmpty();

        var third = req.searches().get(2);
        assertThat(third.header().index()).containsExactly("book_index");
        assertThat(third.body().size()).isEqualTo(BookSearchField.AUTHORS.getDefaultSize());
        Query q2 = third.body().query();
        assertThat(Objects.requireNonNull(q2).isBool()).isTrue();

        assertThat(q2.bool().must()).hasSize(1);
        assertThat(q2.bool().should()).isNotEmpty();
    }


    @Test
    @DisplayName("keyword가 공백이더라도 빈 문자열이라면, 기본 ALL 서치가 만들어진다")
    void buildMultiSearch_blankKeyword() {
        // given
        String keyword = "";
        List<BookSearchField> targets = List.of(BookSearchField.ALL);

        // when
        MsearchRequest req = factory.buildMultiSearch(keyword, targets);

        // then
        assertThat(req.searches()).hasSize(1);
        assertThat(req.searches().getFirst().header().index()).containsExactly("book_index");
        assertThat(req.searches().getFirst().body().size()).isEqualTo(BookSearchField.ALL.getDefaultSize());
    }
}