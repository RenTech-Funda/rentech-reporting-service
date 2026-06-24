package com.floweytech.agrotrack.reports.shared.interfaces.acl;

import com.floweytech.agrotrack.reports.shared.interfaces.rest.resources.ReadingResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * Monitoring Context Facade Implementation
 * @summary
 * Implementación real del ACL hacia el Monitoring Service.
 * Reemplaza el stub anterior (que siempre retornaba lista vacía) por una
 * llamada HTTP real al monitoring-service para obtener las lecturas
 * ambientales necesarias para calcular las métricas del reporte.
 *
 * Consume el endpoint:
 *   GET /api/v1/environment-readings/plot/{plotId}/filter
 *        ?type=TEMPERATURE&start=2024-01-01T00:00:00&end=2024-01-31T23:59:59
 */
@Service
public class MonitoringContextFacadeImpl implements MonitoringContextFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringContextFacadeImpl.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final RestClient restClient;

    public MonitoringContextFacadeImpl(
            @Value("${services.monitoring.url:http://localhost:8084}") String monitoringServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(monitoringServiceUrl)
                .build();
    }

    /**
     * Obtiene las lecturas ambientales desde el monitoring-service
     * filtradas por plotId, tipo de lectura y rango de fechas.
     * Si el servicio no está disponible o no hay datos, retorna lista vacía
     * para que el reporte se genere con métricas en cero (fail-safe).
     */
    @Override
    public List<ReadingResource> fetchReadingsByPlotAndTypeAndPeriod(
            Long plotId,
            String readingType,
            LocalDateTime start,
            LocalDateTime end) {
        try {
            List<ReadingResource> readings = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/environment-readings/plot/{plotId}/filter")
                            .queryParam("type", readingType)
                            .queryParam("start", start.format(FORMATTER))
                            .queryParam("end", end.format(FORMATTER))
                            .build(plotId))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (readings == null) return Collections.emptyList();

            LOGGER.info("Fetched {} readings for plot {} type {} from monitoring-service",
                    readings.size(), plotId, readingType);
            return readings;

        } catch (Exception e) {
            LOGGER.warn("Could not fetch readings from monitoring-service for plot {}: {}",
                    plotId, e.getMessage());
            return Collections.emptyList();
        }
    }
}