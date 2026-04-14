package com.SneakySolo.nearfix.service;

import com.SneakySolo.nearfix.domain.user.Role;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SessionService {

    public Integer getUserId (HttpSession session) {
        return (Integer) session.getAttribute("USER_ID");
    }

    public Role getUserRole (HttpSession session) {
        return (Role) session.getAttribute("USER_ROLE");
    }

    public String getUserName (HttpSession session) {
        return (String) session.getAttribute("USER_NAME");
    }

    public boolean isLoggedIn (HttpSession session) {
        if (session.getAttribute("USER_ID") != null) {
            return true;
        }
        return false;
    }

    public void requireLogin (HttpSession session, HttpServletResponse response) throws IOException {
        if (!isLoggedIn(session)) {
            response.sendRedirect("/auth/login");
        }
    }
}
