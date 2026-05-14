package com.floweytech.agrotrack.reports.domain.model.valueobjects;

/**
 * Report Metrics Value Object
 * @summary
 * Represents a statistical snapshot of environmental readings for a specific report period.
 * It contains calculated values such as average, maximum, minimum, and the data count,
 * serving as an immutable record of the plot's performance.
 *
 * @author FloweyTech developer team
 */
public record ReportMetrics(Double averageValue,
                            Double maxValue,
                            Double minValue,
                            Integer dataCount
) {

    public static ReportMetrics empty() {
        return new ReportMetrics(0.0, 0.0, 0.0, 0);
    }
}
