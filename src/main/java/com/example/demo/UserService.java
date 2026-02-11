package com.example.demo;

import java.util.Locale;
import java.util.logging.Logger;

public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    public String login(String user, String password) {
        String adminPassword = System.getenv("ADMIN_PASSWORD");

        // validação básica (evita NPE e comportamento estranho)
        if (password == null || password.isBlank()) {
            logger.warning("Auth failed: empty password");
            return "UNAUTHORIZED";
        }

        // admin login (segredo vem do ambiente/CI)
        if (adminPassword != null && !adminPassword.isBlank() && adminPassword.equals(password)) {
            logger.info("Admin authenticated");
            return "OK";
        }

        // regra simples para usuário comum
        if (user == null || user.isBlank()) {
            return "INVALID_USER";
        }

        return user.toLowerCase(Locale.ROOT);
    }
}
