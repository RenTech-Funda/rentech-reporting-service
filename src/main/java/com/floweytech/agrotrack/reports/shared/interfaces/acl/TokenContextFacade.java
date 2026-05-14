package com.floweytech.agrotrack.reports.shared.interfaces.acl;

import com.floweytech.agrotrack.reports.shared.infrastructure.tokens.jwt.BearerTokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * Token Context Facade for extracting token information from HTTP requests
 * This is part of the ACL (Anti-Corruption Layer) for shared context
 */
@Service
public class TokenContextFacade {
    private final BearerTokenService bearerTokenService;

    public TokenContextFacade(BearerTokenService bearerTokenService) {
        this.bearerTokenService = bearerTokenService;
    }

    /**
     * Extract user ID from JWT token in the request
     * @param request HTTP servlet request
     * @return User ID from token
     * @throws IllegalArgumentException if JWT token is missing
     */
    public Long extractUserIdFromToken(HttpServletRequest request) {
        String token = bearerTokenService.getBearerTokenFrom(request);
        if (token == null) {
            throw new IllegalArgumentException("JWT token is missing");
        }
        return bearerTokenService.getUserIdFromToken(token);
    }

    /**
     * Extract user role from JWT token in the request
     * @param request HTTP servlet request
     * @return User role from token
     * @throws IllegalArgumentException if JWT token is missing
     */
    public String extractUserRoleFromRequest(HttpServletRequest request) {
        String token = bearerTokenService.getBearerTokenFrom(request);
        if (token == null) {
            throw new IllegalArgumentException("JWT token is missing");
        }
        return bearerTokenService.getRoleFromToken(token);
    }
}

