package mglowinski.library.model;

import java.io.Serializable;

/**
 * Created by macglo on 19.09.17.
 */
public class User implements Serializable {

    private String userId;
    private String userName;
    private String userSurname;
    private String userIdentityCardNumber;
    private String userEmail;
    private String userPassword;

    public User(String userId, String userName, String userSurname, String userIdentityCardNumber, String userEmail, String userPassword) {
        this.userId = userId;
        this.userName = userName;
        this.userSurname = userSurname;
        this.userIdentityCardNumber = userIdentityCardNumber;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public String getUserEmail() {
        return userEmail;
    }
}