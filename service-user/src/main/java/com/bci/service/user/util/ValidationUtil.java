package com.bci.service.user.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
@Component
public class ValidationUtil {

    private final Pattern emailPattern;
    private final Pattern passwordPattern;

    public ValidationUtil(
            @Value("${app.email.regex.regexp}") String emailRegex,
            @Value("${app.password.regex.regexp}") String passwordRegex) {

        this.emailPattern = Pattern.compile(emailRegex);
        this.passwordPattern = Pattern.compile(passwordRegex);
    }

    public boolean isValidEmail(String email) {
        return email != null && emailPattern.matcher(email).matches();
    }

    public boolean isValidPassword(String password) {
        return password != null && passwordPattern.matcher(password).matches();
    }
}
