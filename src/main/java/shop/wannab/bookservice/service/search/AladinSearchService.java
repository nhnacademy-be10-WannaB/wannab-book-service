package shop.wannab.bookservice.service.search;

import shop.wannab.bookservice.service.search.dto.AladinBook;
import shop.wannab.bookservice.service.search.dto.AladinResponse;

public interface AladinSearchService {
    /**
     * ISBN 으로 알라딘에 조회해서, 없으면 null 또는 Optional.empty()
     */
    AladinBook searchByIsbn(String isbn);

    AladinResponse searchByTitle(String query);
}
