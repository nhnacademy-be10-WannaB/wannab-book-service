package shop.wannab.book_service.aladin.controller.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import shop.wannab.book_service.aladin.domain.AladinEnums.CoverOption;
import shop.wannab.book_service.aladin.domain.AladinEnums.QueryType;
import shop.wannab.book_service.aladin.domain.AladinEnums.SearchTarget;
import shop.wannab.book_service.aladin.domain.AladinEnums.SortOption;

/**
 * 알라딘 검색 API 활용하기 위한 관리자 요청 객체
 *
 * @param query 검색어
 * @param queryType 검색어 종류
 * @param searchTarget 검색 대상 (도서, 음반, DVD 등)
 * @param start 검색 결과 시작페이지
 * @param maxResults 최대 검색 수
 * @param sort 정렬 순서 (관련도, 출간일, 제목 등)
 * @param cover 표지 이미지 크기
 * @param categoryId 특정 카테고리 고유 번호
 *
 * @author hunmin
 */
@Builder
public record BookInfoRequest(
        @NotBlank String query,
        QueryType queryType,
        SearchTarget searchTarget,
        @Min(1) Integer start,
        @Max(200) Integer maxResults,
        SortOption sort,
        CoverOption cover,
        @Min(0) Integer categoryId
) {
}
