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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public String getUserIdentityCardNumber() {
        return userIdentityCardNumber;
    }

    public void setUserIdentityCardNumber(String userIdentityCardNumber) {
        this.userIdentityCardNumber = userIdentityCardNumber;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public User(String userId, String userName, String userEmail, String userPassword) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

}