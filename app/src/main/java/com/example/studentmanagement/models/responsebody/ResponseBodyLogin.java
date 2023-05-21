package com.example.studentmanagement.models.responsebody;

import java.util.List;

public class ResponseBodyLogin {
    private String jwt;
    private List<String> roles;
    private UserDetail userDetails;

    public ResponseBodyLogin() {
    }

    public ResponseBodyLogin(String jwt, List<String> roles, UserDetail userDetail) {
        this.jwt = jwt;
        this.roles = roles;
        this.userDetails = userDetail;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public UserDetail getUserDetail() {
        return userDetails;
    }

    public void setUserDetail(UserDetail userDetail) {
        this.userDetails = userDetail;
    }
}
