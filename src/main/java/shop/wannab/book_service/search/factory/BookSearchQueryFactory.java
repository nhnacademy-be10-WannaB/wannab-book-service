package shop.wannab.book_service.search.factory;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.MsearchRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.wannab.book_service.global.properties.ElasticsearchIndexProperties;
import shop.wannab.book_service.search.domain.BookSearchField;

/**
 * Elasticsearch 쿼리를 만드는 클래스
 *
 * @author hunmin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookSearchQueryFactory {

    private final ElasticsearchIndexProperties indexProps;

    /**
     * _msearch 쿼리를 만드는 메서드
     * 검색하고 싶은 Field List를 파라미터로 넣으면 해당 필드들을 검색한다
     */
    public MsearchRequest buildMultiSearch(String keyword,
                                           Collection<BookSearchField> targets) {

        Map<String, Float> boosts = indexProps.getBoost().getFields();

        List<String> baseFields = targets.stream()
                .map(BookSearchField::getFieldName)
                .filter(boosts::containsKey)
                .map(f -> f + "^" + boosts.get(f))
                .toList();

        return MsearchRequest.of(ms -> {
            ms.searches(si -> si
                    .header(h -> h.index(indexProps.getIndex()))
                    .body(b -> b
                            .size(BookSearchField.ALL.getDefaultSize())
                            .query(q -> q.bool(bl -> bl
                                    .should(s -> s.multiMatch(mm -> mm
                                            .query(keyword)
                                            .operator(Operator.And)
                                            .type(TextQueryType.MostFields)
                                            .fields(baseFields)))
                                    .should(s -> s.match(mq -> mq
                                            .field("title_chosung_word")
                                            .query(keyword)
                                            .operator(Operator.And)
                                            .boost(boosts.getOrDefault("title_chosung_word", 1f))))
                                    .should(s -> s.prefix(p -> p
                                            .field("title_chosung_full")
                                            .value(keyword)
                                            .boost(boosts.getOrDefault("title_chosung_full", 1f))))
                                    .minimumShouldMatch("1")
                            ))
                    )
            );


            targets.stream()
                    .filter(f -> f != BookSearchField.ALL)
                    .forEach(field -> ms.searches(si -> si
                            .header(h -> h.index(indexProps.getIndex()))
                            .body(b -> b
                                    .size(field.getDefaultSize())
                                    .query(q -> {
                                        if (field == BookSearchField.TITLE) {
                                            return q.bool(bq -> bq
                                                    .should(s -> s.match(mq -> mq
                                                            .field("title")
                                                            .query(keyword)
                                                            .operator(Operator.And)
                                                            .boost(boosts.getOrDefault("title", 1f))))
                                                    .should(s -> s.match(mq -> mq
                                                            .field("title_chosung_word")
                                                            .query(keyword)
                                                            .operator(Operator.And)
                                                            .boost(boosts.getOrDefault("title_chosung_word", 1f))))
                                                    .should(s -> s.prefix(p -> p
                                                            .field("title_chosung_full")
                                                            .value(keyword)
                                                            .boost(boosts.getOrDefault("title_chosung_full", 1f))))
                                                    .minimumShouldMatch("1")
                                                    .should(s -> s.rankFeature(rf -> rf
                                                            .field("likeCount").log(l -> l.scalingFactor(2))))
                                                    .should(s -> s.rankFeature(rf -> rf
                                                            .field("reviewCount").log(l -> l.scalingFactor(2))))
                                                    .should(s -> s.rankFeature(rf -> rf
                                                            .field("averageRating").saturation(r -> r)))
                                            );
                                        } else {
                                            return q.bool(bq -> bq
                                                    .must(m -> m.match(mq -> mq
                                                            .field(field.getFieldName())
                                                            .query(keyword)
                                                            .operator(Operator.And)))
                                                    .should(s -> s.rankFeature(rf -> rf
                                                            .field("likeCount").log(l -> l.scalingFactor(2))))
                                                    .should(s -> s.rankFeature(rf -> rf
                                                            .field("reviewCount").log(l -> l.scalingFactor(2))))
                                                    .should(s -> s.rankFeature(rf -> rf
                                                            .field("averageRating").saturation(r -> r)))
                                            );
                                        }
                                    })
                            )
                    ));
            return ms;
        });
    }
}
