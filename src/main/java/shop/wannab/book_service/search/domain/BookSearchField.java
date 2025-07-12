package shop.wannab.book_service.search.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookSearchField {
    ALL("all", "통합 검색", false, 10, false, null),
    TITLE("title", "제목 검색", true, 10, true, "title.jaso"),
    DESCRIPTION("description", "설명 검색", true, 10, false, null),
    TAGS("tags", "태그 검색", true, 10, false, null),
    CATEGORIES("categories", "카테고리 검색", true, 10, false, null),
    AUTHORS("authors", "저자 검색", true, 10, false, null),
    PUBLISHERS("publishers", "출판사 검색", true, 10, false, null);

    private final String fieldName;
    private final String description;
    private final boolean highlight;
    private final int defaultSize;
    private final boolean supportsInitial;
    private final String jasoFieldName;

    public static List<BookSearchField> basicList(){
        return List.of(values());
    }

    public static Set<BookSearchField> basicSet() {
        return EnumSet.allOf(BookSearchField.class);
    }

    /**
     * enum 선언 순서대로 정렬된 List 를 반환하는 메서드
     */
    public static List<BookSearchField> ordered(Collection<BookSearchField> selected) {
        return Arrays.stream(values())
                .filter(selected::contains)
                .toList();
    }
}
