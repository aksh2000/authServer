package com.quinbay.authServer.dataModels.requests;


import java.util.List;

public class RegisterRequest {



    private String username;
    private String password;


    // Values will not be stored in the postgres database //
    // -------------------------------------------------- //
    public List<String> roles;
    public List<String> interests;
    // -------------------------------------------------- //


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public CustomUser toAuthenticationRequest(){
        return new CustomUser(this.username, this.password);
    }
}
