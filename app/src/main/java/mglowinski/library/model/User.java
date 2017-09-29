package mglowinski.library.model;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by macglo on 19.09.17.
 */
@Data
public class User implements Serializable {

    private String userId;
    private String userName;
    private String userSurname;
    private String userIdentityCardNumber;
    private String userEmail;
    private String userPassword;

    public User(String userId, String userName, String userEmail, String userPassword) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

}