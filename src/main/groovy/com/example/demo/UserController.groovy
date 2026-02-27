package com.example.demo

class UserController {

    def userService = new UserService()

    String login(String user, String password) {
        userService.login(user, password)
    }

}

