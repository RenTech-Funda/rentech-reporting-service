package com.floweytech.agrotrack.reports.interfaces.rest.resources;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Create Report Resource
 * @summary
 * Represents the data transfer object (Resource) required to initiate the creation of a new report.
 * It encapsulates the user's request details, including the report scope (type), the specific
 * environmental metric to analyze, and the time period for the analysis.
 *
 * @param type The type of report (e.g., PARCEL, GENERAL).
 * @param metricType The environmental metric to analyze (e.g., TEMPERATURE, HUMIDITY).
 * @param periodStart The start date of the report period.
 * @param periodEnd The end date of the report period.
 *
 * @author FloweyTech developer team
 */
public record CreateReportResource(
        @NotBlank(message = "{report.type.required}")
        String type,

        @NotBlank(message = "{report.metric.type.required}")
        String metricType,

        @NotNull(message = "{report.period.start.required}")
        LocalDate periodStart,

        @NotNull(message = "{report.period.end.required}")
        LocalDate periodEnd
) {

}
