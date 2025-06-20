package shop.wannab.book_service.entity.dto;

public interface BookInfoForOrderProjection {
    long getBookId();
    String getTitle();
    int getOriginPrice();
    int getSalesPrice();
}
