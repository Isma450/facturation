package com.myapp.facturation.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.myapp.facturation.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        
        // Pages publiques 
        if (path.equals("/login") || path.equals("/register") || 
            path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/")) {
            return true;
        }
        
        HttpSession session = request.getSession(false);
        User currentUser = null;
        
        if (session != null) {
            currentUser = (User) session.getAttribute("currentUser");
        }
        
        if (currentUser == null) {
            response.sendRedirect("/login");
            return false;
        }
        
        // Ajouter l'utilisateur à la requête pour y accéder dans les contrôleurs
        request.setAttribute("currentUser", currentUser);
        return true;
    }
}
