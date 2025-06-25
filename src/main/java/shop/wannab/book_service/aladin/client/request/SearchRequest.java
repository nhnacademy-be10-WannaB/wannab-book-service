package shop.wannab.book_service.aladin.client.request;

import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import shop.wannab.book_service.aladin.controller.request.BookInfoRequest;

/**
 * 알라딘에 실제로 요청을 보내기 위한 Request 객체
 *
 * @author hunmin
 */
@Builder
public record SearchRequest(
        String query,
        String queryType,
        String searchTarget,
        Integer start,
        Integer maxResults,
        String sort,
        String cover,
        Integer categoryId
) {

    public static SearchRequest from(BookInfoRequest req) {
        return SearchRequest.builder()
                .query(req.query())
                .queryType(req.queryType() != null ? req.queryType().name() : "Keyword")
                .searchTarget(req.searchTarget() != null ? req.searchTarget().name() : "Book")
                .start(req.start() != null ? req.start() : 1)
                .maxResults(req.maxResults() != null ? req.maxResults() : 100)
                .sort(req.sort() != null ? req.sort().name() : "Accuracy")
                .cover(req.cover() != null ? req.cover().name() : "Mid")
                .categoryId(req.categoryId() != null ? req.categoryId() : 0)
                .build();
    }

    public Map<String, Object> toParamMap(String ttbKey) {
        Map<String, Object> map = new HashMap<>();
        map.put("ttbkey", ttbKey);
        map.put("Query", query);
        map.put("QueryType", queryType);
        map.put("SearchTarget", searchTarget);
        map.put("start", start);
        map.put("MaxResults", maxResults);
        map.put("Sort", sort);
        map.put("Cover", cover);
        map.put("CategoryId", categoryId);
        map.put("output", "JS");
        map.put("Version", "20131101");
        return map;
    }
}
