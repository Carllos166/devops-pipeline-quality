package com.example.demo;

public class UserController {

    private UserService userService = new UserService();

    public String login(String user, String password) {
        return userService.login(user, password);
    }

}
