package shop.wannab.book_service.search.factory;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch.core.MsearchRequest;
import java.util.Collection;
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

        MsearchRequest request = MsearchRequest.of(ms -> {
            targets.forEach(field -> ms.searches(si -> si
                    .header(h -> h.index(indexProps.getBook()))
                    .body(b -> {
                        b.size(field.getDefaultSize())
                                .query(q -> q.bool(bq -> bq
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
                                ));

                        if(field.isHighlight()){
                            b.highlight(hl -> hl
                                    .type("unified")
                                    .requireFieldMatch(true)
                                    .preTags("<em>").postTags("</em>")
                                    .fields(field.getFieldName(), f -> f
                                            .fragmentSize(150)
                                            .numberOfFragments(3)
                                            .noMatchSize(150)
                                    )
                            );
                        }
                        return b;
                    })
            ));
            return ms;
        });

        return request;
    }
}
