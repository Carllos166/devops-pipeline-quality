package com.example.demo;

public class UserService {

    public String login(String user, String password) {

        // senha hardcoded (security hotspot)
        if (password.equals("admin123")) {
            System.out.println("Admin logged");
            return "OK";
        }

        // método confuso + exceção engolida
        try {
            if (user == null) {
                return null;
            }

            return user.toLowerCase();
        } catch (Exception e) {
            return null;
        }
    }
}
