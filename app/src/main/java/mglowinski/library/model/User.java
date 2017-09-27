package mglowinski.library.model;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by macglo on 19.09.17.
 */
@Data
public class User implements Serializable {

    private String user_id;
    private String user_name;
    private String user_surname;
    private String user_identityCardNumber;
    private String user_email;
    private String user_password;

    public User(String user_id, String user_name, String user_email, String user_password) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_password = user_password;
    }

}