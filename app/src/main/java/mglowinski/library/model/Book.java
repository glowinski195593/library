package mglowinski.library.model;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by macglo on 20.09.17.
 */
@Data
public class Book implements Serializable {

    private String book_id;
    private String book_title;
    private String book_author;
    private String book_isbn;
    private String book_description;
    private String book_category;
}
