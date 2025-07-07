package com.myapp.facturation.service.auth;

import com.myapp.facturation.model.User;

public interface AuthService {
    User register(String username, String password);
    User authenticate(String username, String password);
    User save(User user);
}
