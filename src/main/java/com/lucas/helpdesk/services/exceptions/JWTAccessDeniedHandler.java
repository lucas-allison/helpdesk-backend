package com.lucas.helpdesk.services.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JWTAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        long date = System.currentTimeMillis();

        String json = "{"
                + "\"timestamp\": " + date + ", "
                + "\"status\": 403, "
                + "\"error\": \"Acesso Negado\", "
                + "\"message\": \"Você não tem permissão para acessar este recurso.\", "
                + "\"path\": \"" + request.getRequestURI() + "\"}";

        response.getWriter().write(json);
    }
}