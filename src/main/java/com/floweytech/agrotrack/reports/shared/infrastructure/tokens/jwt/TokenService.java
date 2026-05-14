package com.floweytech.agrotrack.reports.shared.infrastructure.tokens.jwt;

import jakarta.servlet.http.HttpServletRequest;

public interface TokenService {
    String getUsernameFromToken(String token);
    Long getUserIdFromToken(String token);
    String getRoleFromToken(String token);
    boolean validateToken(String token);
    String getBearerTokenFrom(HttpServletRequest request);
}