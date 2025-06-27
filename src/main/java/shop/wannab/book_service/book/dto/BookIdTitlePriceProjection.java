package shop.wannab.book_service.book.dto;

public interface BookIdTitlePriceProjection {
    long getBookId();
    String getTitle();
    int getSalesPrice();
}
