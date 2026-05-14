package com.floweytech.agrotrack.reports.domain.model.valueobjects;

/**
 * Metric Type Enumeration
 * @summary
 * Defines the specific type of environmental variable being analyzed in a report.
 * This ensures type safety when requesting calculations for specific sensors
 * (e.g., separating Temperature analysis from Humidity).
 *
 * @author FloweyTech developer team
 */
public enum MetricType {
    TEMPERATURE,
    HUMIDITY,
    PH_LEVEL
}
