package mglowinski.library.model;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by macglo on 25.09.17.
 */
@Data
public class Borrow implements Serializable {

    private String borrow_id;
    private String user_id;
    private Book book;
    private String date_borrow;
}
