package shop.wannab.book_service.aladin.domain;

public class AladinEnums {
    public enum QueryType {
        Keyword("제목, 저자"), Title("제목"), Author("저자"), Publisher("출판사");
        public final String description;

        QueryType(String description) {
            this.description = description;
        }
    }

    public enum SearchTarget {
        Book("도서"), Foreign("외국 도서"), Music("음반"), DVD("DVD"), Used("중고"), eBook("전자책"), All("무관");
        public final String description;

        SearchTarget(String description) {
            this.description = description;
        }
    }

    public enum SortOption {
        Accuracy("관련도"), PublishTime("출간일"), Title("제목"), SalesPoint("판매량"), CustomerRating("고객평점");
        public final String description;

        SortOption(String description) {
            this.description = description;
        }
    }

    public enum CoverOption {
        Big("200px"), MidBig("150px"), Mid("85px"), Small("75px"), Mini("65px"), None("0px");
        public final String description;

        CoverOption(String description) {
            this.description = description;
        }
    }
}
