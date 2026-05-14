package com.floweytech.agrotrack.reports.domain.model.commands;

import com.floweytech.agrotrack.reports.domain.model.valueobjects.MetricType;
import com.floweytech.agrotrack.reports.domain.model.valueobjects.ReportType;
import com.floweytech.agrotrack.reports.shared.domain.model.valueobjects.OrganizationId;
import com.floweytech.agrotrack.reports.shared.domain.model.valueobjects.PlotId;
import com.floweytech.agrotrack.reports.shared.domain.model.valueobjects.ProfileId;

import java.time.LocalDate;

/**
 * Create Report Command
 * @summary
 * CreateReportCommand is a record class that represents the command to create a report.
 */
public record CreateReportCommand(
        ProfileId profileId,
        PlotId plotId,
        OrganizationId organizationId,
        ReportType type,
        MetricType metricType,
        LocalDate periodStart,
        LocalDate periodEnd
) {
    /**
     * Validates the command
     */
    public CreateReportCommand {
        if(plotId == null )
            throw new IllegalArgumentException("plotId cannot be null");
        if(organizationId == null )
            throw new IllegalArgumentException("organizationId cannot be null");
        if(type == null)
            throw new IllegalArgumentException("type cannot be null");
        if(periodStart == null )
            throw new IllegalArgumentException("periodStart cannot be null");
        if(periodEnd == null )
            throw new IllegalArgumentException("periodEnd cannot be null");
        if(metricType == null)
            throw new IllegalArgumentException("metricType cannot be null");

    }


}
