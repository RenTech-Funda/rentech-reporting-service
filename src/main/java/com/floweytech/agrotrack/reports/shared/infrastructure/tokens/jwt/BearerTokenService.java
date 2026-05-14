package com.floweytech.agrotrack.reports.shared.infrastructure.tokens.jwt;

import jakarta.servlet.http.HttpServletRequest;

public interface BearerTokenService extends TokenService {
    String getBearerTokenFrom(HttpServletRequest request);
}