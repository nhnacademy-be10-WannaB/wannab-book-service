package shop.wannab.book_service.book.dto;

public interface BookInfoForOrderProjection {
    long getBookId();
    String getTitle();
    int getOriginPrice();
    int getSalesPrice();
}
