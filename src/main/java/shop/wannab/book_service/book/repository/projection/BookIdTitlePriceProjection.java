package shop.wannab.book_service.book.repository.projection;

public interface BookIdTitlePriceProjection {
    Long getBookId();
    String getTitle();
    Integer getSalesPrice();
}
