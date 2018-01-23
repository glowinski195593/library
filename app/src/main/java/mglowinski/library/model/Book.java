package mglowinski.library.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by macglo on 20.09.17.
 */
public class Book implements Serializable {

    private String bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private String bookDescription;
    private List<Category> bookCategory;
    private String bookPublicationYear;

    public Book(String bookId, String bookTitle, String bookAuthor, String bookIsbn, String bookDescription, List<Category> bookCategory, String bookPublicationYear) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookIsbn = bookIsbn;
        this.bookDescription = bookDescription;
        this.bookCategory = bookCategory;
        this.bookPublicationYear = bookPublicationYear;
    }

    public String getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public String getBookDescription() {
        return bookDescription;
    }

    public List<Category> getBookCategory() {
        return bookCategory;
    }

    public String getBookPublicationYear() {
        return bookPublicationYear;
    }
}
