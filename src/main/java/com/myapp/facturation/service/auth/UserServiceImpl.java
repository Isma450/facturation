package com.myapp.facturation.service.auth;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.myapp.facturation.model.User;

import jakarta.servlet.http.HttpSession;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public User getConnectedUser() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(false);
        
        if (session != null) {
            User user = (User) session.getAttribute("currentUser");
            if (user != null) {
                return user;
            }
        }
        
        throw new RuntimeException("Aucun utilisateur connecté");
    }
}
