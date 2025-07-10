package shop.wannab.book_service.book.dto;

public interface BookIdTitlePriceProjection {
    Long getBookId();
    String getTitle();
    Integer getSalesPrice();
}
