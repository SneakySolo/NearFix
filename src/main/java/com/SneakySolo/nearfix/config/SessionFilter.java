package com.SneakySolo.nearfix.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class SessionFilter implements Filter {

    private static final List<String> PUBLIC_URLS = List.of(
            "/",
            "/auth/login",
            "/auth/register",
            "/css/",
            "/uploads/",
            "/location/update"
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI();
        boolean isPublic = PUBLIC_URLS.stream().anyMatch(path::startsWith);

        if (isPublic) {
            chain.doFilter(req, res);
            return;
        }

        Object user = request.getSession().getAttribute("USER_ID");
        if (user == null) {
            response.sendRedirect("/auth/login");
        } else {
            chain.doFilter(req, res);
        }
    }
}