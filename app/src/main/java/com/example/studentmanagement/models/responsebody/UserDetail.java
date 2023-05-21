package com.example.studentmanagement.models.responsebody;

public class UserDetail {
    private String id;
    private String email;
    private String userFullName;
    private String idLogin;
    public UserDetail() {
    }

    public UserDetail(String id, String email, String userFullName, String idLogin) {
        this.id = id;
        this.email = email;
        this.userFullName = userFullName;
        this.idLogin = idLogin;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getIdLogin() {
        return idLogin;
    }

    public void setIdLogin(String idLogin) {
        this.idLogin = idLogin;
    }
}
