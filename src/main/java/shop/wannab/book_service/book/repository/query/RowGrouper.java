package shop.wannab.book_service.book.repository.query;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import shop.wannab.book_service.book.controller.response.BookListResponse;
import shop.wannab.book_service.book.repository.projection.BookInfoProjection;

public final class RowGrouper {

    /**
     * 1단계에서 뽑은 ID 순서를 LinkedHashMap 에 삽입 순서 유지
     */
    public static Map<Long, BookListResponse> group(List<BookInfoProjection> rows,
                                                    List<Long> idOrder) {
        Map<Long, BookListResponse> map = new LinkedHashMap<>();
        idOrder.forEach(id -> map.put(id, BookListResponse.empty(id)));

        for (BookInfoProjection r : rows) {
            BookListResponse dto = map.get(r.bookId());

            if (dto.getTitle() == null) {
                dto.setTitle(r.title());
                dto.setDescription(r.description());
                dto.setPublicationDate(r.publicationDate());
                dto.setOriginPrice(r.originPrice());
                dto.setIsbn(r.isbn());
                dto.setStock(r.stock());
                dto.setStatus(r.status());
            }
            addIfNotNull(dto.getAuthorNames(),    r.authorName());
            addIfNotNull(dto.getPublisherNames(), r.publisherName());
            addIfNotNull(dto.getTagNames(),       r.tagName());
            addIfNotNull(dto.getImageUrls(),      r.imageUrl());
        }
        return map;
    }

    private static void addIfNotNull(List<String> list, String v) {
        if (v != null && !list.contains(v)) list.add(v);
    }

    private RowGrouper() {}
}
