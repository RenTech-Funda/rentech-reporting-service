package com.floweytech.agrotrack.reports.interfaces.rest.resources;

import com.floweytech.agrotrack.reports.domain.model.valueobjects.ReportMetrics;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Report Resource
 * @summary
 * Represents the detailed read-only view of a generated report exposed via the REST API.
 * It contains all the essential information about the report, including its identification,
 * status, context (plot and organization), the specific metric analyzed, and the
 * calculated statistical results (metrics) for the defined period.
 *
 * @param id The unique identifier of the report.
 * @param status The current status of the report (e.g., GENERATED).
 * @param plotId The identifier of the plot associated with the report.
 * @param organizationId The identifier of the organization owning the plot.
 * @param type The scope type of the report (e.g., PARCEL).
 * @param metricType The type of environmental metric analyzed (e.g., TEMPERATURE).
 * @param reportMetrics The calculated statistical data (average, min, max, count).
 * @param periodStart The start date of the analysis period.
 * @param periodEnd The end date of the analysis period.
 * @param generatedAt The exact timestamp when the report was generated.
 *
 * @author FloweyTech developer team
 */
public record ReportResource(
        Long id,
        String status,
        Long plotId,
        Long organizationId,
        String type,
        String metricType,
        ReportMetrics reportMetrics,
        LocalDate periodStart,
        LocalDate periodEnd,
        LocalDateTime generatedAt
) {
}
