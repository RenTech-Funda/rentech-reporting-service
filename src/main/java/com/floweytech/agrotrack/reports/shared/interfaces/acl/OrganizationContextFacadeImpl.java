package com.floweytech.agrotrack.reports.shared.interfaces.acl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

/**
 * Organization Context Facade Implementation
 * @summary
 * Implementación real del ACL hacia el Organization Service.
 * Reemplaza el stub anterior (que siempre retornaba true) por una
 * llamada HTTP real al organization-service para verificar si un
 * plot existe antes de generar un reporte.
 */
@Service
public class OrganizationContextFacadeImpl implements OrganizationContextFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationContextFacadeImpl.class);

    private final RestClient restClient;

    public OrganizationContextFacadeImpl(
            @Value("${services.organization.url:http://localhost:8083}") String organizationServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(organizationServiceUrl)
                .build();
    }

    /**
     * Verifica si un plot existe consultando el organization-service.
     * Hace GET /api/v1/plots/{plotId}:
     *   - 200 → existe, retorna true
     *   - 404 → no existe, retorna false
     *   - cualquier otro error → loguea y retorna false (fail-safe)
     */
    @Override
    public boolean existsPlotById(Long plotId) {
        try {
            restClient.get()
                    .uri("/api/v1/plots/{plotId}", plotId)
                    .headers(headers -> currentAuthorizationHeader()
                            .ifPresent(value -> headers.set(HttpHeaders.AUTHORIZATION, value)))
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                LOGGER.info("Plot {} not found in organization-service", plotId);
                return false;
            }
            LOGGER.warn("Error checking plot {} in organization-service: {}", plotId, e.getMessage());
            return false;
        } catch (Exception e) {
            LOGGER.error("Could not reach organization-service to check plot {}: {}", plotId, e.getMessage());
            return false;
        }
    }

    private java.util.Optional<String> currentAuthorizationHeader() {
        var attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            var authorization = servletRequestAttributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
            if (authorization != null && !authorization.isBlank()) {
                return java.util.Optional.of(authorization);
            }
        }
        return java.util.Optional.empty();
    }
}
